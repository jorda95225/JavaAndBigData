package com.jordan.designpattern.singleton;

/**
 * @autheor masheng
 * @description 双重校验锁
 * @date 2020/4/27
 */
public class Singleton5 {
    private volatile static Singleton5 singleton5;

    public Singleton5() {
    }

    public static Singleton5 getInstance(){
        if (singleton5 == null){
            synchronized (Singleton5.class){
                if (singleton5 == null){
                    singleton5 = new Singleton5();
                }
            }
        }
        return singleton5;
    }
}