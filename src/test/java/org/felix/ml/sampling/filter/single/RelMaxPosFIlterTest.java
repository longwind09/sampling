package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilterTest;
import org.felix.ml.sampling.filter.ILabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class RelMaxPosFIlterTest extends BaseFilterTest {
    @Test
    public void testRelMaxPosFIlter() throws FilterException {
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
        RelMaxPosFilter filter = new RelMaxPosFilter();
        filter.setLabel(0);

        List<Integer> list = filter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(filter.toString(""));
        assertEquals(901, list.size());
        for (Integer i : list) {
            int posid = lineid2posid.get(i.intValue());
            assertTrue(String.format("wrong lindid:%s posid:%s", i, posid), posid <= 900);
        }
    }

    @Test
    public void testRelMaxPosFIlterEmpty() throws FilterException {
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
        RelMaxPosFilter filter = new RelMaxPosFilter();
        filter.setLabel(110);

        List<Integer> list = filter.filter(spackage, scoreResult, filterContext);
        if (debug)
            System.out.println(filter.toString(""));
        assertEquals(0, list.size());
    }
}
