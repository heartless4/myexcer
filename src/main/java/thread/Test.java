package thread;

/**
 * @ClassName : Test
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/19 18:45
 * @Version 1.0
 **/
public class Test {
    public Test(){
        System.out.println("this.getName="+this.getClass().getName());//普通的类对象，是没有getName的方法的。Thread有
    }
    public static void main(String args[]){
        CountOperate countOperate=new CountOperate();//CountOperate实例的默认名是Thread-0
        Thread thread=new Thread(countOperate);
        // thread对象的默认线程名是Thread-1
        thread.setName("a");
        thread.start();
    }
}
