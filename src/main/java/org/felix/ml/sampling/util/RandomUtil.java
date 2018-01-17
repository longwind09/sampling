package org.felix.ml.sampling.util;

import java.util.Random;

/**
 *
 * @version 2016年12月9日 下午6:09:29
 */
public class RandomUtil {
    private static final Random rand = new Random();

    public static int rand(float rate) {
        int min = (int) Math.floor(rate);
        float r = rate - min;
        Random rand = new Random();
        float cmp = rand.nextFloat();
        int ret = min;
        if (r >= cmp)
            ret++;
        return ret;
    }
}
