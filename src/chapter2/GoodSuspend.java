package chapter2;

public class GoodSuspend {
	
	public static Object o = new Object();
	
	static ChanngeObjectThread t1 = new ChanngeObjectThread("ChanngeObjectThread");
	static ChanngeObjectThread t1_1 = new ChanngeObjectThread("ChanngeObjectThread-1");
	static ReadObjectThread t2 = new ReadObjectThread("ReadObjectThread");

	public static void main(String[] args) throws Exception{
		t1.start();
//		t1_1.start();
		t2.start();
		Thread.sleep(1000);
		t1.suspendMe();
//		t1_1.suspendMe();
		System.out.println("----suspend 3 sec");
		Thread.sleep(3000);
		System.out.println("---resume");
		t1.resumeMe();
	}
	
	public static class ChanngeObjectThread extends Thread{
		volatile boolean suspend = false;
		
		public ChanngeObjectThread(String name) {
			super(name);
		}
		
		public void suspendMe(){
			suspend = true;
		}
		
		public void resumeMe(){
			suspend = false;
			
			synchronized (this) {
				notifyAll(); // 提醒任一线程, 如果不notify() 那该线程不会再执行
			}
		}
		
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				synchronized (this) {
					while(suspend){
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				synchronized (o) {
					System.out.println("in " + this.getName());
				}
				Thread.yield();
				/**
				 * 使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。cpu会从众多的可执行态里选择，
				 * 也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，并不是说一定会执行其他线程而该线程在下一次中不会执行到了。
				 */
			}
		}
	}
	
	public static class ReadObjectThread extends Thread{
		
		public ReadObjectThread(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				synchronized (o) {
					System.out.println("in " + this.getName());
				}
				
				Thread.yield();
			}
		}
		
	}
}
