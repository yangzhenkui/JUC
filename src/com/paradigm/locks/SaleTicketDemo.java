package com.paradigm.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//乐观锁和悲观锁总结：
//  乐观锁：适用于读多写少的场景，是一种无锁算法，有两种实现方式（版本号和CAS机制）
//  悲观锁：适用于写多读少的场景，先加锁能够保证写时数据正确，synchronized和lock都是悲观锁
//  类加载是一个将class字节码文件实例转化为Class对象并进行初始化的过程


//synchronized的应用方式：
//  1、作用于实例方法，相当于对当前的实例对象加锁   属于对象锁
//  2、作用于代码块，对括号内配置的对象加锁        属于对象锁
//  3、作用于静态方法，对当前的类进行加锁          属于类锁
//  synchronized同步代码块使用的是monitorenter和monitorexit指令
//  每一个对象都天生的带着一个对象监视器（monitor）
//  synchronized必须作用于每一个对象中，在对象的头文件中存储了锁的相关信息。
//  锁升级的主要功能依赖于markword中的锁标志位和释放偏向锁标志位

public class SaleTicketDemo {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        new Thread(() -> {for (int i = 0; i < 55; i++) ticket.sale();}, "a").start();
        new Thread(() -> {for (int i = 0; i < 55; i++) ticket.sale();}, "b").start();
        new Thread(() -> {for (int i = 0; i < 55; i++) ticket.sale();}, "c").start();
        new Thread(() -> {for (int i = 0; i < 55; i++) ticket.sale();}, "d").start();
        new Thread(() -> {for (int i = 0; i < 55; i++) ticket.sale();}, "e").start();
    }
}


//资源类
class Ticket{
    private int number = 50;
    private Lock lock = new ReentrantLock();  //默认是非公平锁
    // 公平锁和非公平锁的区别
    // !hasQueuedPredecessors()  主要区别是有无此判断：即是判断是否有先驱节点

    // 使用公平锁的话，会存在线程的挂起以及恢复，造成一些时间的浪费，而非公平锁相比之下能够更加充分的利用CPU，减少CPU的空闲状态
    // 除此之外，由于非公平锁释放同步状态时不需要考虑是否还有前驱节点，因此刚释放锁的线程再次获得锁的概率就变大了，进而减少了锁的开销
    // 使用公平锁由于需要排队，所以可能会导致锁饥饿的问题
    public void sale(){
        lock.lock();

        try {
            if(number > 0){
                System.out.println(Thread.currentThread().getName() + "\t 卖出了第" +  number-- + "张票");
            }
        }finally {
            lock.unlock();  //需要保证一定能被解锁！！
        }
    }
}
