package com.jordan.designpattern.singleton;

/**
 * @autheor masheng
 * @description 单例模式
 * @date 2020/4/27
 */
public class Singleton {

    //1.线程不安全的懒汉式
    private static Singleton singleton;

    public Singleton() {
    }

    public static Singleton getInstance(){
        //使用的时候再创建
        if (singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }

    //2.线程安全的懒汉式(加synchronized)
    public static synchronized Singleton getInstance1(){
        if (singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }
}