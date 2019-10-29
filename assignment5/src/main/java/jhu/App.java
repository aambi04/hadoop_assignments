package jhu;

import jhu.enron.EnronDriver;
import jhu.graph.GraphEnronDriver;
import jhu.degree.DegreeCentralityDriver;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.PropertyConfigurator;

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

    int runEnron(String input, String output) throws Exception {
        Configuration conf = getConf();
        conf.set("inputPath", input);
        conf.set("outputPath", output);
        return ToolRunner.run(conf, new EnronDriver(), new String[]{});
    }
    int runEronGraph(String input, String output) throws Exception {
        Configuration conf = getConf();
        conf.set("inputPath", input);
        conf.set("outputPath", output);
        return ToolRunner.run(conf, new GraphEnronDriver(), new String[]{});
    }

   int runDegreeCentrality(String input, String output) throws Exception {
        Configuration conf = getConf();
        conf.set("inputPath", input);
        conf.set("outputPath", output);
System.out.printf("HERE");    
    return ToolRunner.run(conf, new DegreeCentralityDriver(), new String[]{});
    }

   int runDegreeCentralityOptions(String[] arguments) throws Exception {
	Configuration conf = getConf();
	int i = 1;
	String options;
	for (String temp: arguments) {
		System.out.printf(temp + " ");
}
        while (i < arguments.length && arguments[i].startsWith("-")) {
		options = arguments[i];
		i += 1;
		System.out.printf("options %s, ", options);	
	if (options.equals("-tt")) {
			conf.set("tt", arguments[i]);	
	} else if (options.equals("-ct")) {
			conf.set("ct", arguments[i]);
		}
	i += 1;
	}
 	
	conf.set("inputPath", arguments[i]);
	conf.set("outputPath", arguments[i+1]);
	
	return ToolRunner.run(conf, new DegreeCentralityDriver(), new String[]{});
	}

    void showUsage() {
        System.out.println("Usage: ");
        System.out.println("\tenron-stats <inputPath> <outputPath>\trun the enron statistics mapreduce job");
    }

    public int run(String[] strings) throws Exception {
        if(strings.length > 0) {
            if (strings[0].equals("enron-stats") && strings.length == 3) {
                return runEnron(strings[1], strings[2]);
            }
	    else if (strings[0].equals("enron-graph") && strings.length == 3) {
	    	return runEronGraph(strings[1], strings[2]);
	    } else if (strings[0].equals("degree-centrality") && strings.length <= 3) {
		return runDegreeCentrality(strings[1], strings[2]);
	} else if (strings[0].equals("degree-centrality") && strings.length > 3) {
		return runDegreeCentralityOptions(strings);
	}
}
	showUsage();

        return -1;
    }
}
