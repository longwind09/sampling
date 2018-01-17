package org.felix.ml.sampling.support.single;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.felix.ml.sampling.*;
import org.felix.ml.sampling.cfg.CommonConfig;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.util.Constant;
import org.felix.ml.sampling.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 *
 *   单机主函数， 可以用来线下做单元测试
 *  */
public class Process {
    //??
    private static final int mod = 10000;
    private static Logger info = Logger.getLogger("info");
    private static Logger warn = Logger.getLogger("warn");
    private static int offset = 0;
    private static int packId = 0;
    private static ITag tagService;

    private static IFilter innerFilter = null;
    private static IFilter preFilter = null;
    private static IFilter rootFilter = null;

    private static FilterContext context = new FilterContext();
    private static IRowConvert rowConvert = null;
    private static IConfig config;

    public static void process(LineIterator iter, FileWriter out) throws IOException {
        List<String> lines = new ArrayList<String>();
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
        String qid = null;
        innerFilter = config.getInnerFilter();
        preFilter = config.getPreFilter();
        rootFilter = config.getRootFilter();
        tagService = config.getTag();
        rowConvert = config.getRowConvert();
        int lineNo = 0;
        while (iter.hasNext()) {
            String line = iter.next();
            lineNo++;
            if (lineNo % mod == 0)
                info.info(format("process at %s", lineNo));
            if (rowConvert != null)
                try {
                    line = rowConvert.convert(line);
                } catch (ConvertException e) {
                    e.printStackTrace();
                    continue;
                }
            Map<String, String> strMap = StringUtil.str2Map(line);
            String queryTag = StringUtil.getValueWithDefault(config.getProperty("qid"), Constant.QUERY_TAG);
//            info.info("qid name in sample is-->"+queryTag);
            String v = strMap.get(queryTag);
            if (StringUtils.isEmpty(v) || strMap.size() == 0) {
                warn.warn(format("Wrong line: %s", line));
            }
            if (qid == null) {
                qid = v;
            }
            //do combine
            if (qid.equals(v)) {
                lines.add(line);
                maps.add(strMap);
                continue;
            }
            //do reduce
            else {
                processPackage(lines, maps, out);
                qid = v;
                lines.clear();
                maps.clear();
                context.reset();
                lines.add(line);
                maps.add(strMap);
                offset = lineNo;
            }
        }
        //last reduce
        if (lines.size() > 0) {
            processPackage(lines, maps, out);
        }
        if (preFilter != null)
            info.info("\n" + preFilter.toString(""));
        info.info("\n" + rootFilter.toString(""));
    }

    private static void processPackage(List<String> lines, List<Map<String, String>> maps, FileWriter out)
            throws IOException {
        if (lines.size() == 0)
            return;
        ScoreResult scoreResult = tagService.score(maps);
        context.reset();
        SamplePackage spackage = new SamplePackage(maps, lines);
        if (preFilter != null) {
            List<Integer> list;
            try {
                list = preFilter.filter(spackage, scoreResult, context);
                spackage.setFilterBefore(list);
            } catch (FilterException e) {
                warn.error(e);
            }
        }
        List<Integer> validList;
        try {
            validList = innerFilter.filter(spackage, scoreResult, context);
            spackage.setFilterBefore(validList);
        } catch (FilterException e) {
            warn.error(e);
        }
        List<Integer> list;
        try {
            list = rootFilter.filter(spackage, scoreResult, context);
            out(lines, list, scoreResult, out);
        } catch (FilterException e) {
            warn.error(e);
        }
    }

    public static void out(List<String> lines, List<Integer> list, ScoreResult scoreResult, FileWriter out)
            throws IOException {
        packId++;
        if (list.size() > lines.size())
            warn.warn(format("wrong size lines:%s filter:%s", lines.size(), list.size()));
        for (Integer id : list) {
            String line = lines.get(id);
            if (config.getOut().equals(IConfig.Out.LABEL)) {
                Integer label = scoreResult.getLabel(id);
                if (label != null)
                    out.write(format("%s_%s %s %s\n", packId, id + offset, label, line));
            } else if (config.getOut().equals(IConfig.Out.SAMPLE)) {
                Boolean sample = scoreResult.getBin(id);
                if (sample != null)
                    out.write(format("%s_%s %s\t%s\n", packId, id + offset, sample ? 1 : 0, line));
            }
        }
    }

    public static void usage() {
        System.out.println("Usage: java -jar score_and_filter.jar configure inputFile outputFile");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.exit(-1);
        }
        PropertyConfigurator.configure(Process.class.getResourceAsStream("/score_log4j.properties"));
        String cnf = args[0];
        String inFile = args[1];
        String outFile = args[2];
        InputStream in = new FileInputStream(inFile);
        FileWriter out = new FileWriter(outFile);
        try {
            config = new CommonConfig();
            config.load(new File(cnf));
        } catch (ConfigException e) {
            e.printStackTrace();
            warn.error(e);
        }
        info.info("============Start process================");
        LineIterator lineIter = IOUtils.lineIterator(in, "UTF-8");
        process(lineIter, out);
        info.info("============End process================");
        in.close();
        out.close();
    }
}
