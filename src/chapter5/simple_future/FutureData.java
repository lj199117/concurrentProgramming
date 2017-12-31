package chapter5.simple_future;

/**
 * Created by 13 on 2017/5/8.
 */
public class FutureData implements Data {

    protected RealData realData = null; // FutureData是RealData的包装,以聚合的方式
    protected boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        if (isReady) {
            return;
        }
        this.realData = realData;
        isReady = true;
        notifyAll();//RealData已经设置成功，通知getResult()
    }


    @Override
    public synchronized String getResult() {
        while (!isReady) {
            try {
                wait(); // 等待RealData构造完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return realData.result;
    }
}
