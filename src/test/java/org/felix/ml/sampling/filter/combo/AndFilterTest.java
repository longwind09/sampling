package org.felix.ml.sampling.filter.combo;

import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilterTest;
import org.felix.ml.sampling.filter.ILabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class AndFilterTest extends BaseFilterTest {

    @Test
    public void testAndFilter() throws ConfigException, FilterException {
        doTestAndFilter(false);
        doTestAndFilter(true);
    }

    public void doTestAndFilter(boolean shortFlag) throws ConfigException, FilterException {
        int max = 10000;
        IFilter mod2 = createModFIlter(2);
        IFilter mod3 = createModFIlter(3);
        IFilter mod5 = createModFIlter(5);
        AndFilter andFilter = new AndFilter();
        List<IFilter> filters = new ArrayList<IFilter>();
        filters.add(mod2);
        filters.add(mod3);
        filters.add(mod5);
        andFilter.setFilters(filters);
        andFilter.setShort(shortFlag);
        createShuffleSample(max);
        createScoreResult(new ILabel() {
            @Override
            public boolean getSample(int id) {
                return true;
            }

            @Override
            public int getLabel(int id) {
                return id % 30;
            }
        });
        createContext();
        List<Integer> filter = andFilter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(andFilter.toString(""));
        checkLabel(filter, 0);
    }

    private void checkLabel(List<Integer> filter, int label) {
        List<Integer> list = labelList.get(label);
        checkList(list, filter);
    }
}