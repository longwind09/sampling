package org.felix.ml.sampling.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *  */
public class FilterUtil {
    public static int findNear(int value, List<Integer> posList) {
        int high = posList.size() - 1;
        int low = 0;
        int mid;
        while (low <= high) {
            mid = low + (high - low) / 2;
            if (value < posList.get(mid))
                high = mid - 1;
            else if (value > posList.get(mid))
                low = mid + 1;
            else
                return mid;
        }
        if (low == posList.size())
            low = low - 1;
        return low;
    }

    public static Integer[] diffIndex(Integer value, int listId, List<Integer> orderList) {
        Integer[] ret = new Integer[2];
        if (value == null)
            return ret;
        Integer vList = 0;
        try {
            vList = orderList.get(listId);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (value.intValue() == vList) {
            ret[0] = 0;
            ret[1] = 0;
        } else if (value < vList) {
            ret[0] = vList - value;
            if (listId >= 1) {
                ret[1] = value - orderList.get(listId - 1);
            }
        } else if (value > vList) {
            ret[1] = value - vList;
            if (orderList.size() > listId + 1)
                ret[0] = vList - orderList.get(listId + 1);
        }
        return ret;
    }

    public static Map<Integer, Integer> posMap(String key, List<Integer> selList, List<Map<String, String>> rowMaps) {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (Integer i : selList) {
            Map<String, String> rowMap = rowMaps.get(i);
            String value = rowMap.get(key);
            int intValue = Integer.parseInt(value);
            ret.put(i, intValue);
        }
        return ret;
    }

    public static Integer findLast(Integer label, List<Integer> selList, Map<Integer, Integer> id2pos,
                                   Map<Integer, Integer> labelMap) {
        Integer ret = null;
        Integer max = -1;
        for (Integer i : selList) {
            int ilabel = labelMap.get(i);
            if (ilabel != label)
                continue;
            int pos = id2pos.get(i);
            if (pos > max)
                max = pos;
        }
        if (max > -1)
            ret = max;
        return ret;
    }

    public static Integer findLast(Boolean sample, List<Integer> selList, Map<Integer, Integer> id2pos,
                                   Map<Integer, Boolean> binMap) {
        Integer ret = null;
        Integer max = -1;
        for (Integer i : selList) {
            Boolean isample = binMap.get(i);
            if (isample != sample)
                continue;
            int pos = id2pos.get(i);
            if (pos > max)
                max = pos;
        }
        if (max > -1)
            ret = max;
        return ret;
    }

    public static Map<String, Object> trans(Map<String, String> in) {
        if (in == null)
            return null;
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.putAll(in);
        return ret;
    }
}
