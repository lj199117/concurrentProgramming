package chapter5.produce_cosummer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable{
	private static AtomicInteger count = new AtomicInteger(); //总数，原子操作
	private BlockingQueue<Data> queue;
	private boolean isRunning = true;

	public Producer(BlockingQueue<Data> queue) {
		super();
		this.queue = queue;
	}

	@Override
	public void run() {
		while(isRunning){
			try {
				int data = count.incrementAndGet();
				System.err.println("Producer data:"+data);
				if(!queue.offer(new Data(data), 2, TimeUnit.SECONDS)){
					System.err.println("Producer put data failed data:" + data);
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop(){
		this.isRunning = false;
	}
}
