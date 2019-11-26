package com.dn.my.excer.zk.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @ClassName : ZkDistributionLock
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/9/7 19:57
 * @Version 1.0
 **/
public class ZkDistributionLock implements Lock {
    private String lockPath;
    private ZkClient client;

    public ZkDistributionLock(String lockPath){
        if(lockPath ==null || lockPath.trim().equals("")) {
            throw new IllegalArgumentException("patch不能为空字符串");
        }
        this.lockPath=lockPath;
        client=new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZKSerializer());
    }

    public void lock() {
        if(!tryLock()){
            waitForLock(); //么有获取到锁的话，则阻塞自己
        }
        lock();
    }
    private void waitForLock(){
        final CountDownLatch countDownLatch=new CountDownLatch(1);
        IZkDataListener listener=new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {

            }

            public void handleDataDeleted(String s) throws Exception {
                System.out.println("...节点被删除了。释放锁了。");
                countDownLatch.countDown();
            }
        };
        client.subscribeDataChanges(lockPath,listener);

        if(client.exists(lockPath)){
            try {
                countDownLatch.await(); //阻塞自己
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(lockPath,listener);

    }

    public boolean tryLock() { //不会阻塞

        try {
            client.createEphemeral(lockPath);
        } catch (ZkNodeExistsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void lockInterruptibly() throws InterruptedException {
    }
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
    }

    public Condition newCondition() {
        return null;
    }
}
