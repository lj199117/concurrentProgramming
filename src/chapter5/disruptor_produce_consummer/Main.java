package chapter5.disruptor_produce_consummer;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class Main {
	public static void main(String[] args) throws Exception {
		Executor executor = Executors.newCachedThreadPool();
		int ringBufferSize = 1024; // ringBuffer大小,必须为2^n
		EventFactory<Data> eventFactory = new DataFactory();
		
		@SuppressWarnings("deprecation")
		Disruptor<Data> disruptor = new Disruptor<>(
				eventFactory, 
				ringBufferSize, 
				executor, 
				ProducerType.MULTI, //多例 or 单例
				new BlockingWaitStrategy());
		
		//消费者实现了WorkHandler
		disruptor.handleEventsWithWorkerPool(
				new Consummer(), 
				new Consummer(),
				new Consummer());
		
		disruptor.start();
		
		RingBuffer<Data> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		Producer producer1 = new Producer(ringBuffer);
		Producer producer2 = new Producer(ringBuffer);
		ByteBuffer bb = ByteBuffer.allocate(8);
		for(int i=0;;i++){
			bb.putInt(0, i);
			producer.produceData(bb);
			producer1.produceData(bb);
			producer2.produceData(bb);
			Thread.sleep(1000);
			System.out.println("add data:"+i);
		}
	}
}
