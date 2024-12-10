package com.pelatro.signup.mapreduce;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PerformanceReducer extends Reducer<Text, Text, Text, Text> {
	
    private static final double COMPLETION_WEIGHT = 0.4;
    private static final double ONTIME_COMPLETION_WEIGHT = 0.6;

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        double totalTasksAssigned = 0.0;
        double totalTasksCompleted = 0.0;
        double totalOntimeTasksCompleted = 0.0;

        // Parse values for the employee
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length == 4) {
                try {
                    double tasksAssigned = Double.parseDouble(fields[1]);
                    double tasksCompleted = Double.parseDouble(fields[2]);
                    double ontimeTasksCompleted = Double.parseDouble(fields[3]);

                    totalTasksAssigned += tasksAssigned;
                    totalTasksCompleted += tasksCompleted;
                    totalOntimeTasksCompleted += ontimeTasksCompleted;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in value: " + value.toString());
                }
            }
        }

        // Calculate performance
        double performanceFromTasks = totalTasksAssigned > 0 ? (totalTasksCompleted / totalTasksAssigned) * 100 : 0.0;
        double performanceFromOntimeTasks = totalTasksAssigned > 0 ? (totalOntimeTasksCompleted / totalTasksAssigned) * 100 : 0.0;

        // Average of the two performances
        double overallPerformance = ((COMPLETION_WEIGHT*performanceFromTasks )+ (ONTIME_COMPLETION_WEIGHT*performanceFromOntimeTasks)) ;

        context.write(key, new Text(", Total Tasks Assigned: " + totalTasksAssigned +
                ", Total Tasks Completed: " + totalTasksCompleted +
                ", Ontime Tasks Completed: " + totalOntimeTasksCompleted +
                ", Performance (Tasks): " + performanceFromTasks + "%" +
                ", Performance (Ontime Tasks): " + performanceFromOntimeTasks + "%" +
                ", Overall Performance: " + overallPerformance + "%"));
    }
}
