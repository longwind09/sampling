package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.filter.BaseFilter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  */
public class BuiltInFilter extends BaseFilter {

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> fails = scoreResult.getFails();
        List<Integer> ret = new ArrayList<Integer>();
        for (Integer i : spackage.getFilterBefore()) {
            if (!fails.contains(i))
                ret.add(i);
        }
        return ret;
    }

}
