package chapter5.disruptor_produce_consummer;

import com.lmax.disruptor.WorkHandler;

public class Consummer implements WorkHandler<Data>{

	@Override
	public void onEvent(Data event) throws Exception {
		// 数据的读取己经由Disruptor进行封装,onEvent方法为框架的回调方法。
		int data = event.getData();
		System.out.println(Thread.currentThread().getId() + ":Event result:" + data * data);
	}

}
