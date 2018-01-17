package org.felix.ml.sampling.convert;

import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.util.StringUtil;
import org.felix.ml.sampling.util.QueryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.trim;

/**
 *
 *  */
public class HiveConvert extends BaseFieldConvert {

    private static int STX = 0x02;
    public static final String STX_STR = new Character((char) STX).toString();
    private static char ETX = 0x03;
    public static final String ETX_STR = new Character((char) ETX).toString();
    private List<String> kList = new ArrayList<String>();
    private List<String> vList = new ArrayList<String>();
    private List<String> oList = new ArrayList<String>();

    @Override
    public void init(String param) throws ConfigException {
        Map<String, String[]> map = QueryUtil.parseUrlQueryString(param);
        String[] keys = map.get("k");
        String[] values = map.get("v");
        String[] outs = map.get("o");
        if (keys == null || values == null || outs == null || keys.length != values.length
                || values.length != outs.length || keys.length != 1) {
            throw new ConfigException(format("wrong config for %s,param:%s", getClass().getSimpleName(), param));
        }
        String key = keys[0];
        String value = values[0];
        String out = outs[0];
        String[] kArr = key.split(",");
        String[] vArr = value.split(",");
        String[] oArr = out.split(",");
        if (kArr.length != vArr.length || kArr.length != oArr.length)
            throw new ConfigException(format("wrong config for %s,param:%s", getClass().getSimpleName(), param));

        for (String akey : kArr)
            kList.add(trim(akey));
        for (String avalue : vArr)
            vList.add(trim(avalue));
        for (String aout : oArr)
            oList.add(trim(aout));
        super.init(param);
    }

    @Override
    protected String doConvert(String value) {
        Map<String, String> map = StringUtil.toMap(value, ETX_STR, STX_STR);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < kList.size(); i++) {
            String key = kList.get(i);
            String v = map.containsKey(key) ? map.get(key) : vList.get(i);
            sb.append(format("%s%s:%s", i == 0 ? "" : " ", oList.get(i), v));
        }
        return sb.toString();
    }

}
