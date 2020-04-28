package com.jordan.designpattern.chainofresponsibility;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/27
 */
public class Task3 implements Task{

    private Task task;

    public Task3() {
    }

    public Task3(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println("task3 is running");
        if (task != null){
            task.run();
        }
    }
}