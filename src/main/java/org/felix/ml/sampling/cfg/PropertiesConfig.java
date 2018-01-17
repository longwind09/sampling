package org.felix.ml.sampling.cfg;

import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.util.OrderedProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 *  *          <p>
 *          编码问题,读取属性文件,要求编码iso-8859-1,如果是utf-8文件,需要执行native2ascii,不方便!!
 */
public class PropertiesConfig extends ConfigBase {
    private Properties prop = new OrderedProperties();

    /**
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ConfigException
     */
    public PropertiesConfig() {
    }

    @Override
    protected String getValue(String key) {
        return prop.getProperty(key);
    }

    @Override
    protected boolean contains(String key) {
        return prop.contains(key);
    }

    @Override
    protected Enumeration<Object> keys() {
        return prop.keys();
    }

    @Override
    protected void doLoad(InputStream in) throws ConfigException {
        try {
            prop.load(in);
        } catch (IOException e) {
            throw new ConfigException("error load config!", e);
        }
    }

    protected void doLoad(Reader in) throws ConfigException {
        try {
            prop.load(in);
        } catch (IOException e) {
            throw new ConfigException("error load config!", e);
        }
    }

	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return getValue(key);
	}
}
