package com.paradigm.locks;


import com.sun.prism.shader.Solid_TextureYV12_Loader;

import java.util.concurrent.locks.ReentrantLock;

// 可重入锁的演示：可重入锁是指一个线程在外层获得锁后，再进入该线程的内层方法会自动获得锁（前提是锁对象需要是同一个）
// 可重入锁在一定程度上能够避免死锁

// synchronized实现可重入锁的原理是因为每一个锁对象都有一个锁计数器和一个指向持有该锁的线程的指针
// 若计数器为0，则说明锁已经被释放
public class ReEntryLockDemo {
    public static void main(String[] args) {

        //Lock锁的演示
        ReentrantLock lock = new ReentrantLock();

        new Thread(() -> {
            lock.lock();

            try {
                System.out.println(Thread.currentThread().getName() + " ----外层");
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " -----内层");
                }finally {
                    lock.unlock();
                }
            }finally {
                lock.unlock();  //将其注释之后会导致b线程无法获取到锁而一直等待
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try{
                System.out.println("b 线程调用lock-ing....");
            }finally {
                lock.unlock();
            }
        }, "b").start();

        SyncBlock();

    }

    //synchronized演示可重入锁
    static Object objectLock = new Object();

    public static  void SyncBlock(){
        new Thread(() -> {
            synchronized (objectLock){
                System.out.println(Thread.currentThread().getName() + "-----外层");
                synchronized (objectLock){
                    System.out.println(Thread.currentThread().getName() + "----中层");
                    synchronized (objectLock){
                        System.out.println(Thread.currentThread().getName() + "------内层");
                    }
                }
            }
        }, "t2").start();
    }
}
