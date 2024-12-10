package com.pelatro.signup.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class PerformanceJob {

    public static void main(String[] args) throws Exception {
        // Configuration for Hadoop job
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Employee Performance");

        job.setJarByClass(PerformanceJob.class);
        job.setMapperClass(PerformanceMapper.class);
        job.setReducerClass(PerformanceReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Local file path (you can specify this as your input path)
        Path inputPath = new Path("file:///path/to/your/input/file");  // Ensure it's local or HDFS
        Path outputPath = new Path("/path/to/output");  // Local or HDFS output

        // Set input and output paths
        org.apache.hadoop.fs.FileSystem fs = inputPath.getFileSystem(conf);
        fs.copyFromLocalFile(inputPath, new Path("/tmp/input_data"));  // Copy to HDFS if necessary

        // Set the InputFormatClass (ensure it's the correct one)
        job.setInputFormatClass(TextInputFormat.class);

        // Set the OutputFormat class (default is FileOutputFormat)
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job, outputPath);

        // Run the job and wait for it to complete
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
