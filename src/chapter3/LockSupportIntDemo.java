package chapter3;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by 13 on 2017/5/5.
 */
public class LockSupportIntDemo {
    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name) {
            super.setName(name);
        }

        public void run() {
            synchronized (u) {
                System.out.println("in " + getName());
                LockSupport.park();
                if (Thread.interrupted()) {
                	System.out.println(getName() + "被中断");
                }
            }
/*            try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/            
            System.out.println(getName() + "继续执行");
        }
    }


    public static void main(String args[]) throws InterruptedException {
        t1.start();
        Thread.sleep(100);
        t2.start();
        t1.interrupt();
        /**
         * 如果给定线程的许可尚不可用，则使其可用。
         * 如果线程在 park 上受阻塞，则它将解除其阻塞状态。
         * 否则，保证下一次调用 park 不会受阻塞。
         * 如果给定线程尚未启动，则无法保证此操作有任何效果。 
         * @param thread: 要执行 unpark 操作的线程；该参数为 null 表示此操作没有任何效果。
         */
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
    }
}