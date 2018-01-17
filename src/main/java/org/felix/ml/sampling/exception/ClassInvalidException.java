package org.felix.ml.sampling.exception;

/**
 *
 *  */
public class ClassInvalidException extends ConfigException {

    /**
     *
     */
    private static final long serialVersionUID = -2954681749345970072L;

    public ClassInvalidException(String error) {
        super(error);
    }

    public ClassInvalidException(String error, Exception e) {
        super(error, e);
    }
}
