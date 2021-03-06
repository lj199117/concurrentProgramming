package chapter4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 13 on 2017/5/6.
 */
public class ThreadLocalDemo_GC {
    static volatile ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
        protected void finalize() throws Throwable {
            System.out.println(this.toString() + " is gc");
        }
    };

    static volatile CountDownLatch countDownLatch = new CountDownLatch(100);

    public static class ParseDate implements Runnable {
        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {

            try {
                if (threadLocal.get() == null) {
                    threadLocal.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
						private static final long serialVersionUID = 3989320175386660361L;

						protected void finalize() throws Throwable {
                            System.out.println(this.toString() + "SimpleDateFormat is gc");
                        }
                    });
                    System.out.println(Thread.currentThread().getId() + " create SimpleDatFormat");
                }
                Date date = threadLocal.get().parse("2017-05-06 12:33:" + i % 60);
//                System.out.println(i + ":" + date);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }


    public static void main(String args[]) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executorService.execute(new ParseDate(i));
        }
        countDownLatch.await();

        System.out.println("mission complete!");
        // 更容易被垃圾回收器发现回收
        threadLocal = null;
        System.gc();
        System.out.println("first GC complete!!");
        // 在设置ThreadLoca1 的时候， 会清除ThreadLocalMap 中的无效对象
        threadLocal = new ThreadLocal<SimpleDateFormat>();

        countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.execute(new ParseDate(i));
        }

        countDownLatch.await();
        Thread.sleep(1000);
        System.gc();
        System.out.println("second GC complete!");
        
        executorService.shutdown();
    }
}
