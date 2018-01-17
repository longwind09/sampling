package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.util.QueryUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *  */
public class ClassFilter extends BaseFilter {
    public static String shortName = "classFilter";
    private List<Integer> label = new ArrayList<Integer>();
    private Boolean sample;

    public void init(String param) throws ConfigException {
        if (StringUtils.isEmpty(param))
            throw new ConfigException(
                    String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        Map<String, String[]> map = QueryUtil.parseUrlQueryString(param);
        String[] labels = map.get("label");
        String[] samples = map.get("sample");
        if (labels == null && samples == null)
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        if (labels != null) {
            try {
                for (int i = 0; i < labels.length; i++)
                    label.add(Integer.parseInt(labels[i]));
            } catch (Exception e) {
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            }
        }
        if (samples != null) {
            if (samples.length > 1)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            try {
                sample = Boolean.parseBoolean(samples[0]);
            } catch (Exception e) {
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            }
        }
        super.init(param);
    }

    public List<Integer> getLabel() {
        return label;
    }

    public void setLabel(List<Integer> label) {
        this.label = label;
    }

    public Boolean getSample() {
        return sample;
    }

    public void setSample(Boolean sample) {
        this.sample = sample;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> cloneList = spackage.cloneFilterBefore();
        boolean warnFlag = false;
        for (int i = cloneList.size() - 1; i >= 0; i--) {
            int id = cloneList.get(i);
            if (label.size() > 0) {
                Integer rowLabel = scoreResult.getLabel(id);
                if (rowLabel == null) {
                    warnNum++;
                    warnFlag = true;
                    cloneList.remove(i);
                    continue;
                }
                if (!label.contains(rowLabel)) {
                    cloneList.remove(i);
                    continue;
                }
            }
            if (sample != null) {
                Boolean rowSample = scoreResult.getBin(id);
                if (rowSample == null) {
                    warnNum++;
                    warnFlag = true;
                    cloneList.remove(i);
                    continue;
                }
                if (sample.booleanValue() != rowSample) {
                    cloneList.remove(i);
                    continue;
                }
            }
        }
        if (warnFlag)
            warnPackageNum++;
        return cloneList;
    }

}
