package org.felix.ml.sampling;

import org.felix.ml.sampling.exception.ConfigException;

import java.io.File;
import java.io.InputStream;

import static org.felix.ml.sampling.util.Constant.LABEL_VAR;
import static org.felix.ml.sampling.util.Constant.SAMPLE_VAR;
import static org.apache.commons.lang.StringUtils.trim;

/**
 *
 *  */
public interface IConfig {
    public static final String CONFIG_KEY = "config_key";

    public void load(String string) throws ConfigException;

    public void load(File File) throws ConfigException;

    public void load(InputStream in) throws ConfigException;

    public ITag getTag();

    public IFilter getRootFilter();

    public IFilter getPreFilter();

    public IFilter getInnerFilter();

    public Out getOut();
    public String getProperty(String key);

    public IRowConvert getRowConvert();

    public enum Out {
        LABEL(LABEL_VAR), SAMPLE(SAMPLE_VAR);
        private String name;

        private Out(String str) {
            this.name = str;
        }

        public static Out str2Enum(String str) {
            if (LABEL_VAR.equals(trim(str)))
                return Out.LABEL;
            else if (SAMPLE_VAR.equals(trim(str)))
                return Out.SAMPLE;
            return null;
        }

        public boolean equals(Out out) {
            return this.name.equals(out.name);
        }
    }
}
