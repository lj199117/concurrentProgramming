package chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class StreamMain {
	@FunctionalInterface
	interface UserFactory<U extends User> {
		U create(int age, String name);
	}

	static UserFactory<User> uf = User::new;

	public static void main(String[] args) {
		List<User> list = new ArrayList<User>();
		init(list);
		
		Predicate<User> predicate = u -> u.age > 6;
		Predicate<User> otherPredicate = u -> u.age == 3;
		list.stream().filter(predicate.or(otherPredicate)).forEach(System.out::print);
	}

	private static void init(List<User> list) {
		for (int i = 0; i < 10; i++) {
			list.add(uf.create(i, "User" + i));
		}
	}
}

class User {
	String name;
	int age;

	public User(int age, String name) {
		super();
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [name=").append(name).append(", age=").append(age).append("]");
		return builder.toString();
	};
}
