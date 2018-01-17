package org.felix.ml.sampling.filter;

/**
 *
 *  */
public interface ILabel {
    public int getLabel(int id);

    public boolean getSample(int id);
}
