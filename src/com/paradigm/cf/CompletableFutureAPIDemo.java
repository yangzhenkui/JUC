package com.paradigm.cf;

import java.sql.Time;
import java.util.concurrent.*;

//对CompletableFuture中的API进行调用演示
public class CompletableFutureAPIDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        m1();
//        m2();
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {
            return f + 1;
        }).thenApply(f -> {
            return f + 2;
        }).thenAccept(r -> System.out.println(String.format("result: %s", r)));

    }

    //对计算结果进行处理
    private static void m2() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            System.out.println("------1");
            return 1;
        }).handle((f, e) -> {
            System.out.println("------2");
            return f + 2;
        }).handle((f, e) -> {
            System.out.println("-------3");
            return f + 3;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println(String.format("result: %s", v));
            }
        }).exceptionally(e -> {
            System.out.println(String.format("exception: " + e.getMessage()));
            return null;
        }).join());
        //join与get的作用是一样的，都是获得异步任务的结果，但是区别是get会阻塞，join不会
    }


    //获得结果和触发计算
    private static void m1() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(5), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }, executor);

//        System.out.println(future.get());   // 不见不散，依然会造成阻塞
//        System.out.println(future.get(2L, TimeUnit.SECONDS));  //相当于过时不侯，并且会报错
//        System.out.println(future.getNow(9999));   //如果异步任务还未执行完，则获取给定的结果，9999
        TimeUnit.SECONDS.sleep(4);
        System.out.println(future.complete(-44) + "\t" + future.get());  //complete判断是否完成，要是返回ture时，则会将值赋给异步任务

        executor.shutdown();  //关闭线程池，拒绝新任务且将已运行的任务执行完
    }
}
