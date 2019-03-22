package chapter6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * map中的一些方法：merge ifabsent ifpresent
 * @author goldl
 * 参考： https://blog.csdn.net/u010938610/article/details/82622144
 * compute：原map不管是否存在key都会执行remappingFunction
 * computeifabsent：原map不存在该key的时候才执行remappingFunction
 * computeifpresent：原map存在该key的时候才执行remappingFunction
 */
public class MapStream {
	static Map<String, Collection<String>> strings = new HashMap<>();
	private static final AtomicInteger ai = new AtomicInteger();

	public static void main(String[] args) throws InterruptedException {
		testMerge();
		System.err.println("-------------------");
		testComputeIfAbsent();
		System.err.println("-------------------");
		testComputeIfPresent();
		System.err.println("-------------------");
		testComputeIfPresent2();
		System.err.println("-------------------");
	}

	static Map<String, Integer> map1 = new HashMap<>();
	static Map<String, Integer> map2 = new HashMap<>();
	static {
		map1.put("a", 1); map1.put("b", 2); map1.put("c", 3);
		map2.put("a", 4); map2.put("b", 5); map2.put("c", 6); map2.put("d", 10);
	}

	// 按照属性男女分组，然后把年龄汇总
	private static void testMerge() {
		//学生的集合
        List<Student> students = new ArrayList<>();
        students.add(new Student("张三","男",18));
        students.add(new Student("李四","男",20));
        students.add(new Student("韩梅梅","女",18));
        students.add(new Student("小红","女",45));

        //声明接收结果的 map
        Map<String, Integer> resultMap = new HashMap<>();
        for (Student student : students) {
            resultMap.merge(student.getSex(), student.getAge(), (a, b) -> b + a);
        }
		System.out.println(resultMap);
	}

	private static void testComputeIfPresent2() {
		Set<String> mapKeys = new HashSet<>(map1.keySet());
		mapKeys.addAll(map2.keySet());
		
		/**
		 * 只有在当前Map中存在key值的映射且非null时，才调用remappingFunction，
		 * 如果remappingFunction执行结果为null，则删除key的映射，否则使用该结果替换key原来的映射
		 */
		Map<String, Integer> combineMap = new HashMap<>();
		mapKeys.forEach(type -> {
			combineMap.computeIfAbsent(type, key -> genVal(key)); // 合并两个map相同key的value做和
			combineMap.computeIfPresent(type, (key, oldVal) -> genNewVal(key, oldVal)); // 将和值翻倍 & 删除key为b的元素
		});
		
		System.out.println(combineMap);
	}

	private static Integer genVal(String key) {
		Integer newVal = Optional.ofNullable(map1.get(key)).orElse(0) + 
				Optional.ofNullable(map2.get(key)).orElse(0);
		System.out.println(key + "->" + newVal);
		return newVal;
	}

	private static Integer genNewVal(String key, Integer oldVal) {
		Integer newVal = oldVal * 2;
		System.out.println(key + "->" + oldVal + "->" + newVal);
		if(key.equals("b")) return null; // 删除key为b的元素
		return newVal;
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
	    strings.computeIfPresent(index, (key, oldValue) -> oldValue.remove(a) && oldValue.isEmpty() ? null : oldValue);
	}
	
	// 测试多线程并发处理，是否同步操作
	private static void testComputeIfAbsent() throws InterruptedException {
		Map<String, HashSet<String>> map = new ConcurrentHashMap<>(); // 注意HashSet并不是线程安全的
		Map<String, Vector<String>> map1 = new ConcurrentHashMap<>(); // Vector为线程安全
		Map<String, String> map2 = new ConcurrentHashMap<>(); // Vector为线程安全
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 10; i++) {
			int incrementAndGet = ai.incrementAndGet();
			exec.execute(new Runnable() {
				@Override
				public void run() {
					map.computeIfAbsent("name", k -> genValue(k)).add("name" + incrementAndGet);
					map1.computeIfAbsent("name", k -> genVecValue(k)).add("name" + incrementAndGet);
					
					map2.computeIfAbsent(String.valueOf(incrementAndGet), k -> k + ":" + k);
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(1, TimeUnit.SECONDS);
		System.out.println("map :" + map);
		System.out.println("map1:" + map1);
		System.out.println("map2:" + map2);
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