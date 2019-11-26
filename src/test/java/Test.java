import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName : Test
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/7 13:43
 * @Version 1.0
 **/
public class Test {
    public static void main(String args[]) throws Exception{
        Set<String> set=new HashSet<>();
       /* set.add("a");
        set.add("a");
        System.out.println("set="+set);*/
       System.out.println(set.size()); //初始化后，默认是0
        set.add("a");
        System.out.println(set.size()); //1

        List list=new ArrayList();
//        System.out.println(list.size());
        System.out.println("容量："+getSize(list,"elementData")); //容量是0
        list.add("a");
        list.add(2);
//        System.out.println(list.size());
        System.out.println("容量："+getSize(list,"elementData")); //容量是10
        //结论： ArrayList动态扩容：如果通过无参构造的话，初始数组容量为0，当真正对数组进行添加时，才真正分配容量。
        // 每次按照1.5倍（位运算）的比率通过copeOf的方式扩容。 在JKD1.6中实现是，如果通过无参构造的话，初始数组容量为10，每次通过copeOf的方式扩容后容量为原来的1.5倍，以上就是动态扩容的原理。

    }
    static int getSize(Object obj,String fieldName) throws Exception{
        Field field= obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object[] objects=(Object[])field.get(obj);
        return objects.length;
    }
}
