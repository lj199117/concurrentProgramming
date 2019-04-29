package chapter3;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
* 可以用来解决这么一个问题：
*
* jdk的例子 每个 Task 线程处理矩阵的一行，在处理完所有的行之前，该线程将一直在屏障处等待。处理完所有的行之后，将执行所提供的 Runnable
* 屏障操作(即在run中处理)，并合并这些行。
*
* @author LJ
*
*/
public class CyclicBarrierDemo3 {
      public static void main(String[] args) {
            ExecutorService service = Executors.newCachedThreadPool();
            // final CyclicBarrier cb = new CyclicBarrier(3);//创建CyclicBarrier对象并设置3个公共屏障点
            final CyclicBarrier cb = new CyclicBarrier(3, new Runnable()  {
                  public void run() {
                        System.out.println("********我们都到了集合点,可以重新开始了***********");
                  }
            });
            for (int i = 0; i < 3; i++) { // 如果线程数多余屏障数，则达到屏障数即可往下执行
                  Runnable runnable = new Task(cb);// 持有同一个屏障才行
                  service.execute(runnable);
            }
            service.shutdown();
      }
}
class Task implements Runnable {
      CyclicBarrier cb;
      public Task(CyclicBarrier cb) {
            this.cb = cb;
      }
      public void run() {
            try {
                  Thread.sleep((long) (Math.random() * 10000));
                  System.out.println(
                              "线程" + Thread.currentThread().getName()  + "即将到达集合地点1，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
                  cb.await();// 到此如果没有达到公共屏障点，则该线程处于等待状态，如果达到公共屏障点则所有处于等待的线程都继续往下运行
                  Thread.sleep((long) (Math.random() * 10000));
                  System.out.println(
                              "线程" + Thread.currentThread().getName()  + "即将到达集合地点2，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
                  cb.await(); // 这里CyclicBarrier对象又可以重用
                  Thread.sleep((long) (Math.random() * 10000));
                  System.out.println(
                              "线程" + Thread.currentThread().getName()  + "即将到达集合地点3，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
                  cb.await();
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
}