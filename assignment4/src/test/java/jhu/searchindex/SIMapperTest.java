package jhu.searchindex;

import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class SIMapperTest {
    MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;

    @Before
    public void setUp() throws Exception {
        SIMapper mapper = new SIMapper();
        mapDriver = MapDriver.newMapDriver(mapper);
    }

    @Test
    public void testMapper() throws IOException {
        mapDriver.withInput(new LongWritable(), new Text("hello world"));
        mapDriver.withOutput(new Text("hello somefile"), new IntWritable(0));
        mapDriver.withOutput(new Text("world somefile"), new IntWritable(6));
        mapDriver.runTest();
    }

    @Test
    public void testMapper_special() throws IOException {
        mapDriver.withInput(new LongWritable(), new Text("hello!!!!! world"));
        mapDriver.withOutput(new Text("hello!!!!! somefile"), new IntWritable(0));
        mapDriver.withOutput(new Text("world somefile"), new IntWritable(11));
        mapDriver.runTest();
    }

    @Test
    public void testMapper_number() throws IOException {
        mapDriver.withInput(new LongWritable(), new Text("hello 9w98wworld"));
        mapDriver.withOutput(new Text("hello somefile"), new IntWritable(0));
        mapDriver.withOutput(new Text("9w98wworld somefile"), new IntWritable(6));
        mapDriver.runTest();
    }

    @Test
    public void testMapper_stopword() throws IOException {
        mapDriver.withInput(new LongWritable(), new Text("hello a the world"));
        mapDriver.withOutput(new Text("hello somefile"), new IntWritable(0));
        mapDriver.withOutput(new Text("a somefile"), new IntWritable(6));
        mapDriver.withOutput(new Text("the somefile"), new IntWritable(8));
        mapDriver.withOutput(new Text("world somefile"), new IntWritable(12));
        mapDriver.runTest();
    }

}
