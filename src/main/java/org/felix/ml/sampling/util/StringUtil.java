package org.felix.ml.sampling.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

/**
 *
 *  */
public class StringUtil {
    /**
     * 输入的数据格式要求，必须是空格分隔
     * @param in
     * @return
     */
    public static Map<String, String> str2Map(String in) {
        if (in == null)
            return null;
        Map<String, String> ret = new HashMap<String, String>();
        //split by space
        String[] items = StringUtils.split(in.trim(), " ");

        for (String item : items) {
            String[] arr = StringUtils.split(item, ":");
            if (arr.length == 2)
                ret.put(arr[0].trim(), arr[1].trim());
        }
        return ret;
    }


    public static String map2Str(Map<String, String> map) {
        if (map == null || map.size() == 0)
            return null;
        List<String> arr = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arr.add(String.format("%s:%s", entry.getKey(), entry.getValue()));
        }
        return StringUtils.join(arr.toArray(), " ");
    }

    public static String list2Str(List<String> strs) {
        StringBuffer sb = new StringBuffer();
        for (String line : strs) {
            sb.append(line + "\n");
        }
        return sb.toString();
    }

    public static String read(String file) throws IOException {
        return read(new FileInputStream(file));
    }

    public static String read(InputStream in) throws IOException {
        return list2Str(IOUtils.readLines(in));
    }

    public static Map<String, String> toMap(String value, String inner, String outer) {
        Map<String, String> ret = new HashMap<String, String>();
        if (StringUtils.isEmpty(value))
            return ret;
        String[] items = value.split(outer);
        for (String item : items) {
            String[] arrs = item.split(inner);
            ret.put(arrs[0], arrs[1]);
        }
        return ret;
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public static  String getValueWithDefault(String value, String defaultValue) {
        if (StringUtils.isBlank(value)) return defaultValue;
        return value;
    }
}
