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
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 *  */
public class OrFilter extends BaseFilter {
    protected List<IFilter> filters = new ArrayList<IFilter>();

    public List<IFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<IFilter> filters) {
        this.filters = filters;
    }

    public void init(String param) throws ConfigException {
        if (StringUtils.isEmpty(param))
            throw new ConfigException(
                    String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        String[] items = param.split(",");
        for (String item : items) {
            IFilter filter = FilterFactory.getFilter(StringUtils.trim(item));
            if (filter == null)
                throw new FilterDependenceException(
                        String.format("Init %s filter error, param:%s", getClass().getSimpleName(), param));
            filters.add(filter);
        }
        if (filters.size() < 2)
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        super.init(param);
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context)
            throws FilterException {
        List<List<Integer>> all = new ArrayList<List<Integer>>();
        List<Integer> orderList = spackage.cloneFilterBefore();
        for (IFilter filter : filters) {
            List<Integer> flterResult = filter.filter(spackage, scoreResult, context);
            all.add(flterResult);
        }
        return merge(all, orderList);
    }

    protected List<Integer> merge(List<List<Integer>> all, List<Integer> orderList) {
        SortedSet<Integer> set = new TreeSet<Integer>();
        for (List<Integer> list : all)
            set.addAll(list);
        List<Integer> ret = new ArrayList<Integer>();
        for (Integer i : orderList)
            if (set.contains(i)) ret.add(i);
        return ret;
    }

    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString(prefix));
        sb.append(prefix + "start print sub filters:\n");
        for (IFilter filter : filters) {
            sb.append(filter.toString(prefix + "\t"));
        }
        sb.append(prefix + "end print sub filters:\n");
        return sb.toString();
    }
}
