package jichu;

/**
 * @ClassName : AutoboxingTest
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/11/26 7:51
 * @Version 1.0
 **/
public class AutoboxingTest {
    public static void main(String args[]){
        Integer a=new Integer(3);
        Integer b=3; //把3自动装箱成Integer
        int c=3;
        System.out.println(a==b);//false 两个引用没有引用同一对象
        System.out.println(a==c);//true  a自动拆箱成int类型再和c比较

        //装箱：当给一个Integer对象赋一个int值时，会调用Integer对象的valueOf方法，转换成Integer对象
        //如果字面量在-128到127之间，不会new新的对象，而是直接引用常量池中的Integer对象
        Integer n1=100,n2=100,n3=128,n4=128;
        System.out.println(n1==n2); //true
        System.out.println(n3==n4);//false
    }
}
