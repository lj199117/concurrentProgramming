package chapter5.server.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by ycy on 16/1/19.
 * 这个适用LockSupport阻塞当前线程模拟
 * io耗时的情况,服务器响应就慢了;
 * 在其他语言里面,所以不要这么用,这么用就显得逼格不高了.就像生产者-消费者一样,高逼格都是用队列服务器了
 *
 */
public class HeavySocketClient {
    private static ExecutorService tp = Executors.newCachedThreadPool();
    private static final int sleep_time = 1000 * 1000 * 1000;

    public static class EchoClient implements Runnable {
        public void run() {
            Socket client = null;
            PrintWriter writer = null;
            BufferedReader reader = null;
            try {
                client = new Socket();
                client.connect(new InetSocketAddress("127.0.0.1", 12345));
                writer = new PrintWriter(client.getOutputStream(), true);
                writer.print("high client");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client1");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client2");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client3");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client4");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client5");
                LockSupport.parkNanos(sleep_time);
                writer.print("end client6");
                writer.println();
                writer.flush();
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                System.out.println("from server:" + reader.readLine());
            } catch (UnknownHostException  e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {

                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public static void main(String[] args) {
        EchoClient ec=new EchoClient();
        for (int i = 0; i <10 ; i++) {
            tp.submit(ec);
        }
    }
}

