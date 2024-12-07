package mapreducer;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.log4j.BasicConfigurator;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class PerformanceMapper extends TableMapper<Text, Text> {
//    private static final Logger logger = LogManager.getLogger(PerformanceMapper.class);
    {
    	BasicConfigurator.configure();
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public void map(ImmutableBytesWritable rowKey, Result value, Context context) throws IOException, InterruptedException {
        String rowKeyString = Bytes.toString(rowKey.get());
        String[] keyParts = rowKeyString.split("_");

        if (keyParts.length != 2) {
            System.err.println("Invalid row key format: " + rowKeyString);
            return; // Skip invalid row keys
        }

        String empId = keyParts[0];
        String dateString = keyParts[1]; // Assuming date is in dd-MM-yyyy format

        Date date = null;
        try {
            date = DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            System.err.println("Invalid date format: " + dateString);
            return;
        }

        // Extract the task data
        String taskAssigned = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("task_assigned")));
        String taskCompleted = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("task_completed")));
        String ontimeTaskCompleted = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("ontime_task_completed")));

        // Pass data to the reducer, including the parsed date
        if (taskAssigned != null && taskCompleted != null && ontimeTaskCompleted != null && date != null) {
//            logger.info("Mapper Output: Key={}, Value={},{},{},{}", empId, date, taskAssigned, taskCompleted, ontimeTaskCompleted);

            context.write(new Text(empId), new Text(dateString + "," + taskAssigned + "," + taskCompleted + "," + ontimeTaskCompleted));
        } else {
//            logger.warn("Missing data for row key: {}", rowKeyString);

            System.err.println("Missing data for row key: " + rowKeyString);
        }
    }
}
