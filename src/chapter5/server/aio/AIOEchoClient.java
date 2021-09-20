package chapter5.server.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by Taocr on 2017/1/18.
 */
public class AIOEchoClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();  //第一句，打开一个客户端的异步Channel
        client.connect(new InetSocketAddress("localhost", 8000), null, new CompletionHandler<Void, Object>() {      
        	//第二句，连接一个具体的地址同时指定成功时要做什么，失败时要做什么。完全是异步，因此会立即返回
            @Override
            public void completed(Void result, Object attachment) { 
            	//连接成功后要做的事，只不过是将数据写入而已，这里由于是异步的所以一句中要指定具体的写入成功和失败后的操作
                client.write(ByteBuffer.wrap("Hello!".getBytes()), null, new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {  
                    	/**
                    	 * write写完后要做的事：读取写回的数据，这里的语句长是因为要编写读取完成后失败后的事情，
                    	 * 实际上逻辑就是写入后要读取具体的数据到bb中，之后将bb中的数据进行显示。
                    	 */
                        ByteBuffer bb = ByteBuffer.allocate(1024);
                        client.read(bb, bb, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {  
                            	//read成功后要做的事，进行显示
                                bb.flip();
                                System.out.println(new String(bb.array()));
                                try {
                                    client.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {

                            }
                        });
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {  //write写失败后要做的事
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {  //连接失败后要做的事

            }
        });

        Thread.sleep(1000); //第三句，等待上面的线程执行完毕
    }
}