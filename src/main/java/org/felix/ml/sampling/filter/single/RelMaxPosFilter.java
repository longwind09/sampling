package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.util.FilterUtil;
import org.felix.ml.sampling.util.QueryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.felix.ml.sampling.util.Constant.POS_TAG;

/**
 *
 *  */
public class RelMaxPosFilter extends PosFilter {
    private Integer label;
    private Boolean sample;

    public void init(String param) throws ConfigException {
        Map<String, String[]> map = QueryUtil.parseUrlQueryString(param);
        String[] labels = map.get("label");
        String[] samples = map.get("sample");
        if (labels == null && samples == null)
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        if (labels != null && samples != null)
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        if (labels != null) {
            if (labels.length > 1)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            try {
                label = Integer.parseInt(labels[0]);
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
        dynamic = true;
        super.init(param);
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public Boolean getSample() {
        return sample;
    }

    public void setSample(Boolean sample) {
        this.sample = sample;
    }

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        if (label != null) {
            List<Integer> selList = spackage.cloneFilterBefore();
            Map<Integer, Integer> id2pos = FilterUtil.posMap(POS_TAG, selList, spackage.getList());
            Integer maxPos = FilterUtil.findLast(label, selList, id2pos, scoreResult.getLabelMap());
            if (maxPos == null)
                return new ArrayList<Integer>();
            setPos(maxPos);
        } else if (sample != null) {
            List<Integer> selList = spackage.cloneFilterBefore();
            Map<Integer, Integer> id2pos = FilterUtil.posMap(POS_TAG, selList, spackage.getList());
            Integer maxPos = FilterUtil.findLast(sample, selList, id2pos, scoreResult.getBinMap());
            if (maxPos == null)
                return new ArrayList<Integer>();
            setPos(maxPos);
        } else {
            valid = false;
            return spackage.cloneFilterBefore();
        }
        return super.doFilter(spackage, scoreResult, context);
    }

    public void preFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) throws FilterException {
        this.pos = 9999;
        super.preFilter(spackage, scoreResult, context);
    }
}
