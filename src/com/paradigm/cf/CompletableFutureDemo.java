package com.paradigm.cf;


import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


//CompletableFuture实现了CompletionStage和Future，是Future功能的一个扩展，帮助我们简化异步编程
//CompletionStage相当于linux中的管道分隔符，表示的是异步计算中的某个阶段，一个阶段的完成可能会触发另外一个阶段
public class CompletableFutureDemo {

    public static void main(String[] args) {
        //构建线程池以及参数说明
        //非核心线程是在阻塞队列满了之后才会使用到的，而拒绝策略则是当非核心线程均使用完且阻塞队列已满才使用到
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,   //核心线程数
                5,   //最大工作线程数
                2L, TimeUnit.SECONDS,  //空闲线程存活的时间
                new LinkedBlockingDeque<>(5),  //阻塞队列
                Executors.defaultThreadFactory(),   //创建线程的工厂
                new ThreadPoolExecutor.AbortPolicy()   //拒绝策略
                );

    }
}
