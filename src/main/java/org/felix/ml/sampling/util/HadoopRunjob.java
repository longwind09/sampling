package org.felix.ml.sampling.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.util.Date;

public class HadoopRunjob {
    public static final String userGrop_frefix = "user_";

    public static Job RunJob(Configuration conf, String inpathArray[], String outpath, String jobName,
                             Class<?> jarClass,
                             Class<? extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, Text>> mapperClass,
                             Class<? extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>> reducerClass,
                             Class<?> mapOutKey, int reducerNum) throws Exception {
        System.out.println("**#Run[" + jobName + "]_start" + (new Date()).toString());

        System.out.println("**outpath:" + outpath);
        Job job = new Job(conf, jobName);

        int num = 0;
        FileSystem fstm = FileSystem.get(conf);

        //
        for (String inpath : inpathArray) {
            System.out.println("add inpath: " + inpath);
            Path dir = new Path(inpath + "/");
            if (fstm.exists(dir)) {
                num++;
                // System.out.println("**inpath:" + inpath);
                FileInputFormat.addInputPath(job, new Path(inpath));
            } else {
                System.out.println("**input dir:" + inpath + " not exists");
            }
        }

        if (num == 0) {
            throw new Exception("****RunJob: no input path for RunJob.");
        }

        job.setJarByClass(jarClass);
        job.setMapperClass(mapperClass);
        if (reducerClass != null)
            job.setReducerClass(reducerClass);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(mapOutKey);
        job.setMapOutputValueClass(Text.class);

        job.setNumReduceTasks(reducerNum);

        if (!HadoopUtils.RemovePath(conf, outpath)) {
            throw new Exception("****RunJob: rm output path" + outpath + "failed");
        }

        FileOutputFormat.setOutputPath(job, new Path(outpath));

        int status = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("**#Run[" + jobName + "]_end" + (new Date()).toString());
        return job;
    }

    // single inpath
    public static int RunJob(Configuration conf, String inpath, String outpath, String jobName, Class<?> jarClass,
                             Class<? extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, Text>> mapperClass,
                             Class<? extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>> reducerClass,
                             Class<?> mapOutKey, int reducerNum) throws Exception {
        System.out.println("**#Run[" + jobName + "]_start" + (new Date()).toString());
        System.out.println("Input path: " + inpath);
        System.out.println("Output path:" + outpath);

        Job job = new Job(conf, jobName);

        // input path
        FileSystem fstm = FileSystem.get(conf);
        Path pt = new Path(inpath);
        if (!fstm.exists(pt)) {
            throw new Exception("Input path " + inpath + " not exist");
        }
        FileInputFormat.addInputPath(job, pt);

        // output path
        if (!HadoopUtils.RemovePath(conf, outpath)) {
            throw new Exception("Outpath " + outpath + " remove failed");
        }
        FileOutputFormat.setOutputPath(job, new Path(outpath));

        // ----
        conf.set("mapreduce.map.output.compress", "false");
        conf.set("mapreduce.output.fileoutputformat.compress", "false");

        // set class for map and reduce
        job.setJarByClass(jarClass);
        job.setMapperClass(mapperClass);
        job.setMapOutputKeyClass(mapOutKey);
        job.setMapOutputValueClass(Text.class);
        // job.setCombinerClass(reducerClass);
        job.setReducerClass(reducerClass);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        // set reduce num
        job.setNumReduceTasks(reducerNum);
        // wait until job done
        // System.exit(job.waitForCompletion(true)? 0: 1);
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
