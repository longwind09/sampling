package org.felix.ml.sampling.filter.combo;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterDependenceException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.filter.single.RandomFixNumFilter;
import org.felix.ml.sampling.util.QueryUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 *
 *  */
public class RatioFilter extends RandomFixNumFilter {
    private IFilter filterNum;
    private IFilter filterBase;
    private float ratio = -1.0f;

    public void init(String param) throws ConfigException {
        if (StringUtils.isEmpty(param))
            throw new ConfigException(
                    String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        dynamic = true;
        try {
            Map<String, String[]> queryMap = QueryUtil.parseUrlQueryString(param);
            String[] filterBaseIds = queryMap.get("filterBaseId");
            String[] filterNumIds = queryMap.get("filterNumId");
            if (filterBaseIds == null || filterBaseIds.length != 1 ||
                    filterNumIds == null || filterNumIds.length != 1)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            String filterBaseId = filterBaseIds[0];
            String filterNumId = filterNumIds[0];
            String[] ratios = queryMap.get("ratio");
            if (ratios == null || ratios.length != 1)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            this.ratio = Float.parseFloat(ratios[0]);
            if (this.ratio < 0)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            filterBase = FilterFactory.getFilter(filterBaseId);
            if (filterBase == null)
                throw new FilterDependenceException(
                        String.format("Init %s filter error, param:%s", getClass().getSimpleName(), param));
            filterNum = FilterFactory.getFilter(filterNumId);
            if (filterNum == null)
                throw new FilterDependenceException(
                        String.format("Init %s filter error, param:%s", getClass().getSimpleName(), param));
            super.init(param);
        } catch (Exception e) {
            if (e instanceof FilterDependenceException)
                throw (FilterDependenceException) e;
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param), e);
        }
    }

    @Override
    public void preFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) throws FilterException {
        filterNum.filter(spackage, scoreResult, context);

        List<Integer> filterResult = filterBase.filter(spackage, scoreResult, context);
        spackage.setFilterBefore(filterResult);
        super.preFilter(spackage, scoreResult, context);

        this.num = ratio * context.getNum(filterNum.getId());
        if (this.num <= 0)
            valid = false;
        else valid = true;
    }

    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString(prefix));
        sb.append(prefix + "start print sub filters:\n");
        sb.append(filterBase.toString(prefix + "\t"));
        sb.append(prefix + "end print sub filters:\n");
        return sb.toString();
    }
}
