package org.felix.ml.sampling.filter.combo;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterDependenceException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.filter.FilterFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  */
public class NotFilter extends BaseFilter {
    private IFilter filter;

    public IFilter getFilter() {
        return filter;
    }

    public void setFilter(IFilter filter) {
        this.filter = filter;
    }

    public void init(String param) throws ConfigException {
        if (StringUtils.isEmpty(param))
            throw new ConfigException(
                    String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        filter = FilterFactory.getFilter(param);
        if (filter == null)
            throw new FilterDependenceException(
                    String.format("Init %s filter error, param:%s", getClass().getSimpleName(), param));
        super.init(param);
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context)
            throws FilterException {
        List<Integer> ret = new ArrayList<Integer>();
        List<Integer> filterResult = filter.filter(spackage, scoreResult, context);
        for (Integer i : spackage.getFilterBefore())
            if (!filterResult.contains(i))
                ret.add(i);
        return ret;
    }

    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString(prefix));
        sb.append(prefix + "start print sub filters:\n");
        sb.append(filter.toString(prefix + "\t"));
        sb.append(prefix + "end print sub filters:\n");
        return sb.toString();
    }
}
