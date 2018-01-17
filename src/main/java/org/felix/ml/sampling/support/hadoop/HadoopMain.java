package org.felix.ml.sampling.support.hadoop;

import org.felix.ml.sampling.IConfig;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.cfg.CommonConfig;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.support.single.Process;
import org.felix.ml.sampling.util.HadoopRunjob;
import org.felix.ml.sampling.util.StringUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.felix.ml.sampling.support.hadoop.ScoreAndFilterMapper.*;
import static java.lang.String.format;

public class HadoopMain {
    private static Logger info = Logger.getLogger("info");
    private static Logger warn = Logger.getLogger("warn");

    public static void main(String[] args)
            throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        for (String argv : args) {
            System.out.println(argv + "\n");
        }
        PropertyConfigurator.configure(Process.class.getResourceAsStream("/score_log4j.properties"));

        try {
            Configuration conf = new Configuration();

            // ---- 获取用户输入参数
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
            if (otherArgs.length <= 2) {
                System.out.println("argv error");
                System.out.println("eg:input output config jobname=xxx&keytype=xxx&reducenum=?&ranges=xxx_xxx);");
                System.exit(2);
            }
            info.info("============Start process================");
            info.info(format("input:[%s] output:[%s] conf:[%s] param:[%s]", args[0], args[1], args[2], args[3]));

            String inputPath = otherArgs[0];
            String outpath = otherArgs[1];
            String config = otherArgs[2];

            String[] runArgs = otherArgs[3].split("&");
            for (String rarg : runArgs) {
                String[] keyAndVal = rarg.split("=");
                System.out.println("Get conf " + keyAndVal[0] + ":" + keyAndVal[1]);
                conf.set(keyAndVal[0], keyAndVal[1]);
            }
            String string = StringUtil.read(new FileInputStream(config));
            IConfig iconfig = new CommonConfig();
            iconfig.load(string);
            conf.set(IConfig.CONFIG_KEY, string);

            // -------- 设置job 参数 -------------
            String jobname = conf.get("jobname");
            String[] inputList = null;
            if (inputPath.contains(" ")) {
                inputList = inputPath.split(" ");
            } else if (inputPath.contains(";")) {
                inputList = inputPath.split(";");
            } else {
                inputList = new String[]{inputPath};
            }
            Job job = HadoopRunjob.RunJob(conf, inputList, outpath, jobname, HadoopMain.class,
                    ScoreAndFilterMapper.class, null, Text.class, 0);
            setFilterStats(job.getCounters());
            printFilter(iconfig);
            printError(job.getCounters());
            info.info("============End process================");
            System.exit(0);

        } catch (Exception e) {
            System.out.println("----- error! --------");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void setFilterStats(Counters counters) {
        for (IFilter filter : FilterFactory.filters()) {
            String id = filter.getId();
            long inNum = counters.findCounter(getCounterGroupKey(id), INNUM).getValue();
            long success = counters.findCounter(getCounterGroupKey(id), SUCCESS).getValue();
            long successPack = counters.findCounter(getCounterGroupKey(id), SUCCESS_PACK).getValue();
            long packageNum = counters.findCounter(getCounterGroupKey(id), PACKAGENUM).getValue();
            long warnNum = counters.findCounter(getCounterGroupKey(id), WARNNUM).getValue();
            long warnPackageNum = counters.findCounter(getCounterGroupKey(id), WARNPACKAGENUM).getValue();
            filter.setInNum(inNum);
            filter.setSuccessNum(success);
            filter.setSuccessPackNum(successPack);
            filter.setPackageNum(packageNum);
            filter.setWarnNum(warnNum);
            filter.setWarnPackageNum(warnPackageNum);
        }
    }

    public static void printFilter(IConfig iconfig) {
        if (iconfig.getInnerFilter() != null) {
            info.info("=============Inner filter Statistics================");
            info.info("\n" + iconfig.getInnerFilter().toString(""));
        }
        if (iconfig.getPreFilter() != null) {
            info.info("=============Pre-filter Statistics================");
            info.info("\n" + iconfig.getPreFilter().toString(""));
        }
        if (iconfig.getRootFilter() != null) {
            info.info("=============Root filter Statistics================");
            info.info("\n" + iconfig.getRootFilter().toString(""));
        }
    }

    public static void printError(Counters counters) {
        CounterGroup group = counters.getGroup(getCounterGroupKey(FILTER_EXCEPTION));
        Iterator<Counter> iter = group.iterator();
        while (iter.hasNext()) {
            Counter counter = iter.next();
            String msg = format("error occur for class:%s num:%s", counter.getName(), counter.getValue());
            warn.warn(msg);
        }
    }
}
