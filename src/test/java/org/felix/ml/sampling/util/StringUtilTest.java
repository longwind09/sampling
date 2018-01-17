package org.felix.ml.sampling.util;

import org.felix.ml.sampling.convert.HiveConvert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @6
 */
@RunWith(JUnit4.class)
public class StringUtilTest {
    @Test
    public void test2Map() {
        String in = "pos:107 clicked:false infotype:jingpinxinxi pagenum:1 qid:0d30000f-0000-15db-cdac-1c4fd18b8f86^20160930 uid:5hcG/lftqD3muvo9hnaDPA== 1:0 39:-9999 68:-9999 101:0 102:200 103:1 104:6060 105:6 106:1 107:1 108:1143 109:1 110:6 111:1 112:0 113:1 114:0 115:0 116:0 117:1 118:0 119:1 120:1 121:1 122:1 123:1 124:1 125:1 126:20 201:9623 501:0 502:-9999 503:-9999 504:15 505:0 506:0 601:-9999 602:-9999 603:0 604:-9999 605:-9999 606:-9999 607:-9999 608:-9999 609:-9999 610:-9999 611:-9999 612:-9999 613:-9999 614:-9999 615:-9999 616:-9999 617:-9999 001:c	";
        Map<String, String> map = StringUtil.str2Map(in);
        assertEquals(60, map.size());
        assertEquals("107", map.get("pos"));
        assertEquals("5hcG/lftqD3muvo9hnaDPA==", map.get("uid"));
        assertEquals("0d30000f-0000-15db-cdac-1c4fd18b8f86^20160930", map.get("qid"));
        assertEquals("c", map.get("001"));
    }

    @Test
    /**
     * 改了str2map的逻辑，要求key和value不能为"",且key和value中不能有分隔符":"
     */
    public void test2Map2() {
        String in = "a1\ta2: a3:b3:e3 :f3 ";
        Map<String, String> map = StringUtil.str2Map(in);
        assertEquals(0, map.size());
//        assertEquals("", map.get("a2"));
//        assertEquals("b3:e3", map.get("a3"));
//        assertEquals("f3", map.get(""));
    }

    @Test
    public void testEq() {
        Boolean a = new Boolean(true);
        Boolean b = new Boolean(true);
        assertTrue(a.booleanValue() == b);
    }

    @Test
    public void testtoMap() {
        String str1 = String.format("%s%s%s", "30", HiveConvert.ETX_STR, "40");
        String str2 = String.format("%s%s%s", "300", HiveConvert.ETX_STR, "400");
        String str = String.format("%s%s%s", str1, HiveConvert.STX_STR, str2);
        System.out.println(str);
        System.out.println("hex:[" + StringUtil.toHex(str) + "]");
        Map<String, String> map = StringUtil.toMap(str, HiveConvert.ETX_STR, HiveConvert.STX_STR);
        assertEquals(2, map.size());
        assertEquals("40", map.get("30"));
        assertEquals("400", map.get("300"));
    }
}
