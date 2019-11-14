package jhu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jhu.avro.EmailSimple;
import jhu.enron.EmailMessage;
import jhu.enron.EnronDriver;
import jhu.emailselect.EmailSelectDriver;
import jhu.emailhistogram.EmailHistogramDriver;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.*;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;

/**
 * Programming Assignment 2: MapReduce Introduction
 *
 */
public class App extends Configured implements Tool
{
    public static void main( String[] args ) throws Exception {
        int ret = ToolRunner.run(new Configuration(), new App(), args);
        System.exit(ret);
    }

    int writeToAvro(String input, String output) throws IOException {
        FileInputStream fis = new FileInputStream(input);
        DatumWriter<EmailSimple> dr = new SpecificDatumWriter<EmailSimple>(EmailSimple.class);
        DataFileWriter<EmailSimple> dfw = new DataFileWriter<EmailSimple>(dr);
        dfw.create(EmailSimple.SCHEMA$, new File(output));

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        Gson gson = new GsonBuilder().create();

        String line = null;
        int total = 0;
        int dateErrors = 0;
        while ((line = br.readLine()) != null) {
   	    EmailMessage email = gson.fromJson(line, EmailMessage.class);
            EmailSimple emailSimple = email.getEmailSimple();
            if(emailSimple.getDate() == 0L)
                dateErrors++;
            dfw.append(emailSimple);
            total++;
            if((total%1000)==0)
                System.out.printf("%6d records\r",total);
        }
        System.out.printf("%6d records total. %d date parsing errors\n",total, dateErrors);
        dfw.close();
        br.close();

        return 0;
    }

    int runEnronTest(String input, String output) throws Exception {
        Configuration conf = getConf();
        conf.set("testMode", "avroTest");
        return runEnron(input, output);
    }

    int runEnron(String input, String output) throws Exception {
        Configuration conf = getConf();
        conf.set("inputPath", input);
        conf.set("outputPath", output);
        return ToolRunner.run(conf, new EnronDriver(), new String[]{});
    }

    int runEmailSelect(String type, String[] arguments) throws Exception {
	Configuration conf = getConf();

	int i = 2;
	String options;

	conf.set("type", type);
	System.out.println(type);
   	if (type.equals("time")) {
		System.out.printf("time %d %s", arguments.length, arguments[0]);
        	while (i < arguments.length && arguments[i].startsWith("-")) {
			options = arguments[i];
			i += 1;
			System.out.printf("iterateion %s", options);
			if (options.equals("-start")) {
				conf.set("start", arguments[i]);	
			} else if (options.equals("-end")) {
				conf.set("end", arguments[i]);
			}
			i += 1;
		} 
	} else if (type.equals("address")) {
		while (i < arguments.length && arguments[i].startsWith("-")) {
			options = arguments[i];
			i += 1;
			if (options.equals("-from")) {
				conf.set("from", arguments[i]);
				i += 1;	
			} else if (options.equals("-to")) {
				conf.set("to", arguments[i]);
				i += 1;
			} else if (options.equals("-cc")) {
				conf.set("cc", arguments[i]);
				i += 1;
			} else if (options.equals("-and")) {
				conf.set("and", "true");
			}
		}
	} else if (type.equals("subject") || type.equals("body")) {
		conf.set("pattern",arguments[2]);
		i += 1;
	}

 	
	conf.set("inputPath", arguments[i]);
	conf.set("outputPath", arguments[i+1]);
	
	return ToolRunner.run(conf, new EmailSelectDriver(), new String[]{});

    }

	int runEmailHistogram(String bin_type, String input, String output) throws Exception {
		Configuration conf = getConf();
        	conf.set("inputPath", input);
        	conf.set("outputPath", output);
        	conf.set("binType", bin_type);
		return ToolRunner.run(conf, new EmailHistogramDriver(), new String[]{});
		
	}

    void showUsage() {
        System.out.println("Usage: ");
        System.out.println("\tenron-avro <localInputPath> <localOutputPath>\ttranslate json to avro EmailSimple record");
        System.out.println("\tenron-stats <inputPath> <outputPath>\trun the enron statistics mapreduce job");
        System.out.println("\tavro-map <inputPath> <outputPath>\trun the avro map only job");
  	System.out.println("\temail-select -options <inputPath> <outputPath>\trun the email select only job");  
	System.out.println("\temail-histogram bin-type <inputPath> <outputPath>\trun the email histogram only job");

    }

    public int run(String[] strings) throws Exception {
        if(strings.length > 0) {
            if (strings[0].equals("enron-stats") && strings.length == 3) {
                return runEnron(strings[1], strings[2]);
            } else if(strings[0].equals("enron-avro") && strings.length == 3) {
                return writeToAvro(strings[1], strings[2]);
            } else if(strings[0].equals("avro-map") && strings.length == 3) {
                return runEnronTest(strings[1],strings[2]);
            } else if(strings[0].equals("email-select") && strings.length >= 5) {
		return runEmailSelect(strings[1], strings);
	    } else if(strings[0].equals("email-histogram") && strings.length == 4) {

		return runEmailHistogram(strings[1],strings[2], strings[3]);
	}
	}
        showUsage();

        return -1;
    }
}
