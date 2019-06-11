package chapter3.threadpool.test2;
import java.util.concurrent.*;

/**
 * 
 * @author goldl
 *
 */
public class ThreadPoolExecutorTest {
    private static volatile boolean stop = false;
    public static void main(String[] args) throws InterruptedException{
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(16);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, taskQueue);
        executor.execute(()->{
            while (true) {
                System.out.println("start processing");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
                System.out.println("finish processing");
                if (stop) {
                    throw new ThreadDeath();
//                    throw new RuntimeException("runtime exception");
                }
            }
        });
        Thread.sleep(3000);
        stop = true;
        Thread.sleep(2000);

        executor.execute(()->{
            //能够继续提交任务执行
            System.out.println("continue submit runnable task,is All thread in thread pool dead?");
        });
    }
}