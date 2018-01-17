package org.felix.ml.sampling.filter.single;


import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ExpreException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.util.Constant;
import org.felix.ml.sampling.util.ExpressionUtil;
import org.felix.ml.sampling.util.FilterUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 *
 *  */
public class ELFilter extends BaseFilter {
    public String expresion;

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    public void init(String param) throws ConfigException {
        if (StringUtils.isEmpty(param))
            throw new ConfigException(
                    String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        try {
            ExpressionUtil.getExpression(param);
            setExpresion(param);
            super.init(param);
        } catch (Exception e) {
            throw new ConfigException(format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
    }

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> ret = new ArrayList<Integer>();
        for (Integer id : spackage.getFilterBefore()) {
            Map<String, Object> map = FilterUtil.trans(spackage.getList().get(id));
            Boolean bin = scoreResult.getBin(id);
            Integer label = scoreResult.getLabel(id);
            map.put(Constant.SAMPLE_TAG, bin);
            map.put(Constant.LABEL_VAR, label);
            Object obj;
            try {
                obj = ExpressionUtil.eval(expresion, map);
                Boolean flag = (Boolean) obj;
                if (flag) {
                    ret.add(id);
                }
            } catch (ExpreException e) {
                warnNum++;
            }
        }
        return ret;
    }
}
