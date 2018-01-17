package org.felix.ml.sampling;

import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;

/**
 *
 *  */
public interface IFieldConvert {
    public String getField();

    public void setField(String field);

    public void init(String conf) throws ConfigException;

    public String convert(String value) throws ConvertException;
}
