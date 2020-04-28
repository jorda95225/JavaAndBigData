package com.jordan.designpattern.chainofresponsibility;

/**
 * @autheor masheng
 * @description 任务链
 * @date 2020/4/27
 */
public class LiabilityChain {

    public void runChain(){
        //将所有要执行的任务按照指定顺序串联起来
        Task task3 = new Task1();
        Task task2 = new Task2(task3);
        Task task1 = new Task3(task2);
        task1.run();
    }
}