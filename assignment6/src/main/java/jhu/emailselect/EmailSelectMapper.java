package jhu.emailselect;

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
public class EmailSelectMapper extends Mapper<AvroKey<EmailSimple>, NullWritable,
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
	String type = context.getConfiguration().get("type");
	
        EmailSimple email = key.datum();
	
	if (type.equals("time")) {
		String start = context.getConfiguration().get("start");
		String end = context.getConfiguration().get("end");
		Long date = email.getDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
Instant i = Instant.ofEpochSecond(date);
ZonedDateTime dateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());                
String formattedDate = dateTime.format(formatter);
		try {
		Long end_l = 0L;
		Long start_l = 0L;
		if (end != null){
		 	try {
            			LocalDateTime localdatetime = LocalDateTime.parse(end);
            			end_l = localdatetime.toEpochSecond(ZoneOffset.UTC);

        		} catch (Exception e) {
        
        			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        			LocalDate ld = LocalDate.parse(end, dateformatter);
        			end_l = ld.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        		}
		}
		if (start != null){
		 try {
            LocalDateTime localdatetime = LocalDateTime.parse(start);
            start_l = localdatetime.toEpochSecond(ZoneOffset.UTC);

        } catch (Exception e) {
        
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(start, dateformatter);
        start_l = ld.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        }		

}
		if (start == null && end != null) {
			if (date - end_l <= 0) 
				context.write(new Text (formattedDate), One);

		} else if (start != null && end == null) {
                        if (start_l - date <= 0)
                                context.write(new Text (formattedDate), One);
		} else if (start != null && end != null) {
			if ((date - end_l <=0) && (start_l - date <= 0))
				context.write(new Text (formattedDate), One);	
		}
		} catch (Exception e) {context.write(new Text(e.toString()), One);}
	} else if (type.equals("address")) {
		String from_pat = "(.*)" + context.getConfiguration().get("from") + "(.*)";
		String to_pat = "(.*)" + context.getConfiguration().get("to")+ "(.*)";
		String cc_pat = "(.*)" + context.getConfiguration().get("cc")+ "(.*)";
		String and = context.getConfiguration().get("and");
		CharSequence from = email.getFrom();
		List<CharSequence> to = email.getTo();
	        List<CharSequence> cc = email.getCc();
		if (from_pat == null) 
			from_pat = "";
		if (to_pat == null) 
			to_pat = "";
		if (cc_pat == null) 
			cc_pat = "";
		
		Boolean match_from = from.toString().matches(from_pat);

		List<String> match_to_emails = new ArrayList<>();
		List<String> match_cc_emails = new ArrayList<>();
		
		if (to != null){
		for (CharSequence t: to) {
			if(t.toString().matches(to_pat))
				match_to_emails.add(t.toString());
		}}
		if (cc != null) {
		for (CharSequence c: cc) {
			if(c.toString().matches(cc_pat))
				match_cc_emails.add(c.toString());
		}}

		if (and != null && and.equals("true")) {
			if (match_from && match_to_emails.size() > 0 && match_cc_emails.size() > 0){
		if (match_from)
                                context.write(new Text(email.getFrom().toString()), One);
                        if (match_to_emails.size() > 0){
                                for (String to_email: match_to_emails)
                                        context.write(new Text(to_email), One);
                        }
                        if (match_cc_emails.size() > 0) {
                                for (String cc_email: match_cc_emails)
                                        context.write(new Text(cc_email), One);
                        }
		}
	
		} else {
			if (match_from)
				context.write(new Text(email.getFrom().toString()), One);
			if (match_to_emails.size() > 0){
				for (String to_email: match_to_emails)
					context.write(new Text(to_email), One);
			}
			if (match_cc_emails.size() > 0) {
				for (String cc_email: match_cc_emails)
					context.write(new Text(cc_email), One);	
			}	
	
		} 
	} else if (type.equals("subject")) {
		String sub_pattern = "(.*)" + context.getConfiguration().get("pattern") + "(.*)";
		CharSequence subject = email.getSubject();
		if (subject.toString().matches(sub_pattern)) 
			context.write(new Text(email.toString()), One);
	} else if (type.equals("body")) {
		String bod_pattern = "(.*)" + context.getConfiguration().get("pattern") + "(.*)";
		CharSequence body = email.getBody();
		if (body.toString().matches(bod_pattern)) 
			context.write(new Text(email.toString()), One);
	}
    }
}

