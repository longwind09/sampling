package org.felix.ml.sampling.util;

import org.felix.ml.sampling.exception.ExpreException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class JsonParser {
    @Test
    public void json() throws ExpreException {
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
        System.out.println(obj);
    }
}
