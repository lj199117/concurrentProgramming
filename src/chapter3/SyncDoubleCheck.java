package chapter3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author edmond
 * @date 2020/04/28
 */
public class SyncDoubleCheck implements Runnable {
    // 实例化一个倒计树器,初始倒计数为10
    static final CountDownLatch latch = new CountDownLatch(10);
    static final SyncDoubleCheck demo = new SyncDoubleCheck();

    @Override
    public void run() {
        try {
            // 实例对象生成
            Singleton.getInstance();
            // 输出当前线程的名称
            System.out.println(Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 计数器进行减一
            latch.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建一个长度为10的定长线程池
        ExecutorService exec = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            // 启动线程
            exec.submit(demo);
        }

        // 等待检查,阻塞main主线程,只有当CountDownLatch倒计数器为0时才会唤醒阻塞的main主线程
        latch.await();

        // 开启的10个线程中几个线程获取到了锁
        System.out.println("共有 ( " + Singleton.i + " ) 个线程获取到对象锁");
        // 最终生成了几个Singleton实例
        System.out.println("最终生成了( " + Singleton.j + " )个Singleton实例对象");

        // 关闭线程池
        exec.shutdown();
    }
}

class Singleton {

    // 使用volatile禁止指令重排序
    private static volatile Singleton sin = null;

    public static int i = 0;// 标识有几个线程获取到了锁
    public static int j = 0;// 标识系统中到底生成了几个实例

    // 将构造器的修饰符设置为"private"，可以防止在外部进行new实例对象
    private Singleton() {};

    // 获取实例对象的方法，公共的方法。
    public static Singleton getInstance() {
        // 第一次判空。
        if (sin == null) {
            // 加锁
            synchronized (Singleton.class) {
                i++;
                // 第二次判空。
                if (sin == null) {
                    sin = new Singleton();
                    j++;
                }
            }
        }
        return sin;
    }
}
