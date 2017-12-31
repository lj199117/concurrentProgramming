package chapter5.produce_cosummer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {
	private static final int queueSize = 5;
	
	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(10);
		BlockingQueue<Data> queue = new LinkedBlockingDeque<>(queueSize);
		
		service.execute(new Producer(queue));
		
		service.execute(new Consummer(queue));
		service.execute(new Consummer(queue));
		service.execute(new Consummer(queue));
		service.execute(new Consummer(queue));
		service.execute(new Consummer(queue));
	}
}
