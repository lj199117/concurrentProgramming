package chapter3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 13 on 2017/5/5.
 */
public class ReenterLockCondition {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public static void main(String args[]) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                lock.lock();
                condition.await(); // condition必须先拿到锁才能await
                condition.await(); // condition必须先拿到锁才能await
                System.out.println(Thread.currentThread().getName() + " Thread is going on");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        thread1.setName("t1");
        Thread thread2 = new Thread(() -> {
            try {
                lock.lock();
                condition.signal(); // 会立即返回
                System.out.println(Thread.currentThread().getName() + " Thread is going on");
            } finally {
                lock.unlock();
            }
        });
        thread2.setName("t2");
        
        thread1.start();
        Thread.sleep(2000);
        thread2.start();
        
        thread1.join();
        thread2.join();
        
    }
}