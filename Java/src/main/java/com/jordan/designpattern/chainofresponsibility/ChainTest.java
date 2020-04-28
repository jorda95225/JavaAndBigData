package com.jordan.designpattern.chainofresponsibility;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/27
 */
public class ChainTest {
    public static void main(String[] args) {
        //责任链模式按照链的顺序执行一个个处理方法，链上的每一个任务都持有它后面那个任务的对象的引用，方便自己这段
        //代码执行完毕之后，调用后面的处理的逻辑
        //task3 is running
        //task2 is running
        //task1 is running
        LiabilityChain chain = new LiabilityChain();
        chain.runChain();
    }
}