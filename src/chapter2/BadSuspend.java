package chapter2;

public class BadSuspend {
	
	public static Object o = new Object();
	
	static ChanngeObjectThread t1 = new ChanngeObjectThread("t1");
	static ChanngeObjectThread t2 = new ChanngeObjectThread("t2");

	public static void main(String[] args) throws Exception{
		t1.start();
		Thread.sleep(100);
		t2.start();
		
		t1.resume();// t1是可以正常resume()的
		t2.resume();
		
		t1.join();
		t2.join();
	}
	
	public static class ChanngeObjectThread extends Thread{
		
		public ChanngeObjectThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			synchronized (o) {
				System.out.println("in "+this.getName());
				Thread.currentThread().suspend(); // suspend是不会释放锁的
			}
		}
	}
}
