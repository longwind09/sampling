package org.felix.ml.sampling.convert;

import org.felix.ml.sampling.IRowConvert;
import org.felix.ml.sampling.exception.ConfigException;
import org.felix.ml.sampling.exception.ConvertException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

/**
 *
 *  */
@RunWith(JUnit4.class)
public class DefaultRowConvertTest {
    private IRowConvert rowConvert;
    private String conf = "{\"14\":{\"type\":\"HiveConvert\",\"param\":\"k=1,2,4,5,6&v=-1,-2,-4,-5,-6&o=_1,_2,_4,_5,_6\"}}";

    @Before
    public void setup() throws ConfigException {
        rowConvert = new DefaultRowConvert();
        rowConvert.init(conf);
    }

    @Test
    public void test() throws IOException, ConvertException {
        List<String> lines = IOUtils.readLines(DefaultRowConvertTest.class.getResourceAsStream("hadoopLog.txt"));
        System.out.println("conf:" + conf);
        try {
            for (String line : lines) {
                System.out.println("-------------------------------");
                System.out.println("raw line:");
                System.out.println(line);
                String newLine = rowConvert.convert(line);
                System.out.println("new line");
                System.out.println(newLine);
            }
        } catch (ConvertException e) {
            e.printStackTrace();
        }
    }
}
