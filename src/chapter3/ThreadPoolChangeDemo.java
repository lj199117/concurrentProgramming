 package chapter3;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author edmond
 * @date 2020/06/11
 * @see https://mp.weixin.qq.com/s/9HLuPcoWmTqAeFKa1kj-_A
 */
public class ThreadPoolChangeDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 
            60, TimeUnit.SECONDS, 
            new ArrayBlockingQueue<>(10), 
            new ThreadFactoryBuilder().setNameFormat("work" + "-%d").setDaemon(true).build());
        for (int i = 0; i < 15; i++) {
            executor.execute(new Thread(() -> {
                threadpoolStatus(executor, "执行任务");
                try {
                    Thread.sleep(10000); // 10s
                } catch (InterruptedException e) {}
            }));
        }
        threadpoolStatus(executor, "改变之前");
        executor.setCorePoolSize(10);
        /**
         * 如果该值小于CorePoolSize，会出现核心线程数调整之后无效的情况
         * @see https://mp.weixin.qq.com/s/9HLuPcoWmTqAeFKa1kj-_A 动态设置的注意点有哪些？
         */
        executor.setMaximumPoolSize(20);
        /**
         * 线程池被创建后如果没有任务过来，里面是不会有线程的。如果需要预热的话可以调用
         */
        executor.prestartAllCoreThreads(); // 预热核心线程,启动所有的核心线程数,避免活跃线程数数量不变
        threadpoolStatus(executor, "改变之后");
        Thread.currentThread().join();
        
    }
    
    private static void threadpoolStatus(ThreadPoolExecutor executor, String name) {
        BlockingQueue<Runnable> queue = executor.getQueue();
        System.out.println(new Date() + "-" + Thread.currentThread().getName() + "-" + name + "-" +
            "核心线程数：" + executor.getCorePoolSize() + 
            "活动线程数：" + executor.getActiveCount() + 
            "最大线程数：" + executor.getMaximumPoolSize() + 
            "线程池活跃度：" + divide(executor.getActiveCount(), executor.getMaximumPoolSize()) + 
            "任务完成数：" + executor.getCompletedTaskCount() + 
            "队列大小：" + queue.size() +
            "队列剩余大小：" + queue.remainingCapacity() +
            "队列使用度：" + divide(queue.size(), queue.size() + queue.remainingCapacity()));
    }
    
    private static String divide(int n1, int n2) {
        return String.format("%1.2f%%", Double.parseDouble(n1 + "") / Double.parseDouble(n2 + "") * 100);
    }
}
