package org.felix.ml.sampling.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @version 2016年12月9日 下午6:12:21
 */
@RunWith(JUnit4.class)
public class RandomUtilTest {
    @Test
    public void rand() {
        int base = 2;
        float rate = 0.3f;
        float testF = base + rate;
        int num1 = 0;
        int num2 = 0;
        int total = 0;
        for (int i = 0; i < 5000; i++) {
            int tmp = RandomUtil.rand(testF);
            Assert.assertEquals(true, tmp == base || tmp == (base + 1));
            total++;
            if (tmp == base)
                num1++;
            else if (tmp == (base + 1))
                num2++;
        }
        float actRate = 1.0f * num1 / total;
        Assert.assertEquals(true, actRate > (1 - rate - 0.1f) && actRate < (1 - rate + 0.1f));
    }
}
