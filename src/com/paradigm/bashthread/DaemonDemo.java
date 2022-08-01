package com.paradigm.bashthread;


import java.sql.Time;
import java.util.concurrent.TimeUnit;

//演示守护线程和用户线程的区别
//守护线程是一种特殊的线程，不由程序员进行控制，例如垃圾回收线程
//用户线程则是系统的工作线程，用于完成该程序需要的业务操作的
public class DaemonDemo {

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " come in: \t"
            + ((Thread.currentThread().isDaemon()) ? "守护线程" : "用户线程"));
            while(true){

            }
        });
        //如果不是守护线程的话，那么会因为while语句而一直暂停着
        // 而如果t是守护线程时，那么当main线程结束后，整个程序也就结束了
        //所以可以得出结论：当所有用户线程执行结束后，无论守护线程是否结束，系统都会自动退出
        t.setDaemon(true);   // 设置守护线程需要在start之前，否则无效
        t.start();

        //用户线程代码了业务需求，自然当业务完成时，程序也就退出了

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " task is over!!!");
    }
}
