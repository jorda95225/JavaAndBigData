package com.jordan.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @autheor masheng
 * @description reducer
 * @date 2020/5/8
 */
public class WordCountReducer extends Reducer<Text, IntWritable,Text, IntWritable> {
    int sum;
    IntWritable v = new IntWritable();
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        sum = 0;
        for (IntWritable count : values) {
            sum += count.get();
        }
        v.set(sum);
        context.write(key,v);
    }
}