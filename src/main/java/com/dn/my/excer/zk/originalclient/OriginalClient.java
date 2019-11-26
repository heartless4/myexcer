package com.dn.my.excer.zk.originalclient;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @ClassName : OriginalClient
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/4 13:00
 * @Version 1.0
 **/
public class OriginalClient {
    private static final String CONNECT_STRING="127.0.0.1:2181";
    private static final int SESSION_TIMEOUT=3000;
    private static final String DEMO_PATH="/dongnao";

    // 定义一个监控所有节点变化的Watcher
    Watcher watcher=new Watcher() {
        public void process(WatchedEvent watchedEvent) {
//            watchedEvent.getType().getIntValue()==Event.EventType.NodeDeleted.getIntValue()
            System.out.println("接收到了watchEvent changed path"+watchedEvent.getPath()+",changed type:"+watchedEvent.getType().name());
        }
    };
    public void runDemo() throws Exception{
        // 初始化一个与ZK连接。三个参数：
        // 1、要连接的服务器地址，"IP:port"格式；
        // 2、会话超时时间
        // 3、节点变化监视器
        ZooKeeper zooKeeper=new ZooKeeper(CONNECT_STRING,SESSION_TIMEOUT,watcher);

        // 新建节点。四个参数：1、节点路径；2、节点数据；3、节点权限；4、创建模式
        //String newPath = zk.create(DEMO_PATH+"/data", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //System.out.println("create new node '"+newPath+"'");

        // 判断某路径是否存在。两个参数：1、节点路径；2、是否监控（Watcher即初始化ZooKeeper时传入的Watcher）
        Stat beforeStat=zooKeeper.exists(DEMO_PATH,true);
        System.out.println("stat of "+DEMO_PATH+" before change:"+beforeStat);
        // 修改节点数据。三个参数：1、节点路径；2、新数据；3、版本，如果为-1，则匹配任何版本
        Stat afterStat=zooKeeper.setData(DEMO_PATH,"456".getBytes(),-1);
        System.out.println("stat of "+DEMO_PATH+" after change:"+afterStat);

        // 获取所有子节点。两个参数：1、节点路径；2、是否监控该节点
        List<String> children=zooKeeper.getChildren("/",true);
        System.out.println("children of path '/' :" +children.toString());
        // 获取节点数据。三个参数：1、节点路径；2、书否监控该节点；3、版本等信息可以通过一个Stat对象来指定
        byte[] nameByte= zooKeeper.getData(DEMO_PATH,true,null);
        String name=new String(nameByte,"utf-8");
        System.out.println("get data from "+DEMO_PATH+":"+name);
        // 删除节点。两个参数：1、节点路径；2、 版本，-1可以匹配任何版本，会删除所有数据
        zooKeeper.delete(DEMO_PATH,-1);
        System.out.println("delete "+DEMO_PATH);
        zooKeeper.close();


    }

    public static void main(String args[]){
        OriginalClient originalClient=new OriginalClient();
        try {
            originalClient.runDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
