package org.felix.ml.sampling.util;

import org.felix.ml.sampling.exception.ExpreException;
import org.apache.commons.jexl3.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 *
 * @8
 */
public class ExpressionUtil {
    private static JexlEngine jexl = new JexlBuilder().create();
    private static Map<String, JexlExpression> expressionMap = new HashMap<String, JexlExpression>();

    public static Object eval(String expression, Map<String, Object> varMap) throws ExpreException {
        JexlExpression e = getExpression(expression);
        JexlContext jc = new MapContext(varMap);
        jc.set("_ctx", varMap);
        try {
            Object ret = e.evaluate(jc);
            return ret;
        } catch (Exception excep) {
            throw new ExpreException(ExpreException.evalError, format("error evaluate:%s", expression), excep);
        }
    }

    public static JexlExpression getExpression(String expression) throws ExpreException {
        JexlExpression e = expressionMap.get(expression);
        if (e == null) {
            try {
                e = jexl.createExpression(expression);
            } catch (Exception excep) {
                throw new ExpreException(ExpreException.initError, format("error evaluate:%s", expression), excep);
            }
            expressionMap.put(expression, e);
        }
        return e;
    }
}
