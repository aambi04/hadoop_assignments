package jhu.emailhistogram;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jhu.avro.EmailSimple;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.LocalDate;

/**
 * Created by wilsopw1 on 2/25/17.
 */
public class EmailHistogramMapper extends Mapper<AvroKey<EmailSimple>, NullWritable,
        Text, IntWritable> {
    Gson gson = new GsonBuilder().create();
    String inputPath = null;
    IntWritable One = new IntWritable(1);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void map(AvroKey<EmailSimple> key, NullWritable value, Context context) throws IOException, InterruptedException {
	String binType = context.getConfiguration().get("binType");
	
        EmailSimple email = key.datum();
	
	if (binType.equals("hour")) {
		Long date = email.getDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
		Instant i = Instant.ofEpochSecond(date);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());                
		String formattedDate = dateTime.format(formatter);

		context.write(new Text(formattedDate), One);
	} else if (binType.equals("day")) {
                Long date = email.getDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Instant i = Instant.ofEpochSecond(date);
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
                String formattedDate = dateTime.format(formatter);

                context.write(new Text(formattedDate), One);
        } else if (binType.equals("month")) {
                Long date = email.getDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                Instant i = Instant.ofEpochSecond(date);
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
                String formattedDate = dateTime.format(formatter);

                context.write(new Text(formattedDate), One);
        } else if (binType.equals("year")) {
                Long date = email.getDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
                Instant i = Instant.ofEpochSecond(date);
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
                String formattedDate = dateTime.format(formatter);

                context.write(new Text(formattedDate), One);
        }



    }
}

