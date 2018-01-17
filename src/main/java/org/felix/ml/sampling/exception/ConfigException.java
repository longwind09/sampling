package org.felix.ml.sampling.exception;

import org.felix.ml.sampling.BaseException;

/**
 *
 *  */
public class ConfigException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = 1528963364806036598L;

    public ConfigException(String error) {
        super(error);
    }

    public ConfigException(String error, Exception e) {
        super(error, e);
    }
}
