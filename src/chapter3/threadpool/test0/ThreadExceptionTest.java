package chapter3.threadpool.test0;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:线程池异常测试
 * https://mp.weixin.qq.com/s/dqOy2eeeOsDa1AN3nNUftg
 * @author crossoverJie Date: 2019-03-07 20:35
 * @since JDK 1.8
 */
public class ThreadExceptionTest {
	public static void main(String[] args) throws InterruptedException {
		ExecutorService execute = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		execute.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("=====11=======");
			}
		});
		TimeUnit.SECONDS.sleep(5);
		execute.execute(new Run1());
		// TimeUnit.SECONDS.sleep(5);
		//
		// execute.execute(new Run2());
		// execute.shutdown();
	}

	private static class Run1 implements Runnable {
		@Override
		public void run() {
			int count = 0;
			while (true) {
				count++;
				System.out.println("-------222-------------" + count);
				if (count == 10) {
					System.out.println(1 / 0); // 如果不try住异常，线程池一直waitting
					try {
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (count == 20) {
					System.out.println("break after count=" + count);
					break;
				}
			}
		}
	}

	private static class Run2 implements Runnable {
		public Run2() {
			System.out.println("run2 构造函数");
		}

		@Override
		public void run() {
			System.out.println("run222222222");
		}
	}
}