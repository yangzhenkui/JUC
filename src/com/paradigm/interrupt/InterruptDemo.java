package com.paradigm.interrupt;




import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
// 为什么出现中断？因为一个线程不应该又其他线程来强制的中断或者停止，而应该由自身来选择中断还是停止
// 所以Java中提供了一种用于停止线程的机制——中断，但中断只是一种协作方式，具体的过程还需要程序员自己来进行实现
// 手动调用线程的interrupt方法时，该方法仅仅是将中断标志设为true，所以需要自己写代码来检测标志位，同时作出对应的业务处理。

// 中断相关的api方法：
//  1、public void interrupt()  该方法仅仅是将标志位设为true
//  2、public static boolean interrupted()  该方法是判断线程是否被中断，并且清除当前的中断标识
         // 即做了两件事：🐩 返回当前线程的中断状态  🐩 将中断状态标识设为false
//  3、public boolean isInterrupted()     该方法主要是判断线程是否被中断，不清除状态
public class InterruptDemo {
    static volatile boolean isStop = false;   //volatile 主要是保证可见性和有序性(禁止指令重排)
    private final static AtomicBoolean atomicBoolean = new AtomicBoolean(true);
    public static void main(String[] args) throws InterruptedException {
        // 如何使用中断标识停止线程？
//        m1();  // 通过一个volatile变量来进行实现

//        m2();   // 通过AtomicBoolean类来进行实现

//        m3();  // 通过Thread自带的api来进行实现

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("-------" + i);
            }
            System.out.println("after t1.interrupt() ---2th---" + Thread.currentThread().isInterrupted());
        }, "t1");
        t1.start();

        System.out.println("before t1.interrupt()-----" + t1.isInterrupted());
        t1.interrupt();
        TimeUnit.MILLISECONDS.sleep(3);

        System.out.println("after t1.interrupt() --- 1th---" + t1.isInterrupted());
        TimeUnit.MILLISECONDS.sleep(3000);
        System.out.println("after t1.interrupt() --- 3th--" + t1.isInterrupted());

    }

    // 通过Thread自带的api来进行实现
    // 注： 调用interrupt之后发生了什么？
    //   1、如果线程在正常运行中，那么仅将其标识位设为true，至于其他的操作还需要被调用的线程进行配合才行
    //   2、如果线程处于阻塞状态（sleep，wait，join） 那么当在别的线程中调用当前线程对象的interrupt方法时，线程将立即跳出阻塞状态，并且抛出InterruptException异常

    private static void m3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                // isInterrupted() 返回中断状态但并不会清除标识位
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("t1 线程被中断了，break，程序结束");
                    break;
                }
                //System.out.println("hello !");
            }
        },"t1");
        t1.start();

        System.out.println("******before*******" + t1.isInterrupted());  //false
        TimeUnit.SECONDS.sleep(5);
        t1.interrupt();  //将标识位设成true
        System.out.println("******after********" + t1.isInterrupted());   // true
    }

    // 通过AtomicBoolean类来进行实现
    private static void m2() throws InterruptedException {
        new Thread(() -> {
            while(atomicBoolean.get()){
                try{
                    TimeUnit.MILLISECONDS.sleep(500);
                    System.out.println("hello !");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        TimeUnit.SECONDS.sleep(3);
        atomicBoolean.set(false);
    }

    // 通过一个volatile变量来实现中断
    private static void m1() throws InterruptedException {
        new Thread(() -> {
            while (true){
                if(isStop){
                    System.out.println("-------isStop = true, program stop!");
                    break;
                }
                System.out.println(" isStop = false， program continue...");
            }
        }, "t1").start();

        TimeUnit.SECONDS.sleep(1);
        isStop = true;
    }
}
