package org.felix.ml.sampling.cfg;

import org.felix.ml.sampling.IConfig;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.IRowConvert;
import org.felix.ml.sampling.ITag;
import org.felix.ml.sampling.convert.DefaultRowConvert;
import org.felix.ml.sampling.exception.ClassInvalidException;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ExpreException;
import org.felix.ml.sampling.exception.FilterDependenceException;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.filter.single.BuiltInFilter;
import org.felix.ml.sampling.tag.DefaultTagImpl;
import org.felix.ml.sampling.util.ConfigUtil;
import org.felix.ml.sampling.util.Constant;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trim;

/**
 *
 *  */
public abstract class ConfigBase implements IConfig {
    protected List<String> filterIds = new ArrayList<String>();
    protected List<String> success = new ArrayList<String>();
    protected IFilter rootFilter;
    protected IFilter preFilter;
    protected IFilter innerFilter;
    protected IRowConvert rowConvert;
    protected ITag tag;
    protected Out out;
    private String label;
    private String sample;

    /**
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ConfigException
     */
    public ConfigBase() {
    }

    public void load(String str) throws ConfigException {
        doLoad(new StringReader(str));
        init();
        innerFilter = new BuiltInFilter();
        innerFilter.setId("inner");
        FilterFactory.addFilter(innerFilter);
    }

    public void load(File file) throws ConfigException {
        try {
            doLoad(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ConfigException("error load config file!", e);
        }
        init();
    }

    /**
     * 需要编码是iso-8859-1.
     *
     * @param in
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ConfigException
     */
    public void load(InputStream in) throws ConfigException {
        doLoad(in);
        init();
    }

    private void init() throws ConfigException {
        this.label = getValue(Constant.LABEL);
        this.sample = getValue(Constant.SAMPLE);
        this.out = Out.str2Enum(getValue(Constant.OUT));
        initConvert(getValue(Constant.CONVERT));
        expect("Wrong config! please check label.", !isEmpty(this.label));
        expect("Wrong config! please check sample.", !isEmpty(this.sample));
        expect("Wrong config! please check out.", this.out != null);
        initFilterCnf();
        instanceFilter();
        preFilter = FilterFactory.getFilter(Constant.FILTER_PRE_NAME);
        rootFilter = FilterFactory.getFilter(Constant.FILTER_ROOT_NAME);
        if (rootFilter == null)
            throw new ConfigException(String.format("Can't find filter:%s", Constant.FILTER_ROOT_NAME));
        try {
            tag = new DefaultTagImpl(this.label, this.sample);
        } catch (ExpreException e) {
            throw new ConfigException(String.format("wrong for label or sample el expression! check %s and %s", Constant.LABEL, Constant.SAMPLE));
        }
        initInnerFilter();
    }

    private void initConvert(String conf) throws ConfigException {
        if (isEmpty(conf))
            return;
        rowConvert = new DefaultRowConvert();
        rowConvert.init(conf);
    }

    private void initInnerFilter() {
        innerFilter = new BuiltInFilter();
        innerFilter.setId("inner");
        FilterFactory.addFilter(innerFilter);
    }

    private void initFilterCnf() throws ConfigException {
        Enumeration<Object> enumeration = keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String filterName = ConfigUtil.getFilterId4type(key);
            if (!isEmpty(filterName)) {
                filterIds.add(filterName);
                continue;
            }
            filterName = ConfigUtil.getFilterId4param(key);
            if (!isEmpty(filterName) && isEmpty(getTypeCnf(filterName))) {
                throw new ConfigException(
                        format("Find filter param for:%s, need type config.(check configure file order!)", filterName));
            }
        }
        if (!filterIds.contains(Constant.FILTER_ROOT_NAME))
            throw new ConfigException(String.format("Wrong config! please check %s", Constant.FILTER_ROOT_NAME));
    }

    private void instanceFilter() throws ConfigException, FilterDependenceException {
        FilterFactory.init();
        try {
            instanceOrderByCnf();
        } catch (ClassInvalidException e) {
            throw new ConfigException("Failed instance filter,check code!", e);
        } catch (ConfigException e) {
            if (e instanceof FilterDependenceException) {
                try {
                    tryMissOrder();
                } catch (ClassInvalidException e1) {
                    throw new ConfigException("Failed instance filter,check code!", e1);
                } catch (FilterDependenceException e1) {
                    throw e1;
                } catch (ConfigException e1) {
                    throw new ConfigException("Failed init filter, circle dependence?", e1);
                }
            } else {
                throw new ConfigException("Config error!", e);
            }
        }
    }

    private void instanceOrderByCnf() throws ConfigException, ClassInvalidException {
        for (String filterId : filterIds) {
            FilterFactory.createFilter(filterId, getTypeCnf(filterId), getParamCnf(filterId));
            success.add(filterId);
        }
    }

    private void tryMissOrder() throws ConfigException, FilterDependenceException, ClassInvalidException {
        boolean ret = tryOneByOne();
        while (ret) {
            ret = tryOneByOne();
        }
        if (success.size() != filterIds.size()) {
            throw new FilterDependenceException("cricle ref!");
        }
    }

    private boolean tryOneByOne() throws ConfigException, ClassInvalidException {
        boolean ret = false;
        for (String filterId : filterIds) {
            if (success.contains(filterId))
                continue;
            try {
                FilterFactory.createFilter(filterId, getTypeCnf(filterId), getParamCnf(filterId));
                success.add(filterId);
                ret = true;
            } catch (FilterDependenceException e) {
                // ignore
            }
        }
        return ret;
    }

    private String getTypeCnf(String filterId) {
        return trim(getValue(format("%s_type", filterId)));
    }

    private String getParamCnf(String filterId) {
        return trim(getValue(format("%s_param", filterId)));
    }

    private void expect(String str, boolean ret) throws ConfigException {
        if (!ret)
            throw new ConfigException(str);
    }

    protected abstract String getValue(String key);

    protected abstract boolean contains(String key);

    protected abstract Enumeration<Object> keys();

    protected abstract void doLoad(InputStream in) throws ConfigException;

    protected abstract void doLoad(Reader in) throws ConfigException;

    public IRowConvert getRowConvert() {
        return rowConvert;
    }

    public void setRowConvert(IRowConvert rowConvert) {
        this.rowConvert = rowConvert;
    }

    public ITag getTag() {
        return tag;
    }

    public IFilter getRootFilter() {
        return rootFilter;
    }

    public IFilter getPreFilter() {
        return preFilter;
    }

    public IFilter getInnerFilter() {
        return innerFilter;
    }

    public Out getOut() {
        return out;
    }
}
