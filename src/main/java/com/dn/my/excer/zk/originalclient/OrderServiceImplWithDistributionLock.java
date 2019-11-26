package com.dn.my.excer.zk.originalclient;

import com.dn.my.excer.zk.OrderCodeGenerator;
import com.dn.my.excer.zk.OrderCodeGeneratorSingle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * @ClassName : OrderServiceImplWithDistributionLock
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/7 16:07
 * @Version 1.0
 **/
public class OrderServiceImplWithDistributionLock {
    private OrderCodeGenerator orderCodeGenerator=OrderCodeGeneratorSingle.getInstance();

    private Lock lock= new OriginalClientDistributionImprovedLock("/distributeLock");
    private static Set<String> codeSet=new HashSet<>();
    public void createOrder(){
        String orderCode=null;
        try {
            lock.lock();
            orderCode=orderCodeGenerator.getOrderCode();
            codeSet.add(orderCode);
        } finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName()+"....orderCode="+orderCode+",orderCodeset="+codeSet);
    }

}
