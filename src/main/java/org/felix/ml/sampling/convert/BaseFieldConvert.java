package org.felix.ml.sampling.convert;

import org.felix.ml.sampling.IFieldConvert;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;

/**
 *
 *  */
public abstract class BaseFieldConvert implements IFieldConvert {
    protected String field;
    protected String conf;

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public void init(String conf) throws ConfigException {
        this.conf = conf;
    }

    @Override
    public String convert(String value) throws ConvertException {
        try {
            return doConvert(value);
        } catch (Exception e) {
            throw new ConvertException(value, e);
        }
    }

    protected abstract String doConvert(String value);
}
