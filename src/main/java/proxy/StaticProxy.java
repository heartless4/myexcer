package proxy;

/**
 * @ClassName : StaticProxy
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/30 15:50
 * @Version 1.0
 **/
public class StaticProxy implements UserService {
    private UserService userService;
    public StaticProxy(UserService userService){
        this.userService=userService;
    }
    @Override
    public int addUser(User user) {
        return userService.addUser(user);
    }

    @Override
    public String getUserName(int id) {
        return userService.getUserName(id);
    }
}
