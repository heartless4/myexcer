package com.dn.my.excer.zk;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName : OrderCodeGenerator
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/4 19:02
 * @Version 1.0
 **/
public class OrderCodeGenerator {
    // 自增长序列
    private static int i=1;
    public String getOrderCode(){
        // 按照“年-月-日-小时-分钟-秒-自增长序列”的规则生成订单编号
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        Date now=new Date();
        return sdf.format(now)+i++;
    }
    public static void main(String args[]){
        OrderCodeGenerator ocg=new OrderCodeGenerator();
        for(int i=0;i<10;i++){
            ocg.getOrderCode();
        }
    }
}
