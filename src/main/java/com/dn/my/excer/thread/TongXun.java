package com.dn.my.excer.thread;

/**
 * @ClassName : TongXun
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/15 18:23
 * @Version 1.0
 **/
public class TongXun {
    public static void main(String args[]){
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("..r  un:this="+this.getClass().getName());
            }
        }).start();

    }
}
