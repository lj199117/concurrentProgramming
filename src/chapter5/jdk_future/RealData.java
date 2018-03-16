package chapter5.jdk_future;

import java.util.concurrent.Callable;

/**
 * Created by 13 on 2017/5/8.
 */
public class RealData implements Callable<String> {
    private String data;

    public RealData(String data) {
        this.data = data;
    }

    @Override
    public String call() throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            stringBuffer.append(data);
            Thread.sleep(100);
            
			if(i == 8) {
				throw new Exception("error");
			}
        }
        
        return stringBuffer.toString();
    }
}
