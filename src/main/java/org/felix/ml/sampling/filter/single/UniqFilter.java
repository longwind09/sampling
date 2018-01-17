package org.felix.ml.sampling.filter.single;

import org.felix.ml.sampling.FilterContext;
import org.felix.ml.sampling.SamplePackage;
import org.felix.ml.sampling.ScoreResult;
import org.felix.ml.sampling.exception.FilterException;
import org.felix.ml.sampling.filter.BaseFilter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @version 2016年11月30日 下午4:23:00
 */
public class UniqFilter extends BaseFilter {

    @Override
    public List<Integer> doFilter(SamplePackage spackage, ScoreResult scoreResult, FilterContext context)
            throws FilterException {
        Set<String> uniq = new HashSet<String>();
        List<Integer> ret = new ArrayList<Integer>();
        List<String> list = spackage.getOrigList();
        for (Integer id : spackage.getFilterBefore()) {
            String str = list.get(id);
            String md5 = DigestUtils.md5Hex(str);
            if (uniq.contains(md5)) {
                continue;
            } else {
                uniq.add(md5);
                ret.add(id);
            }
        }
        return ret;
    }

}
