package chapter3;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo3 {
    
    /**
     * 先interrupt再park
     */
    public static void main(String[] args) throws InterruptedException {
        String a = new String("A");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("睡觉");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace(); // 异常是sleep抛出来的sleep interrupted，并不是park方法
                }
                LockSupport.park(a);
                System.out.println("起床");
                System.out.println("是否中断：" + Thread.currentThread().isInterrupted());
            }
        });
        t.setName("A-Name");
        t.start();
        t.interrupt();
        System.out.println("突然肚子很疼");
    }
}