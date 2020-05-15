package com.jordan.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

/**
 * @autheor masheng
 * @description HDFS客户端操作测试
 * @date 2020/5/8
 */
public class HdfsClient {

    @Test
    public void testHdfsClient() throws IOException, InterruptedException {
        FileSystem fileSystem = FileSystem.get(URI.create("hdfs://hadoop11:9820"), new Configuration(), "root");
        fileSystem.mkdirs(new Path("/testHDFS"));
        fileSystem.close();
    }
}