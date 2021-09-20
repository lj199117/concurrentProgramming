package chapter3;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo2 {
    
    /**
     * 先unpark再park
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
                    e.printStackTrace();
                }
                LockSupport.park(a);
                System.out.println("7点到，起床");
            }
        });
        t.setName("A-Name");
        t.start();
        LockSupport.unpark(t);
        System.out.println("提前上好闹钟7点起床");
    }
}