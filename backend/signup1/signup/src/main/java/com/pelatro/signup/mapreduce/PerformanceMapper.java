package com.pelatro.signup.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

public class PerformanceMapper extends TableMapper<Text, Text> {

	@Override
	public void map(ImmutableBytesWritable rowKey, Result value, Context context) throws IOException, InterruptedException {
	    String rowKeyString = Bytes.toString(rowKey.get());
	    String[] keyParts = rowKeyString.split("_");

	    if (keyParts.length != 2) {
	        System.err.println("Invalid row key format: " + rowKeyString);
	        return; // Skip invalid row keys
	    }

	    String empId = keyParts[0];
	    String date = keyParts[1];

	    String taskAssigned = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("task_assigned")));
	    String taskCompleted = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("task_completed")));
	    String ontimeTaskCompleted = Bytes.toString(value.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("ontime_task_completed")));

	    if (taskAssigned != null && taskCompleted != null && ontimeTaskCompleted != null) {
	        System.out.println("Mapper Output: Key=" + empId + ", Value=" + date + "," + taskAssigned + "," + taskCompleted + "," + ontimeTaskCompleted);
	        context.write(new Text(empId), new Text(date + "," + taskAssigned + "," + taskCompleted + "," + ontimeTaskCompleted));
	    } else {
	        System.err.println("Missing data for row key: " + rowKeyString);
	    }
	}

}
