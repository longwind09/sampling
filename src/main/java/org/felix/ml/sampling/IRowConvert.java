package org.felix.ml.sampling;

import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;

/**
 *
 * @0
 *
 * mainly for row convert, eg. expand actions map, add columns
 */
public interface IRowConvert {
    public void init(String conf) throws ConfigException;

    public String convert(String in) throws ConvertException;
}
