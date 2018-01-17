package org.felix.ml.sampling.cfg;

import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.util.ConfigUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @7
 */
@RunWith(JUnit4.class)
public class ConfigTest {
    @After
    public void after() {
        FilterFactory.reset();
    }

    @Test
    public void testRegex() {
        Map<String, String> map = new HashMap<String, String>() {
            {
                put("filter1_type", "filter1");
                put("filter_1aZ_type", "filter_1aZ");
                put("filter_filter_type_type", "filter_filter_type");
            }
        };
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String real = ConfigUtil.getFilterId4type(entry.getKey());
            assertEquals(entry.getValue(), real);
        }
    }

    @Test
    public void testWrongStr() {
        List<String> list = new ArrayList<String>() {
            {
                add("filter_type");
                add("aafilter1_type");
                add("filter1_type_aa");
                add("filter-_type");
                add(" filter1_type");
            }
        };
        for (String str : list) {
            String real = ConfigUtil.getFilterId4type(str);
            assertNull(real);
        }
        String real = ConfigUtil.getFilterId4type(null);
        assertNull(real);
    }
}
