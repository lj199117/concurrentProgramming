package chapter3.threadpool.test2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        System.out.println("准备执行:" + t.getName());
    }
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        //若线程执行某任务失败了，重新提交该任务
        if (t != null) {
            Runnable task =  r;
            System.out.println("restart task...");
            execute(task);
        }
    }
    @Override
    protected void terminated() {
        System.out.println("线程池退出!");
    }
}