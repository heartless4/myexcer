package thread;

/**
 * @ClassName : TestJoin
 * @Description :TODO
 * @Author :hjh
 * @Date:2019/10/19 23:37
 * @Version 1.0
 **/
public class TestJoin {
    public static void main(String args[]){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("run..");
                for(int i=0;i<5000;i++){
                    System.out.println("i="+i);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run end");
//                Thread.yield();
            }
        });
        thread.start();


       try {
           System.out.println("Thread.currentThread().getname="+Thread.currentThread().getName());
//            Thread.currentThread().join(100); //当子线程中的操作太长，比如，需要查数据库时，会发生，
                                    //子线程还没执行完，主线程就结束的现象。
           //主线程启动子线程，如果子线程中要进行大量的耗时运算，主线程会早于子线程结束，这时候主线程如果想等待子线程完成之后再运行，就需要join()方法
           thread.join(); //等待thread线程100毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       //不加join的时候，输出结果：main end
        // run。。
        // run end

        //加上main.join后，main end 没有输出，run ，run end后就结束了
        // 用thread.join，run end 后，main end


        System.out.println("main end");
    }
}
