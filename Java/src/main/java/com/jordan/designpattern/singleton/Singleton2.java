package com.jordan.designpattern.singleton;

/**
 * @autheor masheng
 * @description 饿汉式
 * @date 2020/4/27
 */
public class Singleton2 {

    //提前创建
    private static Singleton2 singleton = new Singleton2();

    public Singleton2(){

    }

    public static Singleton2 getInstance(){
        return singleton;
    }
}