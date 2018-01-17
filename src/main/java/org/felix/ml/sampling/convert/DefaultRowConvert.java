package org.felix.ml.sampling.convert;

import org.felix.ml.sampling.IFieldConvert;
import org.felix.ml.sampling.IRowConvert;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;
import org.felix.ml.sampling.util.Constant;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 *
 *
 *
 */
public class DefaultRowConvert implements IRowConvert {
    //这个正则表达式能够支持多种分隔符分隔的key:value
    //提取特征字段
    private static String pattern = "(\\S*):(\\S*)";
    //这两个设成public是为了在别的地方测试吧
    public static Pattern pattern_field = Pattern.compile(pattern);
    public JSONObject json = null;

    @Override

    /**
     * in:String  one raw line of sample, which contains unparsed action map
     * retrun: String new line of sample, which contains parsed action map
     */
    public String convert(String in) throws ConvertException {
        Matcher match = pattern_field.matcher(in);
        StringBuffer sb = new StringBuffer();
        while (match.find()) {
            //特征编号
            String key = match.group(1);
            //目前就注册了一个HiveConvert
            IFieldConvert convert = FieldConvertFactory.getFieldConvert(key);
            if (convert == null) {
                continue;
            }
            String value = match.group(2);
            //see HiveConvert
            String replace = convert.convert(value);
            //替换了原来行里面的actionmap 为解析后的字符串
            match.appendReplacement(sb, replace);
        }
        match.appendTail(sb);
        return sb.toString();
    }

    @Override
    public void init(String conf) throws ConfigException {
        try {
            json = new JSONObject(conf);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonObj = (JSONObject) json.get(key);
                String type = (String) jsonObj.get(Constant.CONVERT_FIELD_TYPE);
                String param = (String) jsonObj.get(Constant.CONVERT_FIELD_PARAM);
                FieldConvertFactory.createFieldConvert(key, type, param);
            }
        } catch (JSONException e) {
            throw new ConfigException(format("", 1));
        }
    }

}
