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

        System.out.println(s1.intern()==s1);//true
        String s2=new StringBuilder("ja").append("va").toString();
        System.out.println(s2.intern()==s2);//jdk1.8.0_172:false.  jdk1.8.0_232-b09:true
        String str1=new String("abc");
        String str2=new String("abc");
        System.out.println(str1.intern()==str1);//false
        System.out.println(str2.intern()==str2);//false
        System.out.println(str1.intern()==str2.intern());//true
        String string2=new String("string")+new String("01");
        string2.intern();
        String string1="string01";
        System.out.println(string2==string1);//true

        String ss1="123";
        String ss2="123";
        System.out.println(ss1==ss2);//true



    }
}
