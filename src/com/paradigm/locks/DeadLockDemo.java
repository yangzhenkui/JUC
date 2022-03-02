package com.paradigm.locks;

import java.util.concurrent.TimeUnit;

//演示死锁
public class DeadLockDemo {
    static Object lockA = new Object();
    static Object lockB = new Object();

    //相互持有造成了死锁现象
    public static void main(String[] args) {
        new Thread(() ->{
            synchronized (lockA){
                System.out.println("获得锁A，期待锁B.....");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lockB){
                    System.out.println("获得锁B....");
                }
            }
        }).start();


        new Thread(() -> {
            synchronized (lockB){
                System.out.println("获得锁B，期待锁A.....");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lockA){
                    System.out.println("获得锁A....");
                }
            }
        }).start();
    }
}
