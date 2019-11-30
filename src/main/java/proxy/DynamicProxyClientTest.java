package proxy;

/**
 * @ClassName : DynamicProxyClientTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/30 16:27
 * @Version 1.0
 **/
public class DynamicProxyClientTest {
    public static void main(String args[]){
        DynamicProxy dynamicProxy=new DynamicProxy();
        UserService userService=(UserService)dynamicProxy.newDynamicProxyInstance(new UserServiceImpl());
        System.out.println(userService.getUserName(1));
    }
}
