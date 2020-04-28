package com.jordan.aggreagate;

import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * @autheor masheng
 * @description 自定义聚合函数
 * @date 2020/4/28
 */
public class MyCountAggregate implements AggregateFunction<ProductViewData, Long, Long> {
    @Override
    public Long createAccumulator() {
        //初始化为0
        return 0L;
    }

    @Override
    public Long add(ProductViewData value, Long accumulator) {
        return accumulator + 1;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}