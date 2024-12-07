package mapreducer;



import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PerformanceReducer extends Reducer<Text, Text, Text, Text> {
	
    private static final Logger logger = LogManager.getLogger(PerformanceReducer.class);


    private static final double COMPLETION_WEIGHT = 0.4;
    private static final double ONTIME_COMPLETION_WEIGHT = 0.6;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); // Updated to match HBase date format

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<Record> records = new ArrayList<>();

        // Parse the values into Record objects (which hold the date and task data)
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length == 4) {
                try {
                    Date date = DATE_FORMAT.parse(fields[0]); // Parse date in yyyy-MM-dd format
                    double tasksAssigned = Double.parseDouble(fields[1]);
                    double tasksCompleted = Double.parseDouble(fields[2]);
                    double ontimeTasksCompleted = Double.parseDouble(fields[3]);
                    records.add(new Record(date, tasksAssigned, tasksCompleted, ontimeTasksCompleted));
                } catch (ParseException | NumberFormatException e) {
                    System.err.println("Invalid data format in value: " + value.toString());
                }
            }
        }
        
//        logger.info("Reducer for empId={} has {} records before sorting.", key.toString(), records.size());


        // Sort the records by date in descending order
        records.sort((r1, r2) -> r2.date.compareTo(r1.date));

        // Only consider the most recent 5 records
        List<Record> recentRecords = records.size() > 5 ? records.subList(0, 5) : records;
        
//        logger.info("Reducer for empId={} is processing {} most recent records.", key.toString(), recentRecords.size());


        // Aggregate data from the recent 5 records
        double totalTasksAssigned = 0.0;
        double totalTasksCompleted = 0.0;
        double totalOntimeTasksCompleted = 0.0;

        for (Record record : recentRecords) {
            totalTasksAssigned += record.tasksAssigned;
            totalTasksCompleted += record.tasksCompleted;
            totalOntimeTasksCompleted += record.ontimeTasksCompleted;
            
            logger.debug("Processing Record: Date={}, TasksAssigned={}, TasksCompleted={}, OnTimeTasksCompleted={}",
                    DATE_FORMAT.format(record.date), record.tasksAssigned, record.tasksCompleted, record.ontimeTasksCompleted);
        }

        // Calculate performance
        double performanceFromTasks = totalTasksAssigned > 0 ? (totalTasksCompleted / totalTasksAssigned) * 100 : 0.0;
        double performanceFromOntimeTasks = totalTasksAssigned > 0 ? (totalOntimeTasksCompleted / totalTasksAssigned) * 100 : 0.0;
        double overallPerformance = (COMPLETION_WEIGHT * performanceFromTasks) + (ONTIME_COMPLETION_WEIGHT * performanceFromOntimeTasks);

        
//        logger.info("Calculated Performance for empId={}: {}", key.toString(), overallPerformance);

        // Write the result for the employee
        context.write(key, new Text(", Total Tasks Assigned: " + totalTasksAssigned +
                ", Total Tasks Completed: " + totalTasksCompleted +
                ", Ontime Tasks Completed: " + totalOntimeTasksCompleted +
                ", Performance (Tasks): " + performanceFromTasks + "%" +
                ", Performance (Ontime Tasks): " + performanceFromOntimeTasks + "%" +
                ", Overall Performance: " + overallPerformance + "%"));
    }

    // Record class to hold task data
    public static class Record {
        Date date;
        double tasksAssigned;
        double tasksCompleted;
        double ontimeTasksCompleted;

        public Record(Date date, double tasksAssigned, double tasksCompleted, double ontimeTasksCompleted) {
            this.date = date;
            this.tasksAssigned = tasksAssigned;
            this.tasksCompleted = tasksCompleted;
            this.ontimeTasksCompleted = ontimeTasksCompleted;
        }
    }
}
