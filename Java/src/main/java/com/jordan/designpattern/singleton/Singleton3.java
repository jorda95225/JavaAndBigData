package com.jordan.designpattern.singleton;

/**
 * @autheor masheng
 * @description 静态内部类
 * @date 2020/4/27
 */
public class Singleton3 {
    private static class SingletonHolder{
        private static final Singleton3 singleton3 = new Singleton3();
    }

    public Singleton3() {
    }

    public static final Singleton3 getInstance(){
        return SingletonHolder.singleton3;
    }
}