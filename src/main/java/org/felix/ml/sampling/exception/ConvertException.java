package org.felix.ml.sampling.exception;

import org.felix.ml.sampling.BaseException;

/**
 *
 *  */
public class ConvertException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = -8020912929870644441L;

    public ConvertException(String error) {
        super(error);
    }

    public ConvertException(String error, Exception e) {
        super(error, e);
    }
}
