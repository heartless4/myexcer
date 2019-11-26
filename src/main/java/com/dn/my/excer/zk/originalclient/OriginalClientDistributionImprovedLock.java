package com.dn.my.excer.zk.originalclient;

import org.I0Itec.zkclient.exception.ZkException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @ClassName : OriginalClientDistributionImprovedLock
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/4 15:28
 * @Version 1.0
 **/
public class OriginalClientDistributionImprovedLock implements Lock {
    private ZooKeeper zkClient;
    private String lockPath;
    private ThreadLocal<String> currentPath=new ThreadLocal<String>();
    private ThreadLocal<String> previousPath=new ThreadLocal<String>();
    private static final String CONNECTION_STRING="127.0.0.1:2181";
//    private static final int SESSION_TIMEOUT=3000;
    private static final int SESSION_TIMEOUT=30000000;
    public OriginalClientDistributionImprovedLock(String path){
        System.out.println("......lockPath="+path);
        if(path==null){
            throw new NullPointerException("参数不能为null");
        }

        try {
            ZooKeeper zkClient=new ZooKeeper(CONNECTION_STRING,SESSION_TIMEOUT,null);
            this.zkClient=zkClient;
            this.lockPath=path;
            if(zkClient.exists(lockPath,false)==null){
                zkClient.create(lockPath,"locked".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch(KeeperException e){
            e.printStackTrace();
        }
    }
    public void lock() {
        if(!tryLock()){
            waitForLock();
            lock();
        }
//        lock(); //这样写就成死循环啦

    }
    private void waitForLock(){
        // 未获得锁的线程，通过栅栏进行控制 阻塞—>唤醒动作
        CountDownLatch countDownLatch=new CountDownLatch(1);
        Watcher watcher=new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)){
                    // 监听到节点被删除，唤醒阻塞中的线程。
                    countDownLatch.countDown();
                    System.out.println("当前节点被删除，去唤醒阻塞的线程"+Thread.currentThread().getName());

                }
            }
        };
        try {
            Stat existStat=zkClient.exists(previousPath.get(),watcher);
            if(existStat!=null){
                countDownLatch.await();// 为获得锁，进入阻塞
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        System.out.println("tryLock...lockpath="+lockPath);
        String rootDir=lockPath+"/";
        try {
            if(currentPath.get()==null||zkClient.exists(currentPath.get(),null)==null){
                // 创建临时顺序节点
                String newPath=zkClient.create(rootDir,"locked".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                currentPath.set(newPath);
            }
            // 获取节点下的子节点
            // 排序子节点，如果自己是最小的节点，则表示获取到了锁
            List<String> sortedChildren = zkClient.getChildren(lockPath,null).stream().sorted().collect(Collectors.<String>toList());
            String curPath=currentPath.get().substring(rootDir.length());
            if(sortedChildren.get(0).equals(curPath)){
                System.out.println("当前节点是最小的节点...");
                return true;
            }else{// 否则获取前一个节点信息，记录它，等待它的删除动作（释放锁）信号
                System.out.println("当前节点不是最小的节点");
                int currentIdx=sortedChildren.indexOf(curPath);
                if(currentIdx>0){
                    String prevPath=sortedChildren.get(currentIdx-1);
                    previousPath.set(rootDir+prevPath);
                }

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        System.out.println("释放锁。。。。unlock:"+currentPath.get());
        if(currentPath.get()!=null){
            try {
                zkClient.delete(currentPath.get(),-1);
                currentPath.set(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    public Condition newCondition() {
        return null;
    }
}
