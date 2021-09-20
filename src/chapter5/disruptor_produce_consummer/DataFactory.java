package chapter5.disruptor_produce_consummer;

import com.lmax.disruptor.EventFactory;

public class DataFactory implements EventFactory<Data>{

	@Override
	public Data newInstance() {
		return new Data();
	}

}
