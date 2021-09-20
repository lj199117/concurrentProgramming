package chapter5.simple_future;

/**
 * Created by 13 on 2017/5/8.
 */
public class RealData implements Data {

    protected final String result; //则其数值一旦在初始化之后便不能更改

    public RealData(String param) {
        //RealData的构造很慢,需要用户等待很久,这里用sleep模拟
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            stringBuffer.append(param);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        result = stringBuffer.toString();
    }

    @Override
    public String getResult() {
        return result;
    }
}