package jhu.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wilsopw1 on 2/25/17.
 */
public class GraphEnronStatsMapper extends Mapper<LongWritable, Text, Text, Text> {
    Gson gson = new GsonBuilder().create();
    String inputPath = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration information
        this.inputPath = context.getConfiguration().get("inputPath");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        if(key.get() == 0) {
            // this is the first line of the file
            // Which files are we processing?
            String filename = ((FileSplit) context.getInputSplit()).getPath().getName();
            context.getCounter("FILE", filename).increment(1);
        }

        // decode json
        EmailMessage message = gson.fromJson(value.toString(), EmailMessage.class);
        String sender = "";
	for(String k : message.header.keySet()) {
            if(k.equals("From"))
            	sender = ((String) message.header.get(k));
	     else if(k.equals("To")) {
                List<String> emails = (List<String>) message.header.get(k);
		for (String e : emails) {
			context.write(new Text(sender + " " + e), new Text("to"));
	      }
		} else if (k.equals("Cc")) {
		List<String> emails_cc = (List<String>) message.header.get(k);
                for (String e_cc : emails_cc) {
			context.write(new Text(sender + " " + e_cc), new Text("cc"));
		}     
		}	
        }
   }
}
