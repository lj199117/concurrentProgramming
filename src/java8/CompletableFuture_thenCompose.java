package java8;

import java.util.concurrent.CompletableFuture;

/**
 * https://www.jianshu.com/p/4897ccdcb278
 * 将两个CompletableFuture建立联系
 * 通常，我们会有多个需要独立运行但又有所依赖的的任务。比如先等用于的订单处理完毕然后才发送邮件通知客户。
 * @author goldl
 *
 */
public class CompletableFuture_thenCompose {
	public static void main(String[] args) throws Exception {
		CompletableFuture<String> completableFuture1=CompletableFuture.supplyAsync(()->{
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result1";
        });

        //等第一个任务完成后，将任务结果传给参数result，执行后面的任务并返回一个代表任务的completableFuture
        CompletableFuture<String> completableFuture2= completableFuture1.thenCompose(result->CompletableFuture.supplyAsync(()->{
            //模拟执行耗时任务
            System.out.println("task2 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return result +":"+ "result2";
        }));

        System.out.println(completableFuture2.get());
	}
}

