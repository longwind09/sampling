package org.felix.ml.sampling.filter;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.IFilter;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

/**
 *
 * @3
 */
public abstract class BaseFilter implements IFilter {

    protected String id;
    protected boolean valid = true;
    protected long packageNum = 0;
    protected long warnPackageNum = 0;
    protected long inNum = 0;
    protected long warnNum = 0;
    protected long successNum = 0;
    protected long successPackNum = 0;

    protected String cnf;
    private Random r = new Random();

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInit() {
        return cnf;
    }

    public void init(String param) throws ConfigException {
        if (this.id == null)
            this.id = randomId();
        this.cnf = param;
    }

    protected String randomId() {
        return String.format("%s_", this.getClass().getSimpleName(), r.nextInt(10000));
    }

    public void preFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context) throws FilterException {
        packageNum++;
        inNum += spackage.getFilterBefore().size();
    }

    public List<Integer> filter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context)
            throws FilterException {
        if (context.contains(this.getId()))
            return context.getFilterResult(this.getId());
        preFilter(spackage, scoreResult, context);
        if (!valid)
            return new ArrayList<Integer>();
        List<Integer> ret = doFilter(spackage, scoreResult, context);
        context.addFilterResult(this.getId(), ret);
        successNum += ret.size();
        if (ret.size() > 0)
            successPackNum++;
        afterFilter(spackage);
        return ret;
    }

    public abstract List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context)
            throws FilterException;

    public void afterFilter(SamplePackage spackage) {
    }

    protected float getFloat(String param, String key, float defaultValue) {
        return defaultValue;
    }

    protected int getInt(String param, String key, int defaultValue) {
        return defaultValue;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(format("%sfilterId:%s filterName:%s cnf:%s\n", prefix, id, getClass().getSimpleName(), getInit()));
        sb.append(format("%spackage:%s\twarnPackage:%s\trows:%s\twarnrows:%s\tpass:%s\tpassPack:%s\n", prefix,
                packageNum, warnPackageNum, inNum, warnNum, successNum, successPackNum));
        return sb.toString();
    }

    public long getSuccessPackNum() {
        return successPackNum;
    }

    public void setSuccessPackNum(long successPackNum) {
        this.successPackNum = successPackNum;
    }

    public long getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(long packageNum) {
        this.packageNum = packageNum;
    }

    public long getWarnPackageNum() {
        return warnPackageNum;
    }

    public void setWarnPackageNum(long warnPackageNum) {
        this.warnPackageNum = warnPackageNum;
    }

    public long getInNum() {
        return inNum;
    }

    public void setInNum(long inNum) {
        this.inNum = inNum;
    }

    public long getWarnNum() {
        return warnNum;
    }

    public void setWarnNum(long warnNum) {
        this.warnNum = warnNum;
    }

    public long getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(long successNum) {
        this.successNum = successNum;
    }
}
