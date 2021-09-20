package java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureWithErrDemo {
	public static void main(String[] args) throws Exception, ExecutionException {
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
	    new Thread(() -> {
	        // 模拟执行耗时任务
	        System.out.println("task doing...");
	        try {
	            Thread.sleep(3000);
	            int i = 1/0;
	        } catch (Exception e) {
	            // 告诉completableFuture任务发生异常了
	            completableFuture.completeExceptionally(e);
	        }
	        // 告诉completableFuture任务已经完成
	        completableFuture.complete("ok");
	    }).start();
	    // 获取任务结果，如果没有完成会一直阻塞等待
	    String result = completableFuture.get();
	    System.out.println("计算结果:" + result);
	}
}
