package org.felix.ml.sampling;

import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;

import java.util.List;

/**
 *
 * @4
 */
public interface IFilter {
    public String getId();

    public void setId(String id);

    public void init(String param) throws ConfigException;

    public String getInit();

    public List<Integer> filter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) throws FilterException;

    public String toString(String prefix);

    public long getPackageNum();

    public void setPackageNum(long packageNum);

    public long getWarnPackageNum();

    public void setWarnPackageNum(long warnPackageNum);

    public long getInNum();

    public void setInNum(long inNum);

    public long getWarnNum();

    public void setWarnNum(long warnNum);

    public long getSuccessNum();

    public void setSuccessNum(long successNum);

    public long getSuccessPackNum();

    public void setSuccessPackNum(long packageNum);
}
