package chapter3;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 13 on 2017/5/5.
 */
public class CountDownLatchDemo {
	static final CountDownLatch endLatch = new CountDownLatch(10);

	public static void main(String args[]) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			executorService.submit(() -> {
			    try {
		            Thread.sleep(new Random().nextInt(5) * 1000);
		            System.out.println("check complete");
		            endLatch.countDown();
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
			});
		}
		// 等待检查,end.countDown();执行 10次
		endLatch.await();//要求主线程等待所有10个检查任务全部完成。待10 个任务全部完成后， 主线程才能继续执行。
		// 发射火箭
		System.out.println("Fire!");
		executorService.shutdown();
	}
}
