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
public class OrFilterTest extends BaseFilterTest {
    @Test
    public void testOrFilter() throws ConfigException, FilterException {
        int max = 10000;
        IFilter mod20 = createModFIlter(20);
        IFilter mod30 = createModFIlter(30);
        IFilter mod50 = createModFIlter(50);
        OrFilter orFilter = new OrFilter();
        List<IFilter> filters = new ArrayList<IFilter>();
        filters.add(mod20);
        filters.add(mod30);
        filters.add(mod50);
        orFilter.setFilters(filters);
        createShuffleSample(max);
        createScoreResult(new ILabel() {
            public boolean getSample(int id) {
                return true;
            }

            public int getLabel(int id) {
                return (id % 20) * (id % 30) * (id % 50) == 0 ? 0 : 1;
            }
        });
        createContext();
        int id = 501;
        System.out.println(((id % 20) * (id % 30) * (id % 50)) == 0 ? 0 : 1);
        List<Integer> filter = orFilter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(orFilter.toString(""));
        checkLabel(filter, 0);
    }

    private void checkLabel(List<Integer> filter, int label) {
        List<Integer> list = labelList.get(label);
        checkList(list, filter);
    }
}
