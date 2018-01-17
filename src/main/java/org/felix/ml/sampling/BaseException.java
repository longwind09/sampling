package org.felix.ml.sampling;

/**
 *
 *  */
public class BaseException extends Exception {
    public BaseException(String error) {
        super(error);
    }

    public BaseException(String error, Exception e) {
        super(error, e);
    }
}
