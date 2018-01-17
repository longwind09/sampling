package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.BaseFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @0
 */
public class RandomFilter extends BaseFilter {
    private float rate = 0.1f;
    private Random r = new Random();

    public void init(String param) throws ConfigException {
        try {
            this.rate = Float.parseFloat(param);
            if (this.rate <= 0 || this.rate > 1)
                throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            super.init(param);
        } catch (Exception e) {
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> ret = new ArrayList<Integer>();
        for (Integer id : spackage.getFilterBefore()) {
            float f = r.nextFloat();
            if (f < rate)
                ret.add(id);
        }
        return ret;
    }
}
