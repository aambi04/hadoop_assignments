package jhu.searchindex;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *  * Created by wilsopw1 on 2/26/17.
 *   */
public class SIReducerTest {
    ReduceDriver<Text, IntWritable, Text, Text> reduceDriver;

    @Before
    public void setUp() throws Exception {
       	SIReducer reducer = new SIReducer();
        reduceDriver = ReduceDriver.newReduceDriver(reducer);
    }

    @Test
    public void reduce() throws Exception {
        List<IntWritable> values = new ArrayList<IntWritable>();
        values.add(new IntWritable(0));
        values.add(new IntWritable(12));
        reduceDriver.withInput(new Text("Howdy somefile"), values);
        reduceDriver.withOutput(new Text("Howdy somefile"), new Text("0 12 "));
        reduceDriver.runTest();
    }

}

