package com.dn.my.excer.thread;

/**
 * @ClassName : InteruptFlag
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/8 21:07
 * @Version 1.0
 **/
public class InteruptFlag {
    private static boolean flag=true;
    public static void main(String args[]){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
                    System.out.println("running。。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag=false;
        System.out.println("end...");
    }
}
