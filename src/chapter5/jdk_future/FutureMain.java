package chapter5.jdk_future;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

/**
 * Created by 13 on 2017/5/8.
 */
public class FutureMain {

	public static void main(String args[]) throws ExecutionException, InterruptedException {
		// 构造FutureTask,相当于之前介绍的RealData
		FutureTask<String> futureTask = new FutureTask<String>(new RealData("a"));

		ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
		    @Override
		    public Thread newThread(Runnable target) {
		        final Thread thread = new Thread(target);
		        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
		            @Override
		            public void uncaughtException(Thread t, Throwable e) {
		            	System.out.println("-----throws exception-------" + e.getMessage());
		            }
		            
		        });
		        return thread;
		    }
		    
		});
		
		
		// 执行FutureTask,相当于前一个例子中的client.request("a")发送请求
		// 在这里开启线程进行RealData的call()执行
		Future<?> future = executorService.submit(futureTask);
		System.out.println("请求完毕");
		System.out.println("future:" + future.get());
		System.out.println("futureTask:" + futureTask.get()); // what is the diff bwt future and futureTask?
		
		/*executorService.execute(new Runnable() {
			@Override
			public void run() {
				throw new RuntimeException("lijin ex");
			}
		});
		executorService.execute(futureTask);*/
		try {
			// 这里依然可以做额外的数据操作,使用sleep代替其他业务逻辑的处理
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 相当于前一个例子中的data.getResult(),取得call()方法的返回值
		// 如果此时call()方法没有执行完成,则依然会等待
		System.out.println("数据=" + futureTask.get());
	}
	
	

	private static class ExceptionCatchingThreadFactory implements ThreadFactory {
		private final ThreadFactory delegate;
		
		private ExceptionCatchingThreadFactory(ThreadFactory delegate) {
			this.delegate = delegate;
		}

		public Thread newThread(final Runnable r) {
			Thread t = delegate.newThread(r);
			t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					e.printStackTrace(); // replace with your handling logic.
				}
			});
			return t;
		}
	}
}