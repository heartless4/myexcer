package com.dn.my.excer.thread;

/**
 * @ClassName : ThreadLocalTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/8 21:24
 * @Version 1.0
 **/
public class ThreadLocalTest {
    private static ThreadLocal value=new ThreadLocal();

    public static void main(String args[]){
        new Thread(new Runnable() {
            @Override
            public void run() {
                value.set("aaa");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("value get="+value.get());
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2 zhong get value="+value.get());
            }
        }).start();
    }
}
