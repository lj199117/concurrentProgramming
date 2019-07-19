package java8;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorCompletionServiceTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Random random = new Random();
//		ExecutorService pool = Executors.newFixedThreadPool(3);
		/**
		 *  这个线程池最大只能容纳10个任务(maximumPoolSize + 队列长度),其他更多的将会被拒绝
		 *  任务数少于2的之后之间运行，后面再来4个任务会被加入有界队列中
		 *  此时再来的任务将会创建新的线程来运行直到池中允许的最大运行线程数maximumPoolSize达到6个，其余的任务将被抛弃
		 */
		ExecutorService pool = new ThreadPoolExecutor(2, 6,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(4), new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						System.out.println(r.toString() + "|被拒绝");
					}
				});
		
		CompletionService<String> service = new ExecutorCompletionService<String>(pool);
		for (int i = 0; i < 40; i++) {
			int data = i;
			service.submit(() -> {
//				Thread.sleep(random.nextInt(10000));
				Thread.sleep(5000);
				System.out.println(Thread.currentThread().getName() + "|完成任务");
				return "data" + data;
			});
		}
		
		for (int j = 0; j < 40; j++) {
			Future<String> take = service.take(); // 这一行没有完成的任务就阻塞
			String result = take.get(); // 这一行在这里不会阻塞，引入放入队列中的都是已经完成的任务
			System.out.println(System.currentTimeMillis() + "获取到结果：" + result);
		}
	}
}
