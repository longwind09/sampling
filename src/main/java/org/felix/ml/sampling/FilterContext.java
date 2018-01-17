package org.felix.ml.sampling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *  */
public class FilterContext {
    private Map<String, List<Integer>> filterResultMap = new HashMap<String, List<Integer>>();
    private Map<String, Object> filterLocal = new HashMap<String, Object>();

    public void addFilterResult(String filterId, List<Integer> filterResult) {
        filterResultMap.put(filterId, filterResult);
    }

    public int getNum(String filterId) {
        int ret = 0;
        List<Integer> list = filterResultMap.get(filterId);
        if (list != null) return list.size();
        return ret;
    }

    public boolean contains(String filterId) {
        return filterResultMap.containsKey(filterId);
    }

    public List<Integer> getFilterResult(String filterId) {
        return filterResultMap.get(filterId);
    }

    public Map<String, Object> getFilterLocal() {
        return filterLocal;
    }

    public void reset() {
        filterResultMap.clear();
        filterLocal.clear();
        ;
    }
}
