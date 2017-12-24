package chapter4;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by 13 on 2017/5/6.
 */
public class AtomicStampedReferenceDemo {
    static AtomicStampedReference<Integer> money = new AtomicStampedReference<Integer>(19, 0);

    public static void main(String args[]) {
    	// 模拟多个线程同时持续更新后台数据库，为用户充值
        for (int i = 0; i < 3; i++) {
            final int timestap = money.getStamp();
            new Thread() {
                public void run() {
                	while(true){
                		while (true) {
                            Integer m = money.getReference();
                            if (m < 20) {
                                if (money.compareAndSet(m, m + 20, timestap, timestap + 1)) {
                                    System.out.println("余额小于20元,充值成功,余额:" + money.getReference() + "元");
                                    break;
                                }
                            } else {
//                                System.out.println("余额大于20,无需充值");
                                break;
                            }
                        }
                	}
                }
            }.start();
        }

        new Thread() {
            public void run() {
            	// 模拟100次消费行为
                for (int i = 0; i < 100; i++) {
                    while (true) {
                        int timestap = money.getStamp();//预期时间戳
                        Integer m = money.getReference(); // 预期值
                        if (m > 10) {
                            System.out.println("金额大于10元");
                            if (money.compareAndSet(m, m - 10, timestap, timestap + 1)) {
                                System.out.println("成功消费10元,余额:" + money.getReference() + "元");
                                break;// 当前消费行为结束,进行i++下一次消费行为
                            }
                        } else {
//                            System.out.println("没有足够的金额");
                            break;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}