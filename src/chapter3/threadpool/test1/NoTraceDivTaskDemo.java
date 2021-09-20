package chapter3.threadpool.test1;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/5/5.
 */
public class NoTraceDivTaskDemo {

    public static void main(String args[]) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        for (int i = 0; i < 5; i++) {
            /*poolExecutor.submit(new DivTask(100, i));*/ //没有报错提示
            poolExecutor.execute(new DivTask(100, i));//有报错提示
        }
    }

}