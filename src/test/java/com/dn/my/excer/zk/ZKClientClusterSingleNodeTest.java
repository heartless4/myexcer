package com.dn.my.excer.zk;

import com.dn.my.excer.zk.zkclient.MyZKSerializer;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Random;

/**
 * @ClassName : ZKClientClusterSingleNodeTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/19 10:54
 * @Version 1.0
 **/
public class ZKClientClusterSingleNodeTest {
    public static void main(String args[]){
        ZkClient zkClient=new ZkClient("localhost:2182",3000);
        zkClient.setZkSerializer(new MyZKSerializer());
        try {
            zkClient.createPersistent("/zk/a",true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        Random random=new Random();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    int idx=random.nextInt(100);
                    zkClient.writeData("/zk/a",String.valueOf(idx));
                    System.out.println(System.nanoTime()+" 修改/zk/a节点的数据为："+idx);
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String data=zkClient.readData("/zk/a");
                    System.out.println(System.nanoTime()+" 读取到了/zk/a节点内容："+data);
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        zkClient.subscribeDataChanges("/zk/a", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object data) throws Exception {
                System.out.println(System.nanoTime()+" ----收到节点数据变化：" + data + "-------------");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println(System.nanoTime()+" ----收到节点被删除了-------------");
            }
        });

        try {
            Thread.sleep(1000 * 60 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
