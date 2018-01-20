package chapter5.jdk_future.parallel_sum;

import java.util.concurrent.Callable;

/**
 * 求解start-end之间的和
 * 
 * @author <a href="mailto:lijin@webull.com">李锦</a>
 * @since 0.1.0
 */
public class RealData implements Callable<Integer> {
	private int start;
	private int end;

	public RealData(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public Integer call() throws Exception {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += i;
			Thread.sleep(10);
		}
		return sum;
	}
}
