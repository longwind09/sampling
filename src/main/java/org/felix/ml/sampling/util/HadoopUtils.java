package org.felix.ml.sampling.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HadoopUtils {

    public static void getCacheFile(Configuration conf, String hdfsFile, String linkName) throws URISyntaxException, IOException {
        if (HadoopUtils.checkPathExist(conf, hdfsFile)) {
            Path inPathRelLocal = new Path(hdfsFile);
            // # 号之后的名称是对上面文件的链接，不同文件的链接名不能相同，虽然由你自己随便取
            String inPathLinkRelLocal = inPathRelLocal.toUri().toString() + "#" + linkName;
            DistributedCache.addCacheFile(new URI(inPathLinkRelLocal), conf);
            System.out.println("addCacheFile(" + hdfsFile + ",linekName:)" + linkName);
        } else {
            System.out.println("error! file not exist !" + hdfsFile);
        }
        return;
    }

    public static boolean RemovePath(Configuration conf, String path) throws IOException {
        if (path.equalsIgnoreCase("/dsap/rawdata")) {
            System.out.println("****ERROR:RemoveROOTPath:" + path);
            return false;
        }
        FileSystem fstm = FileSystem.get(conf);
        Path dir = new Path(path);
        if (fstm.exists(dir)) {
            return fstm.delete(dir, true);
        } else {
            return true;
        }
    }

    /**
     * 检查目录是否已存在
     */
    public static boolean checkPathExist(Configuration conf, String path) throws IOException {
        if ((conf == null) || (null == path))
            return false;

        FileSystem fstm = FileSystem.get(conf);
        Path dir = new Path(path);
        if (fstm.exists(dir))
            return true;
        else
            return false;
    }
}
