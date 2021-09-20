package chapter3;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ������
 * Created by 13 on 2017/5/5.
 */
public class ReenterLock implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    public static void lock() {
        lock.lock();
    }
    
    public static void unlock() {
        lock.unlock();
    }
    
    @Override
    public void run() {
        for (int j = 0; j < 1000000; j++) {
            lock(); // 锁thread1或thread2, 重入是在同一个线程多次加锁才是重入
            try {
                i++;
            } finally {
                unlock();
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        ReenterLock reenterLock = new ReenterLock();
        Thread thread1 = new Thread(reenterLock);
        Thread thread2 = new Thread(reenterLock);
        
        thread1.start();
        thread2.start();
        Thread.sleep(10); // 先让2个线程跑起来计算一部分数据
        
        lock(); // 锁住主线程
        System.out.println(i); // 输出结果
        Thread.sleep(500); // 阻塞，这个时候主线程没释放锁
        System.out.println(i); // 2次结果一致，说明：锁现在是在主线程手里，它不释放另外两个线程都没法继续
        unlock();
        
        thread1.join();
        thread2.join();
        
        System.out.println(i);
    }

}
