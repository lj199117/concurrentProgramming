package chapter3;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by 13 on 2017/5/5.
 */
public class LockSupportDemo {
    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name) {
            super.setName(name);
        }

        public void run() {
            System.out.println("睡觉");
            /**
             * 为了线程调度，在许可可用之前阻塞当前线程。 
             * 如果许可可用，则使用该许可，并且该调用立即返回；// 也就是先执行了unpark()
             * 否则，为线程调度禁用当前线程，并在发生以下三种情况之一以前，使其处于休眠状态：
             *  1. 其他某个线程将当前线程作为目标调用 unpark
             *  2. 其他某个线程中断当前线程
             *  3. 该调用不合逻辑地（即毫无理由地）返回
             */
            LockSupport.park();
            System.out.println("起床");
        }
    }

    /**
     * 先park再unpark
     */
    public static void main(String args[]) throws InterruptedException {
        t1.start();
        t2.start();
        Thread.sleep(5000);
        /**
         * 如果给定线程的许可尚不可用，则使其可用。
         * 如果线程在 park 上受阻塞，则它将解除其阻塞状态。
         * 否则，保证下一次调用 park 不会受阻塞。
         * 如果给定线程尚未启动，则无法保证此操作有任何效果。 
         * @param thread: 要执行 unpark 操作的线程；该参数为 null 表示此操作没有任何效果。
         */
        System.out.println("妈妈喊我起床");
        LockSupport.unpark(t1);
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
    }
}
