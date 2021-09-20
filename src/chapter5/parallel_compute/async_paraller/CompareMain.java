package chapter5.parallel_compute.async_paraller;

public class CompareMain {
	public static void main(String[] args) throws Exception {
		System.err.println(System.currentTimeMillis());
		int sum = 0;
		for(int i=0;i<=1000;i++){
			sum += i;
			Thread.sleep(10);
		}
		System.err.println(System.currentTimeMillis());
		System.out.println(sum);
	}
}
