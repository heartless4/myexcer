package jichu;

/**
 * @ClassName : StringTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/26 8:15
 * @Version 1.0
 **/
public class StringTest {
    public static void main(String args[]){
        String s1=new StringBuilder("go").append("od").toString();
        System.out.println(s1.intern()==s1);
        String s2=new StringBuilder("ja").append("va").toString();
        System.out.println(s2.intern()==s2);
    }
}