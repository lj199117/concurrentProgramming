package chapter5.disruptor_produce_consummer;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;

public class Producer {
	private final RingBuffer<Data> ringBuffer;

	public Producer(RingBuffer<Data> ringBuffer) {
		super();
		this.ringBuffer = ringBuffer;
	}
	
	public void produceData(ByteBuffer bb){
		long dataIndex = ringBuffer.next(); // 得到下一个可用的序列号,该序列号可以拿到下一个可用的Data
		try {
			Data event = ringBuffer.get(dataIndex);
			int data = bb.getInt(0);
			event.setData(data); // 这里进行真正的赋值,设置期望的data,该值最终会传递给消费者
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			ringBuffer.publish(dataIndex); // 数据发布,只有发布的数据才能被消费者看到
		}
	}
}
