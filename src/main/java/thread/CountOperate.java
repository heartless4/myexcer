package thread;

/**
 * @ClassName : CountOperate
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/19 18:42
 * @Version 1.0
 **/
public class CountOperate extends Thread {
    public CountOperate(){//默认先调用了super()方法，即Thread()无参构造方法
        System.out.println("CountOperate---begin");
        System.out.println("Thread.currentThread().getName()="+Thread.currentThread().getName());//可能是main，也可能是新建的线程
        System.out.println("this.getName()="+this.getName()); //输出始终是Thread-0，这里的this，是Thread类的一个实例，不是当前线程
        System.out.println("CountOperate---end");
    }

    @Override
    public void run() {
        System.out.println("run---begin");
        System.out.println("Thread.currentThread().getName()="+Thread.currentThread().getName());
        System.out.println("this.getName()="+this.getName());//输出始终是Thread-0，这里的this，是Thread类的一个实例，不是当前线程
        System.out.println("run---end");
    }
}
