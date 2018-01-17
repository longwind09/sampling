package org.felix.ml.sampling.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class QueryUtilTest {
    @Test
    public void test1() {
        String query = "a=1&b=2&c=333&b=22";
        Map<String, String[]> map = QueryUtil.parseUrlQueryString(query);
        assertEquals(query, 1, map.get("a").length);
        assertEquals(query, "1", map.get("a")[0]);
        assertEquals(query, 2, map.get("b").length);
        assertEquals(query, "2", map.get("b")[0]);
        assertEquals(query, "22", map.get("b")[1]);
    }
}
