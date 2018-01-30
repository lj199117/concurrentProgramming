package chapter5.server.nio;

import java.nio.ByteBuffer;
import java.util.LinkedList;
/**
 * 封装一个队列，保存需要回复给客户端的所有信息，
 * 在进行回复时，只要从outqueue 对象中弹出元素即可
 * 
 * @author <a href="mailto:lijin@webull.com">李锦</a>
 * @since 0.1.0
 */
public class EchoClient {
	private LinkedList<ByteBuffer> outqueue;
	public EchoClient() {
		outqueue = new LinkedList<>();
	}
	public LinkedList<ByteBuffer> getOutputQueue(){
		return outqueue;
	}
	public void enqueue(ByteBuffer bb){
		outqueue.addFirst(bb);
	}
}
