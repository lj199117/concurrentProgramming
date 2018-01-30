package chapter5.server.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class HandleMsg implements Runnable{
	SelectionKey sk;
	ByteBuffer bb;
	public HandleMsg(SelectionKey sk, ByteBuffer bb) {
		this.sk = sk;
		this.bb = bb;
	}

	@Override
	public void run() {
		EchoClient echoClient = (EchoClient) sk.attachment();
		/**
		 * 简单地将接收到的数据压入EchoClient的队列
		 * 如果需要处理业务逻辑，就可以在这里进行处理
		 */
		echoClient.enqueue(bb); // 将收到的数据加入需要发送给客户端的队列中(收到什么就给客户端发什么)
		
		/**
		 * 在数据处理完成后，就可以准备将结果回写到客户端，
		 * 因此，重新注册感兴趣的消息事件，将写操作(OP_WRITE)也作为感兴趣的事件进行提交
		 * 一旦通道准备好写入,就能通知selector做操作
		 */
		sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE); //重新注册感兴趣的消息事件
		sk.selector().wakeup();//强制selector立即返回
	}
}
