package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.filter.BaseFilter;

import java.util.List;

/**
 *
 * @0
 *          全通过.
 */
public class AllPassFilter extends BaseFilter {
    public static String shortName = "allpassFilter";

    public String getShortName() {
        return shortName;
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        return spackage.cloneFilterBefore();
    }
}
