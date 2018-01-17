package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *  */
public class RandomFixNumFilter extends BaseFilter {
    protected float num;
    protected boolean dynamic = false;

    public void init(String param) throws ConfigException {
        if (dynamic) {
            super.init(param);
            return;
        }
        try {
            this.num = Float.parseFloat(param);
            if (this.num <= 0)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            super.init(param);
        } catch (Exception e) {
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> clone = spackage.cloneFilterBefore();
        Collections.shuffle(clone);
        int fixNum = RandomUtil.rand(num);
        int to = clone.size() > fixNum ? fixNum : clone.size();
        clone = clone.subList(0, to);
        return reOrder(clone, spackage.getFilterBefore());
    }

    private List<Integer> reOrder(List<Integer> shuffle, List<Integer> raw) {
        List<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < raw.size(); i++) {
            if (shuffle.contains(raw.get(i)))
                ret.add(raw.get(i));
        }
        return ret;
    }
}
