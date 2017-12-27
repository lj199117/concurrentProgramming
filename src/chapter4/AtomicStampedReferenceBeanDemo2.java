package chapter4;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 这才是正确的姿势
 * @author <a href="mailto:lijin@webull.com">李锦</a>
 * @since 0.1.0
 */
public class AtomicStampedReferenceBeanDemo2 {
	static Bean2 p = new Bean2("lj", 19);
    static AtomicStampedReference<Bean2> money = new AtomicStampedReference<Bean2>(p, 0);
    static Object o = new Object();
    public static void main(String args[]) {
    	// 模拟多个线程同时持续更新后台数据库，为用户充值
    	final int timestap = money.getStamp();
        for (int i = 0; i < 3; i++) {
            
            new Thread() {
                public void run() {
                	while(true){
                		while (true) {
                			Bean2 m = money.getReference();
                			int mywallet = m.getMoney();
                            if (mywallet < 20) {
                            	Bean2 newB = new Bean2("lj", mywallet+ 20);
                                if (money.compareAndSet(m, newB, timestap, timestap + 1)) {
//System.err.println(timestap);
                                    System.out.println("余额小于20元,充值成功,余额:" + money.getReference().getMoney() + "元");
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
        
        //单线程100次消费
        new Thread() {
            public void run() {
            	// 模拟100次消费行为
                for (int i = 0; i < 10; i++) {
                    while (true) {
                    	int timestap = money.getStamp();//预期时间戳
                    	Bean2 m = money.getReference(); // 预期值,m与p都是同一个引用
//System.err.println("第"+i+"次m:"+timestap+"-"+m+"-"+m.getMoney());
                    	int mywallet = m.getMoney();
                        if (mywallet > 10) {
                        	Bean2 newB = new Bean2("lj", mywallet - 10);
                            if (money.compareAndSet(m, newB, timestap, timestap + 1)) {
                                System.out.println("第"+i+"次成功消费10元,余额:" + money.getReference().getMoney() + "元");
                                break;// 当前消费行为结束,进行i++下一次消费行为
                            }
                        } else {
                            System.out.println("没有足够的金额");
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

class Bean2{
	private String name;
	private int money;
	public Bean2(String name, int money) {
		this.name = name;
		this.money = money;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
}