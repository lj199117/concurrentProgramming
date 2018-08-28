package chapter6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamMain {
	@FunctionalInterface
	interface UserFactory<U extends User> {
		U create(int age, String name, BigDecimal money);
	}

	static UserFactory<User> uf = User::new;

	public static void main(String[] args) {
		List<User> list = new ArrayList<User>();
		init(list);
		// 1对数据进行过滤然后输出
		Predicate<User> predicate = u -> u.age > 6;
		Predicate<User> otherPredicate = u -> u.age == 3;
		list.stream().filter(predicate.or(otherPredicate)).forEach(System.out::print);
		System.out.println();
		// 2对数据进行过滤然后迭代这些元素
		System.out.println("Stream不能复用,上面forEach应用完,该Stream会关闭");
		Iterator<User> it = list.stream().filter(predicate.or(otherPredicate)).iterator();
		while (it.hasNext()) {
			User u = it.next();
			System.out.print(u);
		}
		System.out.println();
		// 3对数据进行过滤然后得到这个过滤后的集合
		List<User> resultList = list.stream().filter(predicate.or(otherPredicate)).collect(Collectors.toList());
		resultList.forEach(System.out::print);
		System.out.println();
		
		// 4并行流,得到这个集合中用户的年龄一些统计数据
		// 并行流
		IntSummaryStatistics statistics = list.parallelStream().collect(
				Collectors.summarizingInt(User::getAge));
		System.out.println(statistics.getSum());
		System.out.println(statistics.getCount());
		System.out.println(statistics.getMax());
		
		// 计算和
		List<BigDecimal> bdList = new ArrayList<>();
		bdList.add(BigDecimal.ONE);
		bdList.add(BigDecimal.ONE);
		bdList.add(BigDecimal.TEN);
		BigDecimal result = bdList.parallelStream().reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println("result=" + result);
		
		// 计算对象中的 Money和
		Function<User, BigDecimal> totalMapper = u -> u.getMoney();
		BigDecimal afResult = list.stream()
		        .map(totalMapper)
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println("afResult=" + afResult);
		
		// 上面也可写为
		BigDecimal afResult2 = list.stream()
		        .map(User::getMoney)
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println("afResult2=" + afResult2);
		
		// 5提取对象中的字段并封装成新对象列表, 并不会打乱原list的顺序
		List<AdminUser> adminUList = list.stream().map(u -> {
			AdminUser au = new AdminUser();
			au.age = u.age;
			au.name = u.name;
			return au;
		}).collect(Collectors.toList());
		adminUList.stream().forEach(System.out::print);
		System.out.println();
		
		// 5-1提取对象中的字段并封装成新列表
		List<String> uIdList = list.stream().collect(ArrayList::new, (firstItem, nextItem) -> {
			firstItem.add(nextItem.getName());
		}, List::addAll);
		uIdList.stream().forEach(System.out::print);
		System.out.println();
		
		// 换一种方式
		List<String> uNameList = list.stream().map(User::getName).collect(Collectors.toList());
		uNameList.stream().forEach(System.out::print);
		System.out.println();
		
		// 换一种方式
		List<String> uNameParseList = list.stream().map(u -> "_P".concat(u.getName())).collect(Collectors.toList());
		uNameParseList.stream().forEach(System.out::print);
		System.out.println();
		
		// 6 list分页
		int batchSize = 3;
		int size = list.size();
		for(int i = 0; i < size / batchSize + 1; i++){
			List<User> batchModel = new ArrayList<>();
			list.stream().skip(i * batchSize).limit(batchSize).forEach(e -> batchModel.add(e));
			batchModel.stream().forEach(b -> System.out.print(b.getName()));
			System.out.println();
		}
		System.out.println("batch finish");
		
		// 7 提取Bean元素成Map
		Map<Integer, String> mapUser = list.stream().collect(Collectors.toMap(User::getAge, User::getName));
		mapUser.forEach((k,v) -> System.out.println("key:"+k+"-value:"+v));
		System.out.println();
		
		// 7.1 提取Bean元素成Map, value为本身
		Map<Integer, User> mapUserBean1 = list.stream().collect(Collectors.toMap(User::getAge, user -> user));
		mapUserBean1.forEach((k,v) -> System.out.println("key:"+k+"-value:"+v));
		System.out.println();
		
		// 7.2 提取Bean元素成Map, value为本身
		Map<Integer, User> mapUserBean2 = list.stream().collect(Collectors.toMap(User::getAge, Function.identity()));
		mapUserBean2.forEach((k,v) -> System.out.println("key:"+k+"-value:"+v));
		System.out.println();
		
		// 8 distinct
		list.stream().filter(data -> data.getAge() > 5).map(User::getAge).distinct().forEach(System.out::print);
		System.out.println();
		
		// 9 distinct, 将集合中指定属性重复的item去重
		list.stream().filter(data -> data.getAge() > 5).filter(distinctByKey(User::getAge)).forEach(System.out::print);
		System.out.println("==============9==============");
		
		// 10 基本类型的比较
		final List <Integer> listBox = IntStream.rangeClosed(1, 20).boxed().collect(Collectors.toList());
		System.out.println(listBox.stream().max(Integer::compare).get()); // 正确方式
		System.out.println(listBox.stream().max(Integer::max).get()); // error 不能这么玩
		System.out.println(listBox.stream().min(Integer::min).get()); // error 不能这么玩
		
		// 11 forEach throws
		list.stream().forEach(u -> {
			if(u.getAge() == 7) throw new RuntimeException("throws...");
			System.out.println(u);
		});
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	private static void init(List<User> list) {
		for (int i = 0; i < 10; i++) {
			int age = i;
			// test: 8&9 distinct
			if(i >= 7) {
				age = 7;
			}
			list.add(uf.create(age, "User" + i, BigDecimal.valueOf(i)));
		}
	}
}

class User {
	String name;
	Integer age;
	BigDecimal money;
	
	public User() {
		super();
	}

	public User(Integer age, String name, BigDecimal money) {
		super();
		this.name = name;
		this.age = age;
		this.money = money;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [name=").append(name).append(", age=").append(age).append("]");
		return builder.toString();
	};
}

class AdminUser {
	String name;
	Integer age;
	
	public AdminUser() {}

	public AdminUser(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdminUser [name=").append(name).append(", age=").append(age).append("]");
		return builder.toString();
	}
}
