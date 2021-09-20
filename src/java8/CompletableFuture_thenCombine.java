package java8;

import java.util.concurrent.CompletableFuture;

/**
 * https://www.jianshu.com/p/4897ccdcb278
 * 你需要将两个完全不相干的 CompletableFuture 对象的结果整合起来，
 * 而且你也不希望等到第一个任务完全结束才开始第二项任务
 * @author goldl
 *
 */
public class CompletableFuture_thenCombine {
	public static void main(String[] args) throws Exception {
		CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return 100;
        });

        //将第一个任务与第二个任务组合一起执行，都执行完成后，将两个任务的结果合并
        CompletableFuture<Integer> completableFuture2 = completableFuture1.thenCombine(
                //第二个任务
                CompletableFuture.supplyAsync(() -> {
                    //模拟执行耗时任务
                    System.out.println("task2 doing...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //返回结果
                    return 2000;
                }),
                //合并函数
                (result1, result2) -> result1 + result2);

        System.out.println(completableFuture2.get());
	}
}

