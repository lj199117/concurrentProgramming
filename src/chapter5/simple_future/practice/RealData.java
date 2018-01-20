package chapter5.simple_future.practice;

public class RealData implements Data {
	private final String param;
	
	public RealData(String param) {
		this.param = param;
	}

	@Override
	public String getResult() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "param:"+param;
	}
}
