package com.paradigm.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CompletableFutureNetMallDemo {

    static List<NetMall> netMalls = Arrays.asList(
            new NetMall("jd"),
            new NetMall("pdd"),
            new NetMall("taobao"),
            new NetMall("tianmao")
    );

    //step by step
    public static List<String> getPriceByStep(List<NetMall> netMalls, String productName){
        return netMalls.stream().map(
                netMall -> String.format(productName + "in %s price is: %.2f", netMall.getMallName(), netMall.calcPrice(productName)))
                .collect(Collectors.toList());   //map主要是对流中的每一个元素进行加工
    }

    // 异步请求，大幅度提高了性能
    public static List<String> getPriceBySync(List<NetMall> netMalls, String productName){
        //先对其中的每一个元素都做异步处理，然后再将异步的结果收集为集合，最后在其中获取计算的结果
        return netMalls.stream().map(netMall ->
            CompletableFuture.supplyAsync(() -> String.format(productName + " in %s price is %.2f", netMall.getMallName(), netMall.calcPrice(productName))))
                .collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
//        List<String> list = getPriceByStep(netMalls, "MySQL");
        List<String> list = getPriceBySync(netMalls, "MySQL");
        list.forEach(System.out::println);
        System.out.println("耗费时间为：" + (System.currentTimeMillis() - start) + "ms");
    }


}

class NetMall{


    private String mallName;

    public NetMall(String mallName){
        this.mallName = mallName;
    }

    public String getMallName() {
        return mallName;
    }

    public double calcPrice(String productName){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}
