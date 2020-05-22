package com.jordan;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/30
 */
public class Test {
    public static void main(String[] args) {
        String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println(s);
    }
}