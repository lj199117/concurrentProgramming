package chapter5.simple_future.practice;

public class Client {

	public Data queryByParam(String param) {
		FutureData futureData = new FutureData();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				RealData realData = new RealData(param);
				String result = realData.getResult();
				futureData.setResult(result);
			}
		}).start();
		
		return futureData;
	}

}
