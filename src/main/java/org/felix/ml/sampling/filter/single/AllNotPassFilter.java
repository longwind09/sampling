package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.filter.BaseFilter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @9
 */
public class AllNotPassFilter extends BaseFilter {
    public static String shortName = "allnotpassFilter";

    public String getShortName() {
        return shortName;
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> ret = new ArrayList<Integer>();
        return ret;
    }
}
