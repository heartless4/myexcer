package proxy;

/**
 * @ClassName : StaticProxyClientTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/30 15:58
 * @Version 1.0
 **/
public class StaticProxyClientTest {
    public static void main(String args[]){
        UserService userService=new StaticProxy(new UserServiceImpl());
        System.out.println(userService.getUserName(1));
    }
}
