package com.dn.my.excer.zk.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @ClassName : ZkClientDemo
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/9/7 16:54
 * @Version 1.0
 **/
public class ZkClientDemo {

    public static void main(String args[]){
        ZkClient client=new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZKSerializer());
        client.create("/zookeeper/test2","23",CreateMode.PERSISTENT);

        //客户端订阅的这两个事件的线程，是同一个
        client.subscribeChildChanges("/zookeeper/test2",new IZkChildListener(){

            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("eChildChange:current thread:"+Thread.currentThread().getName()+","+parentPath+"发生变化"+currentChilds); //子节点的创建和删除，都会触发,数据修改不会触发
//eChildChange:current thread:ZkClient-EventThread-11-localhost:2181,/zookeeper/test2发生变化[test22]
            }
        });
        client.subscribeDataChanges("/zookeeper/test2",new IZkDataListener(){
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("DataChanges:current thread:"+Thread.currentThread().getName()+","+dataPath+"发生变化："+data);
            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath+"节点被删除");
            } //DataChanges:current thread:ZkClient-EventThread-11-localhost:2181,/zookeeper/test2发生变化：11
        });
        try {
            System.out.println("current thread:"+Thread.currentThread().getName());
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
