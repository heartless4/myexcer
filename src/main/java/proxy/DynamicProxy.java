package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName : DynamicProxy
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/30 16:06
 * @Version 1.0
 **/
public class DynamicProxy implements InvocationHandler {
    private Object targetObject;
    public Object newDynamicProxyInstance(Object targetObject){
        this.targetObject=targetObject;
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),targetObject.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //在方法执行前后加日志，与业务代码解耦
        Long startTime=System.currentTimeMillis();
        System.out.println("method start excute...");

        System.out.println(proxy instanceof UserService);//true
        System.out.println(proxy instanceof UserServiceImpl);//false
        Object ret=method.invoke(targetObject,args);

        Long endTime=System.currentTimeMillis();
        System.out.println("method end excute...,duration="+(endTime-startTime)+"毫秒");
        return ret;
//        return method.invoke(proxy,args);//循环内部调用，死锁
    }
}
