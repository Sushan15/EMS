package mapreducer;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PerformanceTracker {
    public void performanceDriver() throws IOException, ClassNotFoundException, InterruptedException {
        String hbaseTableName = "employee";
        String localResultFilePath = "/home/pelatro/Hbasedatastoragefile/performance/performance_result.txt";

        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://localhost:9000");
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        Path outputPath = new Path("/user/hadoop/performance_output");
        FileSystem fs = FileSystem.get(config);

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(config, "Employee Performance Tracker");
        job.setJarByClass(PerformanceTracker.class);

        Scan scan = new Scan();
        TableMapReduceUtil.initTableMapperJob(
                hbaseTableName,
                scan,
                PerformanceMapper.class,
                Text.class,
                Text.class,
                job
        );

        job.setReducerClass(PerformanceReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, outputPath);

        boolean success = job.waitForCompletion(true);
        if (success) {
            PerformanceFileCreator fileCreator = new PerformanceFileCreator();
            fileCreator.createPerformanceFile(localResultFilePath, outputPath, config);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        PerformanceTracker tracker = new PerformanceTracker();
        tracker.performanceDriver();
    }
}
