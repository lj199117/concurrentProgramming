package chapter5.parallel_compute.async_paraller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 并行求和 可以利用到渠道的佣金计算中,加快访问速度
 * 
 * 与{@linkplain chapter5.parallel_compute.async_paraller.CompareMain}相比较,可以看出有很大差距
 * CompareMain :1516431789329 -> 1516431799628=10299
 * Main : 1516431869160 -> 1516431870192=1032
 * 差10个数量级
 * 
 * @author <a href="mailto:lijin@webull.com">李锦</a>
 * @since 0.1.0
 */
public class Main {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();
		System.err.println(System.currentTimeMillis());
		// 创建10个任务并执行
		for (int i = 0; i < 10; i++) {
			// 使用ExecutorService执行Callable类型的任务，并将结果保存在future变量中
			int start = 100 * i + 1;
			int end = 100 * i + 100;
			Future<Integer> future = executorService.submit(new RealData(start, end));
			// 将任务执行结果存储到List中
			resultList.add(future);
		}
		System.err.println(System.currentTimeMillis());

		int sum = 0;
		// 遍历任务的结果
		for (Future<Integer> fs : resultList) {
			try {
				Integer value = fs.get();
				sum += value;
				System.out.println(value); // 打印各个线程（任务）执行的结果
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				// 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
				executorService.shutdown();
			}
		}
		System.err.println(System.currentTimeMillis());
		
		System.out.println("sum："+sum);
	}
}
