package com.dn.my.excer.thread;

/**
 * @ClassName : multithread.ThreadState
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/7 19:31
 * @Version 1.0
 **/
public class ThreadState {
    public static void main(String args[]){
        test3();
    }
    public static void test3(){
        Thread thread3=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("1.thread3 state:"+Thread.currentThread().getState().toString());
                synchronized (ThreadState.class){
                    System.out.println("2.thread3 state:"+Thread.currentThread().getState().toString());
                }
            }
        });
        synchronized (ThreadState.class){
            System.out.println("main get lock,thread3 state:"+thread3.getState().toString());
            thread3.start();
            System.out.println("3.thread3 state:"+thread3.getState().toString());
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("5.thread3 state:"+thread3.getState().toString());
        }
        System.out.println("4.thread3 state:"+thread3.getState().toString());
    }
}

