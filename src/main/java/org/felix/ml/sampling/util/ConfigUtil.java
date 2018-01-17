package org.felix.ml.sampling.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 *
 *  */
public class ConfigUtil {
    public static final String FILTER_TYPE = "^(filter([0-9a-zA-Z_]+))_type";
    public static final String FILTER_PARAM = "^(filter([0-9a-zA-Z_]+))_param";
    public static Pattern pattern_type = Pattern.compile(FILTER_TYPE);
    public static Pattern pattern_param = Pattern.compile(FILTER_PARAM);

    public static String getFilterId4type(String in) {
        return getFilterName(in, pattern_type);
    }

    public static String getFilterId4param(String in) {
        return getFilterName(in, pattern_param);
    }

    private static String getFilterName(String in, Pattern pattern) {
        if (isEmpty(in))
            return null;
        Matcher match = pattern.matcher(in);
        if (match.matches()) {
            return match.group(1);
        }
        return null;
    }
}
