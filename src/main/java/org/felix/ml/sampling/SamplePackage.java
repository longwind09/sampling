package org.felix.ml.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @7
 *          <p>
 *          样本过滤包
 */
public class SamplePackage {
    private List<Map<String, String>> list;
    private List<String> origList;
    private List<Integer> filterBefore;

    public SamplePackage(List<Map<String, String>> list, List<String> origList) {
        this.list = list;
        this.origList = origList;
        filterBefore = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++)
            filterBefore.add(i);
    }

    public List<Integer> getFilterBefore() {
        return filterBefore;
    }

    public void setFilterBefore(List<Integer> filterBefore) {
        this.filterBefore = filterBefore;
    }

    public List<String> getOrigList() {
        return origList;
    }

    public List<Integer> cloneFilterBefore() {
        List<Integer> ret = new ArrayList<Integer>();
        ret.addAll(filterBefore);
        return ret;
    }

    public List<Map<String, String>> getList() {
        return list;
    }
}
