package org.felix.ml.sampling.filter;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.single.ELFilter;
import org.felix.ml.sampling.util.Constant;
import org.junit.After;

import java.util.*;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 *
 *  */
public class BaseFilterTest {
    protected static boolean debug = false;
    protected SamplePackage spackage;
    protected FilterContext filterContext;
    protected ScoreResult scoreResult;
    protected Map<Integer, List<Integer>> labelList;
    protected Map<Boolean, List<Integer>> binList;
    protected Map<Integer, Integer> lineid2posid;

    protected void createScoreResult(ILabel ilabel) {
        Map<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
        Map<Integer, Boolean> binMap = new HashMap<Integer, Boolean>();
        lineid2posid = new HashMap<Integer, Integer>();
        List<Integer> fails = new ArrayList<Integer>();
        List<Map<String, String>> rowMaps = spackage.getList();
        labelList = new HashMap<Integer, List<Integer>>();
        binList = new HashMap<Boolean, List<Integer>>();
        for (int i = 0; i < rowMaps.size(); i++) {
            Map<String, String> map = rowMaps.get(i);
            int id = Integer.parseInt(map.get(Constant.POS_TAG));
            int labelV = ilabel.getLabel(id);
            boolean binV = ilabel.getSample(id);
            List<Integer> llist = labelList.get(labelV);
            if (llist == null) {
                llist = new ArrayList<Integer>();
                labelList.put(labelV, llist);
            }
            llist.add(i);
            List<Integer> blist = binList.get(binV);
            if (blist == null) {
                blist = new ArrayList<Integer>();
                binList.put(binV, blist);
            }
            blist.add(i);
            labelMap.put(i, ilabel.getLabel(id));
            binMap.put(i, ilabel.getSample(id));
            lineid2posid.put(i, id);
        }
        scoreResult = new ScoreResult(labelMap, binMap, fails);
    }

    public IFilter createModFIlter(int n) throws ConfigException {
        ELFilter ret = new ELFilter();
        ret.setId(format("mod %s", n));
        ret.init(String.format("(%s %% %s)==0", Constant.POS_TAG, n));
        return ret;
    }

    protected void createContext() {
        filterContext = new FilterContext();
    }

    protected void createSample(int max) {
        spackage = new SamplePackage(createList(max), null);
    }

    protected void createShuffleSample(int max) {
        List<Map<String, String>> list = createList(max);
        Collections.shuffle(list);
        spackage = new SamplePackage(list, null);
    }

    protected List<Map<String, String>> createList(int max) {
        List<Map<String, String>> rowMaps = new ArrayList<Map<String, String>>();
        for (int i = 0; i < max; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(Constant.POS_TAG, "" + i);
            rowMaps.add(map);
        }
        return rowMaps;
    }

    protected void checkList(List<Integer> expected, List<Integer> real) {
        assertEquals(expected.size(), real.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(format("row:%s pos_1:%s pos_2:%s", i, spackage.getList().get(expected.get(i)).get("pos"),
                    spackage.getList().get(real.get(i)).get("pos")), expected.get(i), real.get(i));
        }
    }

    @After
    public void after() {
        FilterFactory.reset();
    }
}
