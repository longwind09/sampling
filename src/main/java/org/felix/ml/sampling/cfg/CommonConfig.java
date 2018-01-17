package org.felix.ml.sampling.cfg;

import org.felix.ml.sampling.exception.ConfigException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 *  */
public class CommonConfig extends ConfigBase {
    PropertiesConfiguration propertiesConf = new PropertiesConfiguration();

    @Override
    protected String getValue(String key) {
        String[] arr = propertiesConf.getStringArray(key);
        return StringUtils.join(arr, ",");
    }

    @Override
    protected boolean contains(String key) {
        return propertiesConf.containsKey(key);
    }

    @Override
    protected Enumeration<Object> keys() {
        final Iterator<String> iter = propertiesConf.getKeys();
        return new Enumeration<Object>() {

            @Override
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            @Override
            public Object nextElement() {
                return iter.next();
            }

        };
    }

    @Override
    protected void doLoad(InputStream in) throws ConfigException {
        try {
            propertiesConf.load(in);
        } catch (ConfigurationException e) {
            throw new ConfigException("error load config!", e);
        }
    }

    protected void doLoad(Reader in) throws ConfigException {
        try {
            propertiesConf.load(in);
        } catch (ConfigurationException e) {
            throw new ConfigException("error load config!", e);
        }
    }

    @Override
    public String getProperty(String key) {
    	return getValue(key);
    }
}
