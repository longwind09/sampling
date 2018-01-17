package org.felix.ml.sampling.exception;

/**
 *
 * @8
 */
public class FilterDependenceException extends ConfigException {

    /**
     *
     */
    private static final long serialVersionUID = -7248287040191708456L;

    public FilterDependenceException(String error, Exception e) {
        super(error, e);
    }

    public FilterDependenceException(String error) {
        super(error);
    }

}
