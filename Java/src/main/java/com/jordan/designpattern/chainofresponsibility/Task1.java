package com.jordan.designpattern.chainofresponsibility;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/27
 */
public class Task1 implements Task{

    private Task task;

    public Task1() {
    }

    public Task1(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println("task1 is running");
        if (task != null){
            task.run();
        }
    }
}