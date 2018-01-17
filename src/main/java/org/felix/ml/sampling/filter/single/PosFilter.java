package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *  */
public class PosFilter extends BaseFilter {
    protected int pos;
    protected boolean dynamic = false;

    public void init(String param) throws ConfigException {
        if (dynamic) {
            super.init(param);
            return;
        }
        try {
            this.pos = Integer.parseInt(param);
            if (this.pos <= 0)
                throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            super.init(param);
        } catch (Exception e) {
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
        super.init(param);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void preFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) throws FilterException {
        super.preFilter(spackage, scoreResult, context);
        if (this.pos <= 0)
            valid = false;
    }

    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        List<Integer> ret = new ArrayList<Integer>();
        List<Map<String, String>> rowMaps = spackage.getList();
        boolean warnPackage = false;
        for (Integer id : spackage.getFilterBefore()) {
            Map<String, String> rowMap = rowMaps.get(id);
            String value = rowMap.get(Constant.POS_TAG);
            if (value == null) {
                warnNum++;
                warnPackage = true;
                continue;
            }
            try {
                int posValue = Integer.parseInt(value);
                if (posValue <= pos)
                    ret.add(id);
            } catch (Exception e) {
                warnNum++;
                warnPackage = true;
                continue;
            }
        }
        if (warnPackage)
            warnPackageNum++;
        return ret;
    }
}
