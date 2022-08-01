package com.paradigm.cf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

//演示FutureTask的使用，自己演示时注意根据自己的需求进行注释代码

public class FutureTaskDemo {
    //FutureTask的三种获取结果方式：
    //1、get   不见不散  会造成阻塞
    //2、get(param1, param2)     过时不侯，到点获取不到结果时会报错
    //3、isDone  轮询操作，可以避免阻塞

    public static void main(String[] args) throws Exception {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(() -> {
            System.out.println(Thread.currentThread().getName() + " come in");
            TimeUnit.SECONDS.sleep(5);
            return 1024;
        });

        new Thread(futureTask, "t1").start();  //启动子线程去完成任务

        //System.out.println(futureTask.get());  //去获取子线程返回的结果，但是会造成阻塞

        //表示最多等多久就不等了  参数1是数字，参数2是单位  要是没有获取到时会报错
        System.out.println(futureTask.get(6L, TimeUnit.SECONDS));


        //通过轮询的方式来尽量避免阻塞
        while(true){
            if(futureTask.isDone()){
                System.out.println("futureTask的结果为 " + futureTask.get());
                break;
            }else{
                System.out.println("先别催了....");
            }
        }
        System.out.println(Thread.currentThread().getName() + " task is over!!");
    }
}
