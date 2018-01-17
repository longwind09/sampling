package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.felix.ml.sampling.util.Constant;
import org.felix.ml.sampling.util.FilterUtil;
import org.felix.ml.sampling.util.QueryUtil;

import java.util.*;

/**
 *
 *  */
public class NeighborFilter extends BaseFilter {
    private Integer label;
    private Boolean sample;
    private Integer upmax;
    private Integer downmax;
    private boolean warn;

    public void init(String param) throws ConfigException {
        try {
            Map<String, String[]> map = QueryUtil.parseUrlQueryString(param);
            String[] labels = map.get("label");
            String[] samples = map.get("sample");
            if (labels == null && samples == null)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            if (labels != null) {
                if (labels.length > 1)
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                try {
                    label = Integer.parseInt(labels[0]);
                } catch (Exception e) {
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                }
            }
            if (samples != null) {
                if (samples.length > 1)
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                try {
                    sample = Boolean.parseBoolean(samples[0]);
                } catch (Exception e) {
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                }
            }
            String[] ups = map.get("up");
            String[] downs = map.get("down");
            if (ups == null && downs == null)
                throw new ConfigException(
                        String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
            if (ups != null) {
                if (ups.length > 1)
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                upmax = Integer.parseInt(ups[0]);
            }
            if (downs != null) {
                if (downs.length > 1)
                    throw new ConfigException(
                            String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
                downmax = Integer.parseInt(downs[0]);
            }
            super.init(param);
        } catch (Exception e) {
            throw new ConfigException(String.format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public Boolean getSample() {
        return sample;
    }

    public void setSample(Boolean sample) {
        this.sample = sample;
    }

    public Integer getUpmax() {
        return upmax;
    }

    public void setUpmax(Integer upmax) {
        this.upmax = upmax;
    }

    public Integer getDownmax() {
        return downmax;
    }

    public void setDownmax(Integer downmax) {
        this.downmax = downmax;
    }

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) {
        warn = false;
        List<Integer> posList = collectValidPos(spackage, scoreResult);
        Collections.sort(posList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        List<Integer> ret = new ArrayList<Integer>();
        if (posList.size() == 0)
            return ret;
        List<Integer> cloneList = spackage.cloneFilterBefore();
        for (Integer lineId : cloneList) {
            Integer linePos = safeGetPos(lineId, spackage);
            int nearPosId = FilterUtil.findNear(linePos, posList);
            Integer[] posDis = FilterUtil.diffIndex(linePos, nearPosId, posList);
            if (upmax != null) {
                Integer upDis = posDis[0];
                if (upDis != null && upDis <= upmax) {
                    ret.add(lineId);
                    continue;
                }
            }
            if (downmax != null) {
                Integer downDis = posDis[1];
                if (downDis != null && downDis <= downmax)
                    ret.add(lineId);
            }
        }
        if (warn)
            warnPackageNum++;
        return ret;
    }

    private List<Integer> collectValidPos(SamplePackage spackage, ScoreResult scoreResult) {
        List<Integer> ret = new ArrayList<Integer>();
        List<Integer> labelPos = new ArrayList<Integer>();
        List<Integer> binPos = new ArrayList<Integer>();
        if (label != null) {
            Map<Integer, Integer> labelMap = scoreResult.getLabelMap();
            for (Map.Entry<Integer, Integer> entry : labelMap.entrySet()) {
                if (entry.getValue() == label) {
                    int id = entry.getKey();
                    Integer pos = safeGetPos(id, spackage);
                    if (pos == null) {
                        warn = true;
                    } else {
                        labelPos.add(pos);
                    }
                }
            }
        }
        if (sample != null) {
            Map<Integer, Boolean> binMap = scoreResult.getBinMap();
            for (Map.Entry<Integer, Boolean> entry : binMap.entrySet()) {
                if (sample.booleanValue() == entry.getValue()) {
                    int id = entry.getKey();
                    Integer pos = safeGetPos(id, spackage);
                    if (pos == null) {
                        warn = true;
                    } else {
                        binPos.add(pos);
                    }
                }
            }
        }
        if (label != null && sample == null)
            ret = labelPos;
        else if (label == null && sample != null)
            ret = binPos;
        for (int pos : labelPos)
            if (binPos.contains(pos))
                ret.add(pos);
        return ret;
    }

    private Integer safeGetPos(int id, SamplePackage spackage) {
        Map<String, String> rowMap = spackage.getList().get(id);
        if (rowMap == null) {
            warnNum++;
            return null;
        }
        String value = rowMap.get(Constant.POS_TAG);
        if (value == null) {
            warnNum++;
            return null;
        }
        try {
            int ret = Integer.parseInt(value);
            return ret;
        } catch (Exception e) {
            warnNum++;
            return null;
        }
    }
}
