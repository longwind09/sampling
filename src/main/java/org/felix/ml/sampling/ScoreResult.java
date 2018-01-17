package org.felix.ml.sampling;

import java.util.List;
import java.util.Map;

/**
 *
 * @0 打分结果
 */
public class ScoreResult {
    private Map<Integer, Integer> labelMap;
    private Map<Integer, Boolean> binMap;
    private List<Integer> fails;

    public ScoreResult(Map<Integer, Integer> labelMap, Map<Integer, Boolean> binMap, List<Integer> fails) {
        this.labelMap = labelMap;
        this.binMap = binMap;
        this.fails = fails;
    }

    public List<Integer> getFails() {
        return fails;
    }

    public Map<Integer, Integer> getLabelMap() {
        return labelMap;
    }

    public Map<Integer, Boolean> getBinMap() {
        return binMap;
    }

    public Integer getLabel(int id) {
        return labelMap.get(id);
    }

    public Boolean getBin(int id) {
        return binMap.get(id);
    }
}
