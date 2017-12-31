package chapter5.produce_consummer;

import java.util.concurrent.BlockingQueue;

public class Consummer implements Runnable{
	private BlockingQueue<Data> queue;
	
	public Consummer(BlockingQueue<Data> queue) {
		super();
		this.queue = queue;
	}


	@Override
	public void run() {
		try {
			while(true){
				Data date = queue.poll(); //take会阻塞性的拿，一定要拿到数据
				if(date != null){
					int value = date.getData();
					System.out.println("result="+(value * value));
				}else{
					System.out.println("Consummer take data failed");
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
