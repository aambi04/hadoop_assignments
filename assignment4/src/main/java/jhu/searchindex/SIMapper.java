package jhu.searchindex;

import jhu.searchindex.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

/**
 * Created by wilsopw1 on 2/25/17.
 */
public class SIMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    String inputPath = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration information
        this.inputPath = context.getConfiguration().get("inputPath");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String filename = "";
	if(key.get() == 0) {
            // this is the first line of the file
            // Which files are we processing?
            filename = ((FileSplit) context.getInputSplit()).getPath().getName();
            context.getCounter("FILE", filename).increment(1);
        }
        // find words
        //String[] words = value.toString().split("\\s+");
        String str_value = value.toString();
        int ind;
        int offset = 0;
        int ind_trav = 0;
        while (ind_trav < str_value.length()) {
                offset = ind_trav;

                String temp = "";
                char trav = str_value.charAt(ind_trav);
                while(trav != ' '){
                    // concatanate the characters of the word
                    temp += trav;
                    ind_trav += 1;
                    if (ind_trav < str_value.length()) {
                        trav = str_value.charAt(ind_trav);
                    } else {
                        break;
                    }
                }
    		String updated_key = temp + " " + filename;
 
                context.write(new Text(updated_key), new IntWritable(offset));
                
                while(trav == ' '){
                    ind_trav += 1;
                    if (ind_trav < str_value.length()) {
                        trav = str_value.charAt(ind_trav);
                    } else {
                        break;
                    }
                }
                
	}
    }    
}
