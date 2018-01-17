package org.felix.ml.sampling.convert;

import org.felix.ml.sampling.IFieldConvert;
import org.felix.ml.sampling.exception.ClassInvalidException;
import org.felix.ml.sampling.exception.ConfigException;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.trim;

/**
 *
 *  */
public class FieldConvertFactory {
    private static Map<String, IFieldConvert> convertMap = new HashMap<String, IFieldConvert>();
    private static Map<String, String> short2FullName = new HashMap<String, String>();

    private static boolean init = false;

    public static void reset() {
        convertMap = new HashMap<String, IFieldConvert>();
        short2FullName = new HashMap<String, String>();
        init = false;
    }

    public static IFieldConvert createFieldConvert(String field, String type, String param)
            throws ClassInvalidException, ConfigException {
        if (!init) {
            init();
        }
        String fullName = short2FullName.get(trim(type));
        if (fullName == null)
            fullName = trim(type);
        synchronized (FieldConvertFactory.class) {
            IFieldConvert ret = null;
            String className = getClassName(fullName);
            try {
                Class classc = Class.forName(className);
                ret = (IFieldConvert) classc.newInstance();
                ret.init(trim(param));
                ret.setField(trim(field));
                convertMap.put(trim(field), ret);
            } catch (ClassNotFoundException e) {
                throw new ClassInvalidException(format("class not found,field:%s shortName:%s fullName:%s param:%s", field,
                        type, fullName, param), e);
            } catch (InstantiationException e) {
                throw new ClassInvalidException(
                        format("instance error,field:%s shortName:%s param:%s", field, type, param), e);
            } catch (IllegalAccessException e) {
                throw new ClassInvalidException(
                        format("instance error,field:%s shortName:%s param:%s", field, type, param), e);
            }
            return ret;
        }
    }

    public static void init() {
        register(HiveConvert.class);
        init = true;
    }

    public static void register(Class aclass) {
        if (!IFieldConvert.class.isAssignableFrom(aclass) || Modifier.isAbstract(aclass.getModifiers())
                || Modifier.isInterface(aclass.getModifiers()))
            return;
        String name = aclass.getSimpleName();
        short2FullName.put(name, aclass.getName());
    }

    public static IFieldConvert getFieldConvert(String name) {
        return convertMap.get(name);
    }

    public static boolean contains(String id) {
        return convertMap.containsKey(id);
    }

    private static String getClassName(String shortName) {
        return shortName;
    }
}
