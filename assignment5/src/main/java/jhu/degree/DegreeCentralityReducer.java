package jhu.degree;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by wilsopw1 on 2/25/17.
 */
public class DegreeCentralityReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int sum_in = 0;
	int sum_out = 0;
        for(Text i : values) {
            if (i.toString().equals("out")) {
		sum_out += 1;
	    } else if (i.toString().equals("in")) {
		sum_in += 1;
	    }
        }
        context.write(new Text(key), new Text(Integer.toString(sum_in) + " " + Integer.toString(sum_out)));
    }
}
