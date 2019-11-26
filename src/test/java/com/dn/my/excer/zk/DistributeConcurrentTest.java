package com.dn.my.excer.zk;

import com.dn.my.excer.zk.originalclient.OrderServiceImplWithDistributionLock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @ClassName : DistributeConcurrentTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/7 16:48
 * @Version 1.0
 **/
public class DistributeConcurrentTest {
    public static void main(String args[]){
        // 服务集群数
        int service=5;
        // 并发数
        int requestSize=10;
        CyclicBarrier cyclicBarrier=new CyclicBarrier(service*requestSize);
        // 多线程模拟高并发
        for(int i=0;i<service;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 模拟分布式集群的场景
                    OrderServiceImplWithDistributionLock orderServiceImplWithDistributionLock=new OrderServiceImplWithDistributionLock();
                    System.out.println(Thread.currentThread().getName() + "---------我准备好---------------");
                    for(int j=0;j<requestSize;j++){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 等待service台服务，requestSize个请求 一起出发
                                    cyclicBarrier.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (BrokenBarrierException e) {
                                    e.printStackTrace();
                                }
                                orderServiceImplWithDistributionLock.createOrder();
                            }
                        }).start();
                    }
                }
            }).start();


        }
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
