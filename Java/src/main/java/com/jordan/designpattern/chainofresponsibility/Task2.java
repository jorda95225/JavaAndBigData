package com.jordan.designpattern.chainofresponsibility;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/27
 */
public class Task2 implements Task{

    private Task task;

    public Task2() {
    }

    public Task2(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println("task2 is running");
        if (task != null){
            task.run();
        }
    }
}