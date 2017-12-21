package chapter3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 13 on 2017/5/5.
 */
public class ReenterLockCondition implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {

        try {
            lock.lock();
            condition.await();
            System.out.println("Thread is going on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
    
    // 使用案例 见ArrayBlockingQueue类
    public static void main(String args[]) throws InterruptedException {
        ReenterLockCondition reenterLockCondition = new ReenterLockCondition();
        Thread thread1 = new Thread(reenterLockCondition);
        thread1.start();
//        Thread thread2 = new Thread(reenterLockCondition);
//        thread2.start();
        Thread.sleep(2000);
        // 通知线程t1 继续执行
        lock.lock();
        condition.signal(); // 在signal()方法调用后，系统会从当前Condition 对象的等待队列中，唤醒一个线程(如果Condition等待队列有多个线程，需要signalAll)
        lock.unlock(); // 必须释放锁，让Condition等待队列中的线程去争夺资源
    }
}