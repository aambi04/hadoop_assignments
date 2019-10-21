package jhu.searchindex;

import jhu.searchindex.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by wilsopw1 on 2/25/17.
 */
public class SIReducer extends Reducer<Text, IntWritable, Text, Text> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        
        for(IntWritable i : values) {
           sb.append(String.valueOf(i.get()) + " ");
        }
        context.write(new Text(key), new Text(sb.toString()));
    }
}
