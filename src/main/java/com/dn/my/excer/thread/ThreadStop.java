package com.dn.my.excer.thread;

/**
 * @ClassName : ThreadStop
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/6/8 17:16
 * @Version 1.0
 **/
public class ThreadStop {
    public static void main(String args[]) throws InterruptedException {
        MyThread thread=new MyThread();
        thread.start();
        Thread.sleep(1000);
        thread.stop();//破坏了原子性
//        thread.interrupt();
        System.out.println("is interupted:"+thread.isInterrupted()+",thread isalive:"+thread.isAlive());
        while(thread.isAlive()){

        }
        System.out.println("2 is interupted:"+thread.isInterrupted()+",thread isalive:"+thread.isAlive());
        thread.print();
        
    }
}

class MyThread extends Thread{
    private int i=0;
    private int j=0;
    @Override
    public void run() {
        synchronized (this){
            i++;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            j++;
        }
        System.out.println("锁释放");
    }
    public void print(){
        System.out.println("i="+i+",j="+j);
    }
}
