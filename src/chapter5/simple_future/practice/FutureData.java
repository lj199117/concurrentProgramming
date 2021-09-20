package chapter5.simple_future.practice;

public class FutureData implements Data{
	boolean isReady;
	String result; //自己练习的时候没搞好的就是没用聚合的方式实现
	@Override
	public synchronized String getResult() {
		while(!isReady){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public synchronized void setResult(String result) {
		this.result = result;
		isReady = true;
		notifyAll();
	}

}
