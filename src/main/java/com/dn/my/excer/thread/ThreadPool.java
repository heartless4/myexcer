package com.dn.my.excer.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * @ClassName : ThreadPool
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/15 13:23
 * @Version 1.0
 **/
public class ThreadPool {
    public static void main(String args[]){
        Callable callable= Executors.callable(()->{
            System.out.println("test...");
            System.out.println("end");
                }

        );
        try {
            Object obj=callable.call();
            System.out.println("obj="+obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
