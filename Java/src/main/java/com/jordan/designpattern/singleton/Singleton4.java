package com.jordan.designpattern.singleton;

/*
 * 功能描述:枚举
 * 这种方式不仅能避免多线程同步问题，还能防止反序列化重新创建新的对象
 * @author masheng
 * @date 2020/4/27
 * @param null
 * @return
 */
public enum Singleton4 {
    SINGLETON_4;
    public void whateverMethod(){

    }
}
