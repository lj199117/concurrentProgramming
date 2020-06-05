package chapter2;

/**
 * Created by 13 on 2017/5/4.
 */
public class VolatileDemo2 {
    static boolean running = true; // 不加volatile，不会停止running，因为cpu会优先从cpu缓存读取
    
    public static void main(String args[]) throws InterruptedException {
        new Thread(() -> {
            while(running) {
                // dosomething
            }
            System.out.println("stop running");
        }).start();
        
        Thread.sleep(1000);
        
        new Thread(() -> {
            running = false;
        }).start();
    }
}
