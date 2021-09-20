package java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * https://www.jianshu.com/p/4897ccdcb278
 * 等待多个 CompletableFuture 对象执行完毕
 * @author goldl
 *
 */
public class CompletableFuture_allOf {
	public static void main(String[] args) throws Exception {
		CompletableFuture<String> completableFuture1=CompletableFuture.supplyAsync(()->{
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result1";
        });

        CompletableFuture<String> completableFuture2=CompletableFuture.supplyAsync(()->{
            //模拟执行耗时任务
            System.out.println("task2 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result2";
        });
        // test anyOf
//        CompletableFuture<Object> anyResult=CompletableFuture.anyOf(completableFuture1,completableFuture2);
//        System.out.println("第一个完成的任务结果:"+anyResult.get());

        // test allOf
        long start = System.currentTimeMillis();
        CompletableFuture<String> allResult=CompletableFuture.allOf(completableFuture1,completableFuture2).thenApply(v -> {
        	String res = "";
        	try {
        		String f1 = completableFuture1.get();
				String f2 = completableFuture2.get();
				res = f1 + f2;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        	return res;
        });
        System.out.println("res=" + allResult.get());
        System.out.println("无阻塞时" + (System.currentTimeMillis()-start)); // 也就是说thenApply会自己join等待future结果，把thenApply去掉可以看到现象
        //阻塞等待所有任务执行完成
        allResult.join();
        System.out.println("阻塞所有任务执行完成" +  (System.currentTimeMillis()-start));
	}
}

