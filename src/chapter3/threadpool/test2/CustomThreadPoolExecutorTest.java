package chapter3.threadpool.test2;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池异常处理
 * 参考：https://www.cnblogs.com/hapjin/p/10240863.html
 * @author goldl
 *
 */
public class CustomThreadPoolExecutorTest {
	private static final AtomicInteger threadNumber=new AtomicInteger(1);
    private static volatile boolean stop = false;
    
    public static void main(String[] args)throws InterruptedException {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(16);
        //定义 线程执行过程中出现异常时的 异常处理器
        MyExceptionHandler exceptionHandler = new MyExceptionHandler();
        ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				String name = "mycustom-thread-" + threadNumber.getAndIncrement();
		        Thread ret = new Thread(null, r, name);
		        ret.setUncaughtExceptionHandler(exceptionHandler);
				return ret;
			}
		};
        CustomThreadPoolExecutor threadPoolExecutor = new CustomThreadPoolExecutor(1, 2, 1, TimeUnit.HOURS, taskQueue,threadFactory);

        threadPoolExecutor.execute(()->{
            //提交的是一个while(true)任务,正常运行时这类任务不会结束
            while (true) {
                System.out.println("start processing");
                try {
                    //模拟任务每次执行耗时1000ms
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
                System.out.println("finish processing");

                if (stop) {
                    throw new RuntimeException("running encounter exception");
                }
            }
        });

        Thread.sleep(2000);
        //模拟 test- 线程 在执行任务过程中抛出异常
        stop = true;
        Thread.sleep(1000);
        stop = false;
    }

    private static class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(String.format("thread name=%s, msg=%s", t.getName(), e.getMessage()));
        }
    }
}