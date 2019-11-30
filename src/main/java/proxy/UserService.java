package proxy;

/**
 * @ClassName : UserService
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/30 15:42
 * @Version 1.0
 **/
public interface UserService {
     int addUser(User user);
     String getUserName(int id);
}
