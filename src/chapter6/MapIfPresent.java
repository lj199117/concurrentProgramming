package chapter6;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MapIfPresent {
	static Map<String, Collection<String>> strings = new HashMap<>();
	private static final AtomicInteger ai = new AtomicInteger();

	public static void main(String[] args) throws InterruptedException {
		testComputeIfAbsent();

		testComputeIfPresent();
	}

	private static void testComputeIfPresent() throws InterruptedException {
		addString("a1");         // {a=[a1]}      <-- collection dynamically created
		addString("a2");         // {a=[a1, a2]}
		removeString("a1");      // {a=[a2]}
		System.out.println(strings);
		removeString("a2");      // {}            <-- both key and collection removed
		System.out.println(strings);
		
	}
	private static void addString(String a) {
	    String index = a.substring(0, 1);
	    strings.computeIfAbsent(index, ign -> new HashSet<>()).add(a);
	}

	private static void removeString(String a) {
	    String index = a.substring(0, 1);
	    /**
		 *  只有在当前Map中存在key值的映射且非null时，才调用remappingFunction，
		 *  如果remappingFunction执行结果为null，则删除key的映射，否则使用该结果替换key原来的映射
		 *  
		 *  注意: 尝试把  && c.isEmpty()注销可以看到效果
		 */
	    strings.computeIfPresent(index, (key, value) -> value.remove(a) && value.isEmpty() ? null : value);
	}
	
	// 测试多线程并发处理，是否同步操作
	private static void testComputeIfAbsent() throws InterruptedException {
		Map<String, HashSet<String>> map = new ConcurrentHashMap<>(); // 注意HashSet并不是线程安全的
		Map<String, Vector<String>> map1 = new ConcurrentHashMap<>(); // Vector为线程安全
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 10; i++) {
			int incrementAndGet = ai.incrementAndGet();
			exec.execute(new Runnable() {
				@Override
				public void run() {
					map.computeIfAbsent("name", k -> genValue(k)).add("name" + incrementAndGet);
					map1.computeIfAbsent("name", k -> genVecValue(k)).add("name" + incrementAndGet);
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(1, TimeUnit.SECONDS);
		System.out.println("map:" + map);
		System.out.println("map1:" + map1);
	}

	// 根据key 产生一个value, 只会调用一次
	static HashSet<String> genValue(String str) {
		System.out.println("map genValue:" + str);
		return new HashSet<String>();
	}

	static Vector<String> genVecValue(String str) {
		System.out.println("map1 genVecValue:" + str);
		return new Vector<String>();
	}
}