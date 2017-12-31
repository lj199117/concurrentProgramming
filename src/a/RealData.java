package a;

public class RealData implements Data {
	private static String result = "";
	@Override
	public String getResult() {
		try {
			for(int i=0;i<10;i++){
				result.concat(""+i);
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
