package com.dn.my.excer.zk.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * @ClassName : ZKDistributionImprovedLock
 * @Description :
 * @Author :hjh
 * @Date:2019/9/28 21:26
 * @Version 1.0
 **/
public class ZKDistributionImprovedLock implements Lock {
    private String lockPath;
    private ZkClient zkClient;
    private ThreadLocal<String> currentPath=new ThreadLocal<String>();
    private ThreadLocal<String> beforePath=new ThreadLocal<String>();
    private ThreadLocal<Integer> reenterCount=ThreadLocal.withInitial(()->0);
    /*private ThreadLocal<Integer> reenterCount=ThreadLocal.withInitial(new Supplier<Integer>() {
    public Integer get() {
        return 0;
    }
});*/

    /*
     * 利用临时顺序节点来实现分布式锁
     * 获取锁：取排队号（创建自己的临时顺序节点），然后判断自己是否是最小号，如是，则获得锁；不是，则注册前一节点的watcher,阻塞等待
     * 释放锁：删除自己创建的临时顺序节点
     */
    public ZKDistributionImprovedLock(String lockPath){
        if(lockPath==null||lockPath.trim().equals("")){
            throw new IllegalArgumentException("lockPath不能为空字符串");
        }
        this.lockPath=lockPath;
        zkClient=new ZkClient("localhost:2181");
        zkClient.setZkSerializer(new MyZKSerializer());
        if(!zkClient.exists(lockPath)){
            try {
                zkClient.createPersistent(lockPath,true);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
        }

    }
    public void lock() {
        if(!tryLock()){
            // 阻塞等待
            waitForLock();
            // 再次尝试加锁
            lock();
        }

    }
    private void waitForLock(){
        final CountDownLatch cdl=new CountDownLatch(1);
        // 注册watcher
        IZkDataListener dataListener=new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {

            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(Thread.currentThread().getId()+"监听到节点被删除，分布式锁被释放");
                cdl.countDown();
            }
        };
        zkClient.subscribeDataChanges(beforePath.get(),dataListener);
        // 怎么让自己阻塞
        if(zkClient.exists(beforePath.get())){
            try {
                System.out.println(Thread.currentThread().getId()+" 分布式锁没有抢到，进入阻塞状态");
                cdl.await();
                System.out.println(Thread.currentThread().getId()+ " 释放分布式锁，被唤醒。");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        // 醒来后，取消watcher
        zkClient.unsubscribeDataChanges(beforePath.get(),dataListener);
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        System.out.println(Thread.currentThread().getId()+"正在尝试获取锁");
        if(currentPath.get()==null|| !zkClient.exists(currentPath.get())){
            String currentNode=zkClient.createEphemeralSequential(lockPath+"/","locked");
            currentPath.set(currentNode);
            reenterCount.set(0);
        }


        // 获得所有的子
        List<String> children= zkClient.getChildren(lockPath);
        // 排序list
        Collections.sort(children);
        String smallestNode=children.get(0);
        // 判断当前节点是否是最小的
        if(currentPath.get().equals(lockPath+"/"+smallestNode)){
            System.out.print(Thread.currentThread().getId()+"得到了锁");
            reenterCount.set(reenterCount.get()+1);
            return true;
        }else{
            // 取到前一个
            // 得到字节的索引号
            int curIndex=children.indexOf(currentPath.get().substring(lockPath.length()+1));

            beforePath.set(lockPath+"/"+children.get(curIndex-1));
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        System.out.println(Thread.currentThread().getId()+"释放分布式锁");
        if(reenterCount.get()>1){
            // 重入次数减1，释放锁
            reenterCount.set(reenterCount.get()-1);
            return;
        }
        // 删除节点
        if(currentPath.get()!=null){
            zkClient.delete(currentPath.get());
            currentPath.set(null);
            reenterCount.set(0);
        }
    }

    public Condition newCondition() {
        return null;
    }
}
