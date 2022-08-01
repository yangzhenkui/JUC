package com.paradigm.cf;


import java.util.concurrent.*;

// ComnpletableFuture中核心的四个方法
// runAsync(无返回值)以及SupplyAsync(有返回值)  两种有可分为是不是使用默认的线程池
//CompletableFuture的优点：
//    1、异步任务结束时，能够自动的回调某个对象的方法
//    2、异步任务出错时，能够自动回调某个对象的方法
//    3、主线程设置好回调后，就不需要担心异步任务之间的执行
public class CompletableFutureDemo2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        m1();
//        m2();
        m3();
    }

    private static void m3() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " come in....");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = ThreadLocalRandom.current().nextInt(100);
            if (result < 6) {
                int a = 10 / 0;
            }
            return result;
            //whenComplete表示的是对上一个异步的结果进行处理，如果e不为空时，则表示上一步能够正确的结果，否则就是错误的
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println(String.format("result: %s", v));
            }
        }).exceptionally(e -> {
            //当异步任务出现exception时，可以在这儿进行处理
            System.out.println("exception: " + e.getMessage());
            return -44;
        });
        System.out.println(String.format("result: %s", future.get()));

        //避免主线程立即结束，因为主线程结束的话会导致CompletableFuture默认使用的线程池会立即关闭
        TimeUnit.SECONDS.sleep(3);
        executor.shutdown();
    }

    private static void m2() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(5), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " come in...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task is over!");
            return 1024;
        }, executor);

        System.out.println(" 返回值为：" + future.get());
        executor.shutdown();   //关闭线程池，拒绝新任务且执行完已提交的任务
    }

    private static void m1() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " come in!!!");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task is over!!!!");
        }, poolExecutor);

        System.out.println(future.get());  //runAsync无返回值
        poolExecutor.shutdown();  //关闭线程池，拒绝新任务并且将已经运行的任务执行完
    }
}
