package org.felix.ml.sampling.exception;

import org.felix.ml.sampling.BaseException;

/**
 *
 *  */
public class FilterException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = -8995499271146244952L;

    public FilterException(String error) {
        super(error);
    }

    public FilterException(String error, Exception e) {
        super(error, e);
    }
}
