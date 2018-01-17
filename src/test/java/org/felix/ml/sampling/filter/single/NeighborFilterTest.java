package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilterTest;
import org.felix.ml.sampling.filter.ILabel;
import org.felix.ml.sampling.filter.combo.NotFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 */
@RunWith(JUnit4.class)
public class NeighborFilterTest extends BaseFilterTest {

    @Test
    public void testNeighborFilter() throws ConfigException, FilterException {
        doTest(1001, 10, 3, 2, false);
        doTest(1001, 10, 3, null, false);
        doTest(1001, 10, null, 2, false);

        doTest(1001, 10, 3, 2, true);
        doTest(1001, 10, 3, null, true);
        doTest(1001, 10, null, 2, true);
    }

    private void doTest(final int max, final int modn, final Integer up, final Integer down, final boolean exclude)
            throws ConfigException, FilterException {
        IFilter mod10 = createModFIlter(modn);
        createShuffleSample(max);
//		createSample(max);
        createScoreResult(new ILabel() {
            public boolean getSample(int id) {
                int mod = id % modn;
                if (exclude && mod == 0)
                    return false;
                if (down != null && mod <= down)
                    return true;
                if (up != null) {
                    mod = (id + up) % modn;
                    if (mod <= up)
                        return true;
                }
                return false;
            }

            public int getLabel(int id) {
                return id % modn;
            }
        });
        createContext();
        NeighborFilter neighbor = new NeighborFilter();
        neighbor.setLabel(0);
        neighbor.setUpmax(up);
        neighbor.setDownmax(down);
        neighbor.setId("neighbor_filter");
        mod10.filter(spackage, scoreResult, filterContext);
        List<Integer> nerghborList = null;
        if (exclude) {
            NotFilter notFilter = new NotFilter();
            notFilter.setFilter(mod10);
            notFilter.setId("not_filter");
            spackage.setFilterBefore(notFilter.filter(spackage, scoreResult, filterContext));
        }
        nerghborList = neighbor.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(neighbor.toString(""));
        checkSample(nerghborList, true);
    }

    private void checkSample(List<Integer> filter, boolean sample) {
        List<Integer> list = this.binList.get(sample);
        checkList(list, filter);
    }

    @Test
    public void testNeighborFilterEmpty() throws FilterException {
        int max = 1000;
        createShuffleSample(max);
        createScoreResult(new ILabel() {
            @Override
            public boolean getSample(int id) {
                return true;
            }

            @Override
            public int getLabel(int id) {
                return id % 100;
            }
        });
        createContext();
        NeighborFilter neighbor = new NeighborFilter();
        neighbor.setLabel(110);
        neighbor.setUpmax(10);
        neighbor.setDownmax(10);

        List<Integer> list = neighbor.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(neighbor.toString(""));
        assertEquals(0, list.size());
    }
}
