package jhu.degree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;


/**
 * Created by wilsopw1 on 2/25/17.
 */
public class DegreeCentralityDriver extends Configured implements Tool {

    public int run(String[] strings) throws Exception {

        Configuration conf = getConf();
        String inputPath = conf.get("inputPath");
        String outputPath = conf.get("outputPath");
	String ct = conf.get("ct");
	String tt = conf.get("tt");
	
	// set ct and tt to 0 by default if they are empty values in cli
	if (ct == null) {
		ct = "0";
		conf.set("ct", "0");
	} 
	if (tt == null) {
		tt = "0";
		conf.set("tt", "0");
	}
        Job job = Job.getInstance(conf, "Degree Centrality Processor");
        job.setJarByClass(getClass());

        job.setMapperClass(DegreeCentralityMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(DegreeCentralityReducer.class);
        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
