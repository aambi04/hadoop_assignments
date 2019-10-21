package jhu.searchindex;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *  * Created by wilsopw1 on 2/26/17.
 *   */
public class SIDriverTest {
    MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, Text> mapReduceDriver;

    @Before
    public void setUp() throws Exception {
        SIMapper mapper = new SIMapper();
        SIReducer reducer = new SIReducer();
        mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);

    }

    @Test
    public void run() throws Exception {
        mapReduceDriver.withInput(new LongWritable(), new Text("hello world hello world"));
        mapReduceDriver.withInput(new LongWritable(), new Text("hadoop world hadoop world"));
        mapReduceDriver.withOutput(new Text("hadoop somefile"), new Text("0 13 "));
        mapReduceDriver.withOutput(new Text("hello somefile"), new Text("0 12 "));
        mapReduceDriver.withOutput(new Text("world somefile"), new Text("6 18 7 20 "));
        mapReduceDriver.runTest();
    }


}

