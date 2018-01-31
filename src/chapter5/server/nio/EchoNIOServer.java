package chapter5.server.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoNIOServer {
	private static ExecutorService tp = Executors.newCachedThreadPool();//用于对每一个客户端进行相应的处理
	public static Map<Socket, Long> time_stat = new HashMap<>();//它用于统计在某一个Socket上花费的时间
	private Selector selector = null; // selector用于处理所有的网络连接
	
	private void startServer() throws Exception{
		/**
		 * 创建了ServerSocketChannel对象，并调用configureBlocking()方法，配置为非阻塞模式，
		 * 接下来把该通道绑定到指定端口，最后向Selector中注册事件，此处指定的是参数是OP_ACCEPT，
		 * 即指定我们想要监听accept事件，也就是新的连接发 生时所产生的事件，对于ServerSocketChannel通道来说，
		 * 我们唯一可以指定的参数就是OP_ACCEPT
		 */
		selector = SelectorProvider.provider().openSelector(); //通过工厂方法获得一个Selector对象的实例
		
		//ServerSocketChannel为SelectableChannel的一种抽象实现
		ServerSocketChannel ssc = ServerSocketChannel.open();//获得表示服务端的SocketChannel实例
		ssc.configureBlocking(false);
		InetAddress localHost = InetAddress.getLocalHost();
		InetSocketAddress isa = new InetSocketAddress(localHost, 12345);
		ssc.socket().bind(isa);//将该ServerSocketChannel绑定到指定ip地址
		
		/**
		 * 将SelectableChannel(ssc)注册到selector上面，并说明感兴趣的事件为Accept;
		 * 一旦selector发现有它感兴趣的事件(Accept:有新的客户端连接上)，
		 * 那么就会通知ServerSocketChannel进行处理，
		 * 且这个acceptKey为Channel-Selector的契约
		 */
		SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		while(true){
//			if(selector == null){
//				wait(); //加synchonized
//			}
			if (!selector.isOpen()) {
	            System.out.println("selector is closed");
	            break;
	        }
			selector.select(); //该调用会阻塞，直到至少有一个事件发生  
			Set<SelectionKey> readyKeys = selector.selectedKeys(); // 获取就绪的Channel集合
			Iterator<SelectionKey> it = readyKeys.iterator();
			long e = 0;
			while(it.hasNext()){
				SelectionKey sk = it.next();
				it.remove(); //注意，这个非常重要，否则就会重复处理相同的SelectionKey
				
				if(sk.isAcceptable()){
					doAccept(sk); // Channel在Acceptable状态
				}else if(sk.isValid() && sk.isReadable()){ //判断Channel 是否已经可以读了
					SelectableChannel channel = sk.channel();
					SocketChannel socketChannel = (SocketChannel)channel;
					Socket socket = socketChannel.socket();
					if(!time_stat.containsKey(socket)){
						time_stat.put(socket, System.currentTimeMillis());
					}
					doRead(sk); // Channel已经可以读了
				}else if(sk.isValid() && sk.isWritable()){
					doWrite(sk); // Channel已经可以写了
					e = System.currentTimeMillis();
					SelectableChannel channel = sk.channel();
					SocketChannel socketChannel = (SocketChannel)channel;
					Socket socket = socketChannel.socket();
					long b = time_stat.remove(socket);
					System.err.println("Spend:"+(e-b)+"ms");
				}
			}
		}
	}
	
	/**
	 * 它与客户端建立连接的实现过程
	 * @param sk
	 * @since 0.1.0
	 */
	private void doAccept(SelectionKey sk) {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel clientChannel;
		try {
			clientChannel = server.accept();//当有新的客户端接入,表示和客户端通信的通道
			clientChannel.configureBlocking(false);
			
			/**
			 * 新生成的Channel注册到selector选择器上,并告诉selector,我现在对读(OP_READ)操作感兴趣
			 * 当selector发现这个Channel已经准备好读时,就能及时通知它读取
			 */
			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			/**
			 * 将这个客户端实例作为附件，附加到表示这个连接的SelectionKey上。
			 * 这样在整个连接的处理过程中(读写操作可以通过共享数据)， 我们都可以共享这个EchoClient实例。
			 */
			EchoClient echoClient = new EchoClient();
			clientKey.attach(echoClient);
			
		} catch (Exception e) {
			System.out.println("Failed to accept new client.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 当Channel可以读取时,doRead方法就会被调用
	 * @param sk
	 * @since 0.1.0
	 */
	private void doRead(SelectionKey sk) {
		/**
		 * Channel通道不同于流的地方就是通道是双向的，可以用于读、写和同时读写操作。
		 * 所以拿到一个Channel就同时拿到了服务端与客户端，
		 * channel.read(bb) // 从客户端读
		 * channel.write(bb) // 写到客户端
		 */
		
		//通过这个SelectionKey可以得到当前的客户端Channel
		SocketChannel channel = (SocketChannel) sk.channel();
		ByteBuffer bb = ByteBuffer.allocate(8192);//8K的缓冲区读取数据
		int len;
		
		try {
			len = channel.read(bb);
			if(len < 0){
				/**
				 * doWrite方法在完成后,感兴趣的事件变成了READ,所以最终会调到这里,
				 * 但是客户端那边不会再发内容过来了,导致读不到内容,造成关闭
				 */
//				System.out.println("doRead len < 0, disConnect");
//				disConnect(sk);
				return;
			}
		} catch (Exception e) {
			System.out.println("Failed to read from client.");
			e.printStackTrace();
			disConnect(sk);
			return;
		}
		
		bb.flip(); //重置缓冲区,为处理新数据做准备
		/**
		 * 如果数据处理很复杂，就能在单独的线程中进行，而不用阻塞任务派发线程。
		 */
		tp.execute(new HandleMsg(sk, bb));
	}
	

	private void doWrite(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();
		EchoClient echoClient = (EchoClient) sk.attachment();
		LinkedList<ByteBuffer> outq = echoClient.getOutputQueue();
		ByteBuffer bb = outq.getLast();
		try {
			byte[] data = bb.array();
	        String msg = new String(data).trim();
			System.out.println("nio server doWrite:" + msg);
			int len = channel.write(bb);
			if(len < 0){
//				System.out.println("doWrite len < 0, disConnect");
				disConnect(sk);
				return;
			}
			if(bb.remaining() == 0){
				outq.removeLast(); // bb成功写入完成
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			disConnect(sk);
		}
		
		if(outq.size() == 0){
			// 数据发送完成后,将写事件从感兴趣的操作中去掉(即对READ感兴趣)
			sk.interestOps(SelectionKey.OP_READ);
		}
	}
	
	/**
	 * 如果捕捉到该sk对应的channel出现异常，
	 * 即表明该channel对应的client出现了异常，所以从selector中取消sk的注册
	 * @param sk
	 * @since 0.1.0
	 */
	private void disConnect(SelectionKey sk) {
		//从Selector中删除指定的SelectionKey
		sk.cancel();
		if(sk.channel() != null){
			try {
				sk.channel().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        System.err.println("disConnect channel...");
	}
	

	public static void main(String[] args) {
		try {
			new EchoNIOServer().startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
