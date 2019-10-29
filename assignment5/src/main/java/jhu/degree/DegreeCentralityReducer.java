package jhu.degree;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 *  * Created by wilsopw1 on 2/25/17.
 *   */
public class DegreeCentralityReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
 	String ct = context.getConfiguration().get("ct");
	String tt = context.getConfiguration().get("tt");

        int sum_out = 0;
	int sum_to = 0;
	int sum_cc = 0;
        for(Text i : values) {
            if (i.toString().equals("to")) {
		sum_to += 1;
	    } else if (i.toString().equals("out")) {
		sum_out += 1;
	    } else if (i.toString().equals("cc")) {
	   	sum_cc += 1;
	    }
        }
        if (sum_cc >= Integer.parseInt(ct) && sum_to >= Integer.parseInt(tt)) { 
	context.write(new Text(key), new Text(Integer.toString(sum_to + sum_cc) + " " + Integer.toString(sum_out)));
	}
    }
}
