package com.paradigm.locks;

import java.util.concurrent.TimeUnit;

// 演示死锁
// 死锁是因为两个或两个以上的线程在执行过程中因争夺资源而造成的一种互相等待的现象。
// 死锁产生的主要原因是系统资源不足、进程运行推进顺序不合适、资源分配不当
// 排查死锁的方法：
//  1、 命令行：⭕️ jps -l 找出进程编号  ⭕️jstack 进程编号查看是否存在死锁
//  2、 图形化：jconsole   可以直接检测是否存在死锁
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
