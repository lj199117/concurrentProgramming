package chapter6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
		IntSummaryStatistics statistics = list.parallelStream().collect(Collectors.summarizingInt(User::getAge));
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
	}

	private static void init(List<User> list) {
		for (int i = 0; i < 10; i++) {
			list.add(uf.create(i, "User" + i, BigDecimal.valueOf(i)));
		}
	}
}

class User {
	String name;
	int age;
	BigDecimal money;

	public User(int age, String name, BigDecimal money) {
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

	public void setAge(int age) {
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
