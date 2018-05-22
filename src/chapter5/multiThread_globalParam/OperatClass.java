package chapter5.multiThread_globalParam;

public class OperatClass extends AbstractClass implements Runnable {

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			print(i);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		OperatClass target = new OperatClass(); // 模拟Spring 的Service是单例
		Thread t1 = new Thread(target); // 模拟每次过来用户请求, 都会启动一个新线程
		Thread t2 = new Thread(target);
		Thread t3 = new Thread(target);
		
		t1.start();
		t2.start();
		t3.start();
		
//		t1.join();
//		t2.join();
//		t3.join();
	}
}
