package org.felix.ml.sampling.cfg;

import org.felix.ml.sampling.IConfig;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.util.Constant;
import org.felix.ml.sampling.util.StringUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class CommonConfigTest {
    @After
    public void after() {
        FilterFactory.reset();
    }

    @Test
    public void testString() throws IOException, ConfigException {
        String string = StringUtil.read(ConfigFileTest.class.getResourceAsStream("cfg_u8.cnf"));
        IConfig conf = new CommonConfig();
        conf.load(string);
        String qid_name = StringUtil.getValueWithDefault(conf.getProperty("qid"), Constant.QUERY_TAG);
        System.out.println(qid_name);
        System.out.println(conf.getProperty("qid"));
        System.out.println(conf.getRootFilter().toString(""));
    }

    @Test
    public void testNorm() throws FileNotFoundException, IOException, ConfigException {
        IConfig conf = new CommonConfig();
        try {
            conf.load(ConfigFileTest.class.getResourceAsStream("cfg_u8.cnf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
