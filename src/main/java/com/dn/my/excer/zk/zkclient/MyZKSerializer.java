package com.dn.my.excer.zk.zkclient;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName : MyZKSerializer
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/9/7 16:48
 * @Version 1.0
 **/
public class MyZKSerializer implements ZkSerializer {
    public byte[] serialize(Object o) throws ZkMarshallingError {
        String obj=(String)o;
        try {
            return obj.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            String obj=new String(bytes,"utf-8");
            return obj;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
