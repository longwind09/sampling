package org.felix.ml.sampling;

import org.felix.ml.sampling.exception.ExpreException;

import java.util.List;
import java.util.Map;

/**
 *
 * @1
 */
public interface ITag {
    public int tag(Map<String, String> row) throws ExpreException;

    public boolean tag2bin(int tag) throws ExpreException;

    public ScoreResult score(List<Map<String, String>> rowMaps);
}
