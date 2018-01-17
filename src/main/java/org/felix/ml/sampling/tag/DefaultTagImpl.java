package org.felix.ml.sampling.tag;

import org.felix.ml.sampling.ITag;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ExpreException;
import org.felix.ml.sampling.util.ExpressionUtil;
import org.felix.ml.sampling.util.FilterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.felix.ml.sampling.util.Constant.LABEL_VAR;

/**
 *
 *  */
public class DefaultTagImpl implements ITag {
    protected int packageNum = 0;
    protected int warnPackageNum = 0;
    protected int inNum = 0;
    protected int warnNum = 0;
    protected int successNum = 0;
    private String tagExpre;
    private String binExpre;

    public DefaultTagImpl(String tagExpression, String binExpression) throws ExpreException {
        tagExpre = tagExpression;
        binExpre = binExpression;
        ExpressionUtil.getExpression(tagExpression);
        ExpressionUtil.getExpression(binExpression);
    }

    public ScoreResult score(List<Map<String, String>> rowMaps) {
        Map<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
        Map<Integer, Boolean> binMap = new HashMap<Integer, Boolean>();
        List<Integer> fails = new ArrayList<Integer>();
        packageNum++;
        boolean warn = false;
        for (int i = 0; i < rowMaps.size(); i++) {
            inNum++;
            Map<String, String> map = rowMaps.get(i);
            int tag;
            boolean label_eval = false;
            boolean sample_eval = false;
            try {
                tag = tag(map);
                label_eval = true;
                labelMap.put(i, tag);
                boolean sample = tag2bin(tag);
                sample_eval = true;
                binMap.put(i, sample);
                successNum++;
            } catch (ExpreException e) {
                warn = true;
                warnNum++;
            } catch (Exception e) {
                warn = true;
                warnNum++;
            }
        }
        if (warn)
            warnPackageNum++;
        return new ScoreResult(labelMap, binMap, fails);
    }

    public int tag(Map<String, String> row) throws ExpreException {
        Map<String, Object> map = FilterUtil.trans(row);
        Object obj = ExpressionUtil.eval(tagExpre, map);
        Integer tag = null;
        if (obj instanceof String) {
            tag = Integer.parseInt((String) obj);
        } else {
            tag = (Integer) obj;
        }
        row.put(LABEL_VAR, "" + tag);
        return tag;
    }

    public boolean tag2bin(int tag) throws ExpreException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(LABEL_VAR, tag);
        Object obj = ExpressionUtil.eval(binExpre, map);
        Boolean ret = (Boolean) obj;
        return ret;
    }

}
