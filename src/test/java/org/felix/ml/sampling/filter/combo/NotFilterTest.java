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
public class NotFilterTest extends BaseFilterTest {
    @Test
    public void testOrFilter() throws ConfigException, FilterException {
        int max = 10000;
        IFilter mod2 = createModFIlter(2);
        NotFilter notFilter = new NotFilter();
        List<IFilter> filters = new ArrayList<IFilter>();
        filters.add(mod2);
        notFilter.setFilter(mod2);
        createShuffleSample(max);
        createScoreResult(new ILabel() {
            public boolean getSample(int id) {
                return true;
            }

            public int getLabel(int id) {
                return id % 2;
            }
        });
        createContext();
        List<Integer> filter = notFilter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(notFilter.toString(""));
        checkLabel(filter, 1);
        NotFilter not2Filter = new NotFilter();
        not2Filter.setFilter(notFilter);
        NotFilter not3Filter = new NotFilter();
        not3Filter.setFilter(not2Filter);
        filter = not3Filter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(not3Filter.toString(""));
        checkLabel(filter, 1);
    }

    private void checkLabel(List<Integer> filter, int label) {
        List<Integer> list = labelList.get(label);
        checkList(list, filter);
    }
}
