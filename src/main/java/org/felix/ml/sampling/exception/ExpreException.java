package org.felix.ml.sampling.exception;

import org.felix.ml.sampling.BaseException;

/**
 *
 *  */
public class ExpreException extends BaseException {
    public static final int initError = 1;
    public static final int evalError = 2;
    /**
     *
     */
    private static final long serialVersionUID = -6374539116728615568L;
    private int type;

    public ExpreException(int type, String error) {
        super(error);
        this.type = type;
    }

    public ExpreException(int type, String error, Exception e) {
        super(error, e);
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
