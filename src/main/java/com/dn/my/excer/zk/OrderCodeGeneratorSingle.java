package com.dn.my.excer.zk;

/**
 * @ClassName : OrderCodeGeneratorSingle
 * @Description :订单编号生成类-单例模式，通过静态内部类的方式实现
 * @Author :hjh
 * @Date:2019/10/4 19:08
 * @Version 1.0
 **/
public class OrderCodeGeneratorSingle {
    static class InstanceHolder{
        private static OrderCodeGenerator instance=new OrderCodeGenerator();
    }
    public static OrderCodeGenerator getInstance(){
        return InstanceHolder.instance;
    }

}
