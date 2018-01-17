package org.felix.ml.sampling.util;

import org.felix.ml.sampling.exception.ExpreException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class ExpressionUtilTest {
    @Test
    public void testInt() throws ExpreException {
        String expression = "click*4+comment*2+email";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("click", 1);
                put("comment", 0);
                put("email", 1);
            }
        };
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Integer);
        assertEquals(5, obj);
    }

    @Test
    public void testString() throws ExpreException {
        String expression = "infotype.equals(\"精品\")";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("infotype", "精品");
            }
        };
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Boolean);
        assertEquals(true, obj);
    }

    @Test
    public void testTernary() throws ExpreException {
        String expression = "(\"true\".equals(click)?1:0)*4+comment*2+email";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("click", "true");
                put("comment", 0);
                put("email", 1);
            }
        };
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Integer);
        assertEquals(5, obj);
    }

    @Test
    public void testException() {
        String expression = "click*4+comment*2+email";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("click", 1);
                put("comment", 0);
            }
        };
        try {
            Object obj = ExpressionUtil.eval(expression, varMap);
        } catch (Exception e) {
            assertEquals(true, e instanceof ExpreException);
        }
    }

    @Test(expected = ExpreException.class)
    public void testWrongExp() throws ExpreException {
        String expression = "click*4++comment*2+++email";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("click", 1);
                put("comment", 0);
            }
        };
        Object obj = ExpressionUtil.eval(expression, varMap);
    }

    //自动转换的类型,
    @Test
    public void testString2int() throws ExpreException {
        int n = 30;
        String expression = format(
//				"(Integer.parseInt(%s) %% %s)==0"
                "((%s %% 20) * (%s %% 30) * (%s %% 50))== 0"
                , Constant.POS_TAG, Constant.POS_TAG, Constant.POS_TAG);
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put(Constant.POS_TAG, "50");
            }
        };
        System.out.println(expression);
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Boolean);
        assertEquals(true, obj);
    }

    @Test
    public void testString2int2() throws ExpreException {
        int n = 30;
        String expression = format(
                "_ctx['%s']*1"
                , "101");
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("101", "50");
            }
        };
        System.out.println(expression);
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Integer);
        assertEquals(50, obj);
    }

    @Test
    public void testStringEq() throws ExpreException {
        String expression = "\"jingpinxinxi\"==infotype";
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("infotype", "jingpinxinxi");
            }
        };
        System.out.println(expression);
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Boolean);
        assertEquals(true, obj);
        varMap.put("infotype", "jingpin");
        obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Boolean);
        assertEquals(false, obj);
    }

    @Test
    public void testJson() throws ExpreException {
        String json = "{ " +
                "\"stats\": { " +
                "\"sdr\": \"aa:bb:cc:dd:ee:ff\"," +
                "\"rcv\": \"aa:bb:cc:dd:ee:ff\", " +
                "\"time\": \"UTC in millis\", " +
                "\"type\": 1, " +
                "\"subt\": 1, " +
                "\"argv\": [" +
                "{\"1\": 2}," +
                "{\"2\": 3}" +
                "]}" +
                "}";
        String query = "/stats/argv/0/1";

        final JSONObject jsonV = new JSONObject(json);
        System.out.println(json);
        jsonV.query(query);
        Map<String, Object> varMap = new HashMap<String, Object>() {
            {
                put("jsonV", jsonV);
            }
        };
        String expression = "jsonV.query(\"" + query + "\")";
        Object obj = ExpressionUtil.eval(expression, varMap);
        assertEquals(true, obj instanceof Integer);
        assertEquals(2, ((Integer) obj).intValue());

        query = "/stats/argv/0/3";
        expression = "jsonV.query(\"" + query + "\")";
        obj = ExpressionUtil.eval(expression, varMap);
        assertNull(obj);
    }
}