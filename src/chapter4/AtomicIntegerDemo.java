package chapter4;

import java.util.concurrent.atomic.AtomicInteger;


public class AtomicIntegerDemo {
    static AtomicInteger i = new AtomicInteger();
    static int ii = 0;
    
    
    public static class AddThread implements Runnable {

        @Override
        public void run() {
            for (int j = 0; j < 10000; j++) {
                i.incrementAndGet();
                ii++;
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {

        Thread[] threads = new Thread[10];

        for (int j = 0; j < 10; j++) {
            threads[j] = new Thread(new AddThread());
        }

        for (int j = 0; j < 10; j++) {
            threads[j].start();
        }
        for (int j = 0; j < 10; j++) {
            threads[j].join();
        }

        System.out.println(i);
        System.out.println(ii);
    }
}
