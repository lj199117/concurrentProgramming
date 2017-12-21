package chapter3;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by 13 on 2017/5/5.
 */
public class CountTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10000; // 一个任务最多处理的求和个数

    private long start;
    private long end;

    public CountTask(long start, long end) {
        this.start = start;
        this.end = end;
    }


    @Override
    protected Long compute() {
        long sum = 0;
        boolean canCompute = (end - start) < THRESHOLD;
        if (canCompute) {
            for (long i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            long step = (start + end) / 100;

            ArrayList<CountTask> subTasks = new ArrayList<CountTask>();
            long pos = start;

            for (int i = 0; i < 100; i++) {
                long lastOne = pos + step;
                if (lastOne > end) {
                    lastOne = end;
                }
                CountTask subTask = new CountTask(pos, lastOne);
                pos += step + 1;
                subTasks.add(subTask);
                subTask.fork();// 使用fork()提交子任务
            }

            for (CountTask t : subTasks) {
                sum += t.join(); // 等待所有的子任务结束，并将结果再次求和
            }
        }
        return sum;
    }


    public static void main(String args[]) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask task = new CountTask(0, 200000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(task);

        long res = 0;
        try {
            res = result.get();//如果在执行get()方法时，任务没有结束，那么主线程就会在get()方法 时等待。
            System.out.println("sum=" + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
