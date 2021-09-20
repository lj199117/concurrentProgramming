package chapter5.server.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

public class EchoNIOClientInter {
	//定义处理编码和解码的字符集
	private Charset charset=Charset.forName("UTF-8");
    private Selector selector;

    public void init(String ip , int port) throws IOException {
    	this.selector = SelectorProvider.provider().openSelector();     //实例化一个Selector用于IO复用
    	
    	SocketChannel channel = SocketChannel.open();
    	channel.configureBlocking(false);   //创建一个SocketChannel实例并设置为非阻塞模式
        channel.connect(new InetSocketAddress(ip, port));   //channel连接具体指定的ip地址：端口，但此时不一定就连接上了
        channel.register(selector, SelectionKey.OP_CONNECT);    //将此通道登记在selector，感兴趣的事件是connect
    }

    public void working() throws IOException {  //正式工作的客户端代码
        while (true) {
            if (!selector.isOpen()) //对于selector不处于开启状态，即我们后面关闭了selector的时候，整个客户端也就跳出了它具体运行的代码
                break;
            selector.select();  //开始监控
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();    //监控返回时代表有事件满足了，获取准备好的事件的集合的迭代器
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                ite.remove();   //remove一定要，因为selector不会自己将准备好的事件处理后就删去重新监控，如果不执行remove，下一次生成的时候无论SelectionKey是否就绪它都会存在于这个Set中
                if (key.isConnectable()) {  //根据SelectionKey具体情况进行判断执行哪种方法
                    connect(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

	public void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel)key.channel();   //通过SelectionKey来获取具体对应的channel
        if (channel.isConnectionPending()) {    //确认当前channel是否真的连接上了
            channel.finishConnect();    //如未连接，那么调用finishConnect完成连接
        }
        channel.configureBlocking(false);   //设置这个channel为非阻塞状态，奇怪的是，应该在之前设置了，为什么这里还要设置
        channel.write(ByteBuffer.wrap(new String("1+1+1").getBytes())); //将一个语句放入ByteBuffer后写入，即写想了服务器端
        channel.register(this.selector, SelectionKey.OP_READ);  //写入完成后就需要等待Server的回复，因此将此channel登记为对读感兴趣，此时已经连接上了就不需要再对connect感兴趣了
    }

    public void read(SelectionKey sk) throws IOException {
        SocketChannel channel = (SocketChannel) sk.channel();   //获取具体的channel
        ByteBuffer buffer = ByteBuffer.allocate(100);
        String msg = "";
        while(channel.read(buffer) > 0){
        	buffer.flip();
        	msg += charset.decode(buffer);
		}
        System.out.println("客户端收到信息：" + msg);
        //为下一次读/写做准备
		sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        
        /*channel.read(buffer);   //直接读取对于读数据已经准备好的channel
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println("客户端收到信息：" + msg);
        sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);*/
    }
    
    private void write(SelectionKey sk) {
    	SocketChannel channel = (SocketChannel) sk.channel();   //获取具体的channel
    	//创建键盘输入流
		Scanner scan = new Scanner(System.in);
		//读取键盘输入
		String line = scan.nextLine();
		//将键盘输入的内容输出到SocketChannel中
		try {
			channel.write(charset.encode(line));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		sk.interestOps(SelectionKey.OP_READ);
		sk.selector().wakeup();//强制selector立即返回
	}
    
    public static void main(String[] args) {
		try {
			EchoNIOClientInter echoNIOClient = new EchoNIOClientInter();
			InetAddress localHost = InetAddress.getLocalHost();
			echoNIOClient.init(localHost.getHostAddress(), 12345);
			echoNIOClient.working();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
