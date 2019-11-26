package com.dn.my.excer.zk.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @ClassName : ZKDistributionQueue
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/4 23:50
 * @Version 1.0
 **/

/**
 *  ZKDistributeQueue 分布式队列实现
 *  * 使用zk指定的目录作为队列，子节点作为任务。
 *  * 生产者能往队列中添加任务，消费者能往队列中消费任务。
 *  * 持久节点作为队列，持久顺序节点作为任务。
 */
public class ZKDistributionQueue extends AbstractQueue<String> implements BlockingQueue<String> ,Serializable {
    private static final long serialVersionUID=1l;
    /**
     * zookeeper客户端操作实例
     */
    private ZkClient zkClient;
    /**
     *  定义在zk上的znode，作为分布式队列的根目录。
     */
    private String queueRootNode;
    private static final String default_queueRootNode="/distributeQueue";
    /**队列写锁节点*/
    private String queueWriteLockNode;
    /**队列读锁节点*/
    private String queueReadLockNode;
    /**
     * 子目录存放队列下的元素，用顺序节点作为子节点。
     */
    private String queueElementNode;
    /**
     * ZK服务的连接字符串，hostname:port形式的字符串
     */
    private String zkConnUrl;
    private static final String default_zkConnUrl="localhost:2181";
    /**
     * 队列容量大小，默认Integer.MAX_VALUE，无界队列。
     **/
    private int capacity;
    private static final int default_capacity=Integer.MAX_VALUE;
    /**
     * 控制进程访问的分布式锁
     */
    final Lock distributionWriteLock;
    final Lock distributionReadLock;

    public ZKDistributionQueue(){
        this(default_zkConnUrl,default_queueRootNode,default_capacity);
    }
    public ZKDistributionQueue(String zkServerUrl,String rootNodeName,int capacity){
        if (zkServerUrl == null) throw new IllegalArgumentException("zkServerUrl");
        if (rootNodeName == null) throw new IllegalArgumentException("rootNodeName");
        if (capacity <= 0) throw new IllegalArgumentException("initCapacity");
        this.zkConnUrl=zkServerUrl;
        this.queueRootNode=rootNodeName;
        this.capacity=capacity;
        initial();
        distributionWriteLock=new ZKDistributionImprovedLock(queueWriteLockNode);
        distributionReadLock=new ZKDistributionImprovedLock(queueReadLockNode);

    }
    /**
     * 初始化队列信息
     */
    private void initial(){
        queueWriteLockNode=queueRootNode+"/writeLock";
        queueReadLockNode=queueRootNode+"/readLock";
        queueElementNode=queueRootNode+"/element";
        zkClient=new ZkClient(zkConnUrl);
        zkClient.setZkSerializer(new MyZKSerializer());
        if(!this.zkClient.exists(queueElementNode)){
            this.zkClient.createPersistent(queueElementNode,true);
        }
    }






    /**
     * 容量足够立即将指定的元素插入到队列中，成功时返回true，容量不够，则返回false。
     *
     */
    @Override
    public boolean offer(String s) {
        checkElement(s);
        distributionWriteLock.lock();
        try {
            if(size()<capacity){
                enqueue(s);
                return true;
            }
        } finally {
            distributionWriteLock.unlock();
        }
        return false;
    }
// ==============================特殊值操作============================
    /**
     * 获取并删除头部元素，如果队列为空则返回null
     */
    @Override
    public String poll() {
        String firstChild=peek();
        try {
            distributionReadLock.lock();
            if(firstChild==null){
                return null;
            }
            boolean result=dequeue(firstChild);
            if(!result){
                return null;
            }
        } finally {
            distributionReadLock.unlock();
        }
        return firstChild;
    }
    // ==============================抛出异常操作============================
    /**
     * 获取并删除队列头部元素，当队列为空时抛出异常
     */
    @Override
    public String remove(){
        if(size()<0){
            throw new IllegalDistributionLockStatException(IllegalDistributionLockStatException.State.EMPTY);
        }
        distributionReadLock.lock();
        String firstChild;
        try {
            firstChild=poll();
            if(firstChild==null){
                throw new IllegalDistributionLockStatException("移除失败");
            }
        } finally {
            distributionReadLock.unlock();
        }
        return firstChild;
    }
    /**
     * 容量足够的情况下，将指定的元素加入队列，插入成功返回true，
     * 容量不足够的情况下，抛出IllegalDistributeQueueStateException异常。
     */
    @Override
    public boolean add(String e){
        checkElement(e);
        // 判断是否可以添加任务，不能则抛出异常
        if(size()>=capacity){
            throw new IllegalDistributionLockStatException(IllegalDistributionLockStatException.State.FULL);
        }
        distributionWriteLock.lock();
        try {
            return offer(e);
        }catch (Exception el){
            el.printStackTrace();
        }finally {
            distributionWriteLock.unlock();
        }
        return false;
    }
    // 阻塞操作
    @Override
    public void put(String s) throws InterruptedException {
        checkElement(s);
        distributionWriteLock.lock();
        try {
            if(size()>=capacity){// 容量不够，阻塞，监听元素出队
                waitForRemove();
                put(s);
            }else{// 容量足够
                enqueue(s);
                System.out.println(Thread.currentThread().getName()+"..往队列中放了元素");
            }
        } finally {
            distributionWriteLock.unlock();
        }
    }
    @Override
    public String take() throws InterruptedException {
        distributionReadLock.lock();
        try {
            List<String> children= zkClient.getChildren(queueElementNode);
            if(children!=null&&!children.isEmpty()){
                children=children.stream().sorted().collect(Collectors.toList());
                String takeChild=children.get(0);
                String childNode=queueElementNode+"/"+takeChild;
                String elementData=zkClient.readData(childNode);
                dequeue(childNode);
                System.out.println(Thread.currentThread().getName()+"...移除队列元素");
                return elementData;
            }else{
                waitForAdd();// 阻塞等待队列有元素加入
                return take();
            }
        } finally {
            distributionReadLock.unlock();
        }
    }


    /**
     * 暂不支持迭代子
     */
    @Override
    public Iterator<String> iterator() {
        throw new UnsupportedOperationException();
    }
    /**
     * 获取队列头部元素，但不执行删除，如果队列为空则返回null。
     *
     */
    @Override
    public String peek() {
        List<String> children=zkClient.getChildren(queueElementNode);
        if(children!=null&&!children.isEmpty()){
            children=children.stream().sorted().collect(Collectors.toList());
            String firstChild=children.get(0);
            String elementData=zkClient.readData(queueElementNode+"/"+firstChild);
            return elementData;
        }
        return null;
    }
    /**
     * 往zk中添加元素
     * @param e
     */
    private void enqueue(String e){
        zkClient.createPersistentSequential(queueElementNode+"/",e);
    }
    /**
     * 从zk中删除元素
     * @param e
     * @return
     */
    private boolean dequeue(String e){
        return zkClient.delete(e);
    }
    /**
     * 返回队列中已存在的元素数量
     */
    @Override
    public int size() {
        return zkClient.countChildren(queueElementNode);
    }



    // 暂不支持
    @Override
    public boolean offer(String s, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }



    @Override
    public String poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }
    /**
     * 返回剩余容量，没有限制返回Integer.MAX_value。
     */
    @Override
    public int remainingCapacity() {
        return capacity-size();
    }

    @Override
    public int drainTo(Collection<? super String> c) {
        return drainTo(c,size());
    }

    /**
     * 从队列移除指定大小的元素，并将移除的元素添加到指定的集合中。
     */
    @Override
    public int drainTo(Collection<? super String> c, int maxElements) {
        if(c==null){
            throw new NullPointerException();
        }
        int transferSize=0;
        List<String> children= zkClient.getChildren(queueElementNode);
        if(children!=null&&!children.isEmpty()){
            List<String> removeElements =children.stream().sorted().limit(maxElements).collect(Collectors.toList());
            transferSize=removeElements.size();
            c.addAll(removeElements);
            removeElements.forEach((e)->{
                zkClient.delete(e);
            });
        }
        return transferSize;
    }
    private static void checkElement(String e){
        if(e==null) throw new NullPointerException();
        if("".equals(e.trim())){
            throw new IllegalArgumentException("不能使用空格");
        }
        if(e.startsWith(" ")||e.endsWith(" ")){
            throw new IllegalArgumentException("前后不能包含空格");
        }
    }
    /**
     * 队列容量满了，不能再插入元素，阻塞等待队列移除元素。
     */
    private void waitForRemove(){
        CountDownLatch cdl=new CountDownLatch(1);
        // 注册watcher
        IZkChildListener iZkChildListener=new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if(currentChilds.size()<capacity){
                    cdl.countDown();
                    System.out.println(Thread.currentThread().getName()+"---监听到队列有元素移除，唤醒阻塞生产者线程");
                }
            }
        };
        zkClient.subscribeChildChanges(queueElementNode,iZkChildListener);
        try {
            if(size()>=capacity){// 有任务移除，激活等待的添加操作
                System.out.println(Thread.currentThread().getName()+"----队列已满，阻塞等待队列元素释放");
                cdl.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zkClient.unsubscribeChildChanges(queueElementNode,iZkChildListener);
    }
    /**
     * 队列空了，没有元素可以移除，阻塞等待元素添加到队列中。
     */
    private void waitForAdd(){
        CountDownLatch cdl=new CountDownLatch(1);
        // 注册watcher
        IZkChildListener iZkChildListener=new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if(currentChilds.size()>0){// 有任务了，激活等待的移除操作
                    cdl.countDown();
                    System.out.println(Thread.currentThread().getName()+"...监听到队列有元素加入，唤醒阻塞消费者线程");
                }
            }
        };
        zkClient.subscribeChildChanges(queueElementNode,iZkChildListener);
        try {
            // 确保队列是空的
            if(size()<=0){
                System.out.println(Thread.currentThread().getName()+"...队列已空，等待元素加入队列");
                cdl.await();
                System.out.println(Thread.currentThread().getName()+"...队列已有元素，线程被唤醒");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zkClient.unsubscribeChildChanges(queueElementNode,iZkChildListener);

    }
}
