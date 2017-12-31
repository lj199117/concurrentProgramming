package chapter5;
/**
 * 
 * @author <a href="mailto:lijin@webull.com">李锦</a>
 * @since 0.1.0
 */
public class ImmutablePattenLong {
	static Long v = new Long(0l);
	public static void main(String[] args) throws Exception {
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<5000;i++)
//					v = v + 1;
					v = Long.sum(1, v);
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<5000;i++){
//					v = v + 1;
					v = Long.sum(1, v);//这是类方法
				}
			}
		});
		t2.start();
		
		t1.join();
		t2.join();
		
		System.out.println(v);
	}
}
