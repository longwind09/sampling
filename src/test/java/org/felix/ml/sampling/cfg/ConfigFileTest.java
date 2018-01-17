package org.felix.ml.sampling.cfg;

import org.apache.commons.io.IOUtils;
import org.felix.ml.sampling.IConfig;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.FilterDependenceException;
import org.felix.ml.sampling.filter.FilterFactory;
import org.felix.ml.sampling.util.StringUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class ConfigFileTest {
    @After
    public void after() {
        FilterFactory.reset();
    }

    @Test
    public void testString() throws IOException, ConfigException {
        String string = StringUtil.read(ConfigFileTest.class.getResourceAsStream("cfg.properties"));
        IConfig conf = new PropertiesConfig();
        conf.load(string);
    }

    @Test
    public void testNorm() throws FileNotFoundException, IOException, ConfigException {
        IConfig conf = new PropertiesConfig();
        conf.load(ConfigFileTest.class.getResourceAsStream("cfg.properties"));
    }

    @Test
    public void shuffleOrderCnf() throws IOException, ConfigException {
        List<String> lines = IOUtils.readLines(ConfigFileTest.class.getResourceAsStream("cfg.properties"));
        for (int i = 0; i < 1000; i++) {
            List<String> clone = new ArrayList<String>();
            clone.addAll(lines);
            Collections.shuffle(clone);
            InputStream in = toInputStream(clone);
            try {
                new PropertiesConfig().load(in);
                ;
            } catch (ConfigException e) {
                System.out.println("process at :" + i);
                e.printStackTrace();
            }
            in.close();
        }
    }

    private InputStream toInputStream(List<String> lines) {
        StringBuffer sb = new StringBuffer();
        for (String line : lines)
            sb.append(line + "\n");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Test(expected = FilterDependenceException.class)
    public void circle() throws ConfigException {
        IConfig config = new PropertiesConfig();
        config.load(ConfigFileTest.class.getResourceAsStream("cfg_circle.properties"));
        System.out.println("failed for commad line!!");
    }

    @Test
    public void test1() throws ConfigException {
        IConfig config = new PropertiesConfig();
        config.load(ConfigFileTest.class.getResourceAsStream("cfg_test1_u8.cnf"));
    }

}
