package com.paradigm.cf;

import java.sql.Time;
import java.util.concurrent.*;

//对CompletableFuture中的API进行调用演示
public class CompletableFutureAPIDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        m1();
//        m2();
//        m3();   //对计算结果进行消费
//        m4();   //对计算速度进行选用
//        m5();   //对任务A与任务B的计算结果进行合并
    }


    //对计算结果进行合并
    private static void m5() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 10;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 20;
        }), (r1, r2) -> {
            return r1 + r2;
        }).join());
    }

    //对计算速度进行选用
    private static void m4() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        }), r -> {
            return r;
        }).join());
    }

    //对计算结果进行消费
    private static void m3() {
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {    //thenApply表示计算结果存在依赖，串行化执行，有异常时会被叫停
            return f + 1;
        }).thenApply(f -> {
            return f + 2;
        //thenAccept表示对计算结果进行消费并无返回结果
        }).thenAccept(r -> System.out.println(String.format("result: %s", r)));

        //Code之任务间的顺序执行
        // 1、thenRun 任务A执行完任务B， 且B不需要A的结果
        // 2、thenAccept  A执行完到B，且B需要A的返回值，但B无返回值
        // 3、thenApply   A执行完到B，且B需要A的返回值，B也有返回值
        System.out.println(CompletableFuture.supplyAsync(() -> "return A").thenRun(() -> {}).join());
        System.out.println(CompletableFuture.supplyAsync(() -> "return A").thenAccept(f -> {}).join());
        System.out.println(CompletableFuture.supplyAsync(() -> "return A").thenApply(f -> f + " return B").join());
    }

    //对计算结果进行处理
    private static void m2() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            System.out.println("------1");
            return 1;
        }).handle((f, e) -> {  //handle方法可以处理异常
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
