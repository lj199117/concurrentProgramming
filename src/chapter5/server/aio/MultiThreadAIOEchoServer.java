package chapter5.server.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Taocr on 2017/1/18.
 */
public class MultiThreadAIOEchoServer {
    private final static int PORT = 8000;   //定义好一个端口，服务器端需要对某一端口进行监听
    private AsynchronousServerSocketChannel server; //服务器端的异步IO通道

    public MultiThreadAIOEchoServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    public void start() {   //开启服务器的方法，具体定义服务器的监听线程要做什么事
        System.out.println("Server listen on" + PORT);

        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
        	/**
        	 * 核心语句，此方法会立即返回，
        	 * 第一个参数是个附件，作用是让当前线程和后续的回调方法可以共享信息，
        	 * 第二个参数是一个接口，包含了异步操作accept()成功or失败时候的具体操作，在成功or失败时回调
        	 */
            final ByteBuffer buffer = ByteBuffer.allocate(1024);    //疑问：ByteBuffer为什么不在completed内部定义？
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {    
            	//completed 执行时证明已有客户端成功连接，因为accept成功了
                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult = null;
                try {
                    buffer.clear();
                    /**
                     * 异步读取方法，不会等待读完再返回，返回结果是一个Future，
                     * 这里调用了Future.get方法，因此会直接查询Future中的数据，即将异步的方法变为了同步
                     */
                    result.read(buffer).get(100, TimeUnit.SECONDS); 
                    buffer.flip();  //重置buffer的起始位置
                    writeResult = result.write(buffer); //将数据写入通道，立即返回一个future
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        server.accept(null, this);  //进行下一个客户端连接的准备
                        writeResult.get();  //确保write操作完成，如没完成，这步会阻塞住
                        result.close(); //关闭当前的这个Channel连接
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed" + exc);
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new MultiThreadAIOEchoServer().start(); //调用后立即返回
        while (true) {  //因为上面的语句立即返回，所以这句有存在的必要，否则还没有客户端连接程序就结束了。
            Thread.sleep(1000);
        }
    }
}