package org.felix.ml.sampling.support.hadoop;

import org.felix.ml.sampling.*;
import org.felix.ml.sampling.cfg.CommonConfig;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.util.Constant;
import org.felix.ml.sampling.util.HadoopRunjob;
import org.felix.ml.sampling.util.StringUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * 打算把雍坤这里的一个mapper拆成mapper和reducer
 * 从而不依赖上游的qid一定满足聚合条件
 */
public class ScoreAndFilterMapper extends Mapper<Object, Text, Text, Text> {
    static final String SUCCESS = "_success";
    static final String SUCCESS_PACK = "_success_pack";
    static final String INNUM = "_in";
    static final String PACKAGENUM = "_package";
    static final String WARNNUM = "_warnnum";
    static final String WARNPACKAGENUM = "_warnpackage";

    static final String FILTER_EXCEPTION = "filterException";
    private List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
    private Map<String, Long> errorMap = new HashMap<String, Long>();
    private IConfig iconfig = new CommonConfig();
    private ITag tagService;
    private IFilter innerFilter;
    private IFilter preFilter;
    private IFilter rootFilter;
    private IRowConvert rowConvert;
    private FilterContext filterContext = new FilterContext();
    private String qid = null;
    private List<String> lines = new ArrayList<String>();

    static String getCounterGroupKey(String key) {
        return String.format("%s%s", HadoopRunjob.userGrop_frefix, key);
    }

    protected void setup(Context context) throws IOException, InterruptedException {
        String in = context.getConfiguration().get(IConfig.CONFIG_KEY);
        try {
            iconfig.load(in);
            innerFilter = iconfig.getInnerFilter();
            tagService = iconfig.getTag();
            preFilter = iconfig.getPreFilter();
            rootFilter = iconfig.getRootFilter();
            rowConvert = iconfig.getRowConvert();
        } catch (ConfigException e) {
            throw new InterruptedException("error config!");
        }
    }

    @Override
    protected void cleanup(Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        try {
            processPackage(lines, maps, context);
        } catch (Exception e) {
            processError(e);
        }
        super.cleanup(context);
        for (IFilter filter : FilterFactory.filters())
            increase(filter, context);
        increaseError(context);
    }

    private void increase(IFilter filter, Context context) {
        String filterId = filter.getId();
        increase(filterId, SUCCESS, filter.getSuccessNum(), context);
        increase(filterId, SUCCESS_PACK, filter.getSuccessPackNum(), context);
        increase(filterId, INNUM, filter.getInNum(), context);
        increase(filterId, PACKAGENUM, filter.getPackageNum(), context);
        increase(filterId, WARNNUM, filter.getWarnNum(), context);
        increase(filterId, WARNPACKAGENUM, filter.getWarnPackageNum(), context);
    }

    private void increase(String filterId, String suffix, long num, Context context) {
        Counter counter = context.getCounter(getCounterGroupKey(filterId), suffix);
        counter.increment(num);
    }

    private void increaseError(Context context) {
        for (Map.Entry<String, Long> entry : errorMap.entrySet()) {
            Counter counter = context.getCounter(getCounterGroupKey(FILTER_EXCEPTION), entry.getKey());
            counter.increment(entry.getValue());
        }
    }

    private void processError(Exception e) {
        String key = e.getClass().getName();
        Long num = errorMap.get(e);
        if (num == null) {
            errorMap.put(key, 1L);
        } else {
            errorMap.put(key, num++);
        }
    }

    /**
     * map operation
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        try {
            safeMap(key, value, context);
        } catch (IOException | InterruptedException e) {
            throw e;
        } catch (Exception e) {
            processError(e);
        }
    }

    /**
     * operation with exception captured
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     * @throws ConvertException
     */
    private void safeMap(Object key, Text value, Context context) throws IOException, InterruptedException, ConvertException {
        String line = value.toString();
        if (rowConvert != null) {
            line = rowConvert.convert(line);
        }
        Map<String, String> strMap = StringUtil.str2Map(line);
        String qid_key = StringUtil.getValueWithDefault(iconfig.getProperty("qid"), Constant.QUERY_TAG);
        String v = strMap.get(qid_key);
        if (isEmpty(v) || strMap.size() == 0) {
            return;
        }
        if (qid == null) {
            qid = v;
        }
        if (qid.equals(v)) {
            lines.add(line);
            maps.add(strMap);
        } else {
            processPackage(lines, maps, context);
            qid = v;
            lines.clear();
            maps.clear();
            filterContext.reset();
            lines.add(line);
            maps.add(strMap);
        }
    }

    private void processPackage(List<String> lines, List<Map<String, String>> maps, Context context)
            throws IOException, InterruptedException {
        if (lines.size() == 0)
            return;
        ScoreResult scoreResult = tagService.score(maps);
        filterContext.reset();
        SamplePackage spackage = new SamplePackage(maps, lines);
        if (preFilter != null) {
            List<Integer> list;
            try {
                list = preFilter.filter(spackage, scoreResult, filterContext);
                spackage.setFilterBefore(list);
            } catch (FilterException e) {
                processError(e);
            }
        }
        List<Integer> validList;
        try {
            validList = innerFilter.filter(spackage, scoreResult, filterContext);
            spackage.setFilterBefore(validList);
        } catch (FilterException e) {
            processError(e);
        }
        List<Integer> list;
        try {
            list = rootFilter.filter(spackage, scoreResult, filterContext);
//            out(lines, list, scoreResult, context);
            out_put(maps, list, scoreResult, context);
        } catch (FilterException e) {
            processError(e);
        }
    }

    /**
     * 代替out，区别是这个输出的行是完全由特征编号：特征值拼接起来的，前面加上label
     *
     * @param maps
     * @param list
     * @param scoreResult
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    private void out_put(List<Map<String, String>> maps, List<Integer> list, ScoreResult scoreResult, Context context)
            throws IOException, InterruptedException {
        for (Integer id : list) {
            String line = StringUtil.map2Str(maps.get(id));
            if (iconfig.getOut().equals(IConfig.Out.LABEL)) {
                Integer label = scoreResult.getLabel(id);
                if (label != null) {
                    String out = label + " " + trim(line);
                    context.write(new Text(out), null);
                }
            } else if (iconfig.getOut().equals(IConfig.Out.SAMPLE)) {
                Boolean sample = scoreResult.getBin(id);
                if (sample != null) {
                    String out = (sample ? "1" : "0") + " " + trim(line);
                    context.write(new Text(out), null);
                }
            }
        }
    }

    /**
     * 输出的行是由输入的行替换其中的action map 加上label
     *
     * @param lines
     * @param list
     * @param scoreResult
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void out(List<String> lines, List<Integer> list, ScoreResult scoreResult, Context context)
            throws IOException, InterruptedException {
        for (Integer id : list) {
            String line = lines.get(id);
            if (iconfig.getOut().equals(IConfig.Out.LABEL)) {
                Integer label = scoreResult.getLabel(id);
                if (label != null) {
                    String out = label + " " + trim(line);
                    context.write(new Text(out), new Text(""));
                }
            } else if (iconfig.getOut().equals(IConfig.Out.SAMPLE)) {
                Boolean sample = scoreResult.getBin(id);
                if (sample != null) {
                    String out = (sample ? "1" : "0") + " " + trim(line);
                    context.write(new Text(out), new Text(""));
                }
            }
        }
    }
}
