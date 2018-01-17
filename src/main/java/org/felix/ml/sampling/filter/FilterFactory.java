package org.felix.ml.sampling.filter;

import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.exception.ClassInvalidException;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.combo.AndFilter;
import org.felix.ml.sampling.filter.combo.NotFilter;
import org.felix.ml.sampling.filter.combo.OrFilter;
import org.felix.ml.sampling.filter.combo.RatioFilter;
import org.felix.ml.sampling.filter.single.*;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.trim;

/**
 *
 * @1
 */
public class FilterFactory {
    private static Map<String, IFilter> filterMap = new HashMap<String, IFilter>();
    private static Map<String, String> short2FullName = new HashMap<String, String>();
    private static boolean init = false;

    public static void reset() {
        filterMap = new HashMap<String, IFilter>();
        short2FullName = new HashMap<String, String>();
        init = false;
    }

    public static IFilter createFilter(String id, String shortName, String param)
            throws ClassInvalidException, ConfigException {
        if (!init) {
            init();
        }
        String fullName = short2FullName.get(trim(shortName));
        if (fullName == null)
            fullName = trim(shortName);
        synchronized (FilterFactory.class) {
            IFilter ret = null;
            String className = getClassName(fullName);
            try {
                Class classc = Class.forName(className);
                ret = (IFilter) classc.newInstance();
                ret.init(trim(param));
                ret.setId(id);
                filterMap.put(id, ret);
            } catch (ClassNotFoundException e) {
                throw new ClassInvalidException(format("class not found,id:%s shortName:%s fullName:%s param:%s", id,
                        shortName, fullName, param), e);
            } catch (InstantiationException e) {
                throw new ClassInvalidException(
                        format("instance error,id:%s shortName:%s param:%s", id, shortName, param), e);
            } catch (IllegalAccessException e) {
                throw new ClassInvalidException(
                        format("instance error,id:%s shortName:%s param:%s", id, shortName, param), e);
            }
            return ret;
        }
    }

    public static void clear(String id) {
        filterMap.clear();
        short2FullName.clear();
    }

    public static boolean contains(String id) {
        return filterMap.containsKey(id);
    }

    public static IFilter getFilter(String id) {
        return filterMap.get(id);
    }

    public static Set<String> filterIds() {
        return filterMap.keySet();
    }

    public static Collection<IFilter> filters() {
        return filterMap.values();
    }

    public static void addFilter(IFilter filter) {
        filterMap.put(filter.getId(), filter);
    }

    public static void init() {
        register(AllNotPassFilter.class);
        register(AllPassFilter.class);
        register(ClassFilter.class);
        register(ELFilter.class);
        register(NeighborFilter.class);
        register(PosFilter.class);
        register(RandomFilter.class);
        register(RandomFixNumFilter.class);
        register(RatioFilter.class);
        register(RelMaxPosFilter.class);
        register(UniqFilter.class);
        register(AndFilter.class);
        register(OrFilter.class);
        register(NotFilter.class);
        init = true;
    }

    public static void register(Class aclass) {
        if (!IFilter.class.isAssignableFrom(aclass) || Modifier.isAbstract(aclass.getModifiers())
                || Modifier.isInterface(aclass.getModifiers()))
            return;
        String name = aclass.getSimpleName();
        short2FullName.put(name, aclass.getName());
    }

    private static String getClassName(String shortName) {
        return shortName;
    }
}
