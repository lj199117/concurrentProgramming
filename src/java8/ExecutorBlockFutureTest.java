package java8;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 与ExecutorCompletionServiceTest对比
 * ExecutorCompletionService的优势就是他在take方法拿结果的时候是哪个任务完事了它就可以拿到哪个，但是ExecutorBlockFutureTest这个就不行，它是顺序拿，没完成就干等着
 * @author goldl
 *
 */
public class ExecutorBlockFutureTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Random random = new Random();
		ExecutorService pool = Executors.newFixedThreadPool(5);
		List<Future<String>> resultFuture = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			final int tmp = i;
			Future<String> future = pool.submit(() -> {
				Thread.sleep(5000);
				System.out.println(Thread.currentThread().getName() + "|完成任务");
				return "data" + tmp;
			});
			resultFuture.add(future);
		}

		for (Future<String> future : resultFuture) {
			String result = future.get();
			System.out.println("执行结果" + result);
		}
	}
}
