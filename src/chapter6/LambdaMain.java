package chapter6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class LambdaMain {
	@FunctionalInterface
	interface UserFactory<U extends User> {
		U create(int age, String name, BigDecimal money);
	}
	static UserFactory<User> uf = User::new;
	
	public static void main(String[] args) {
		List<User> list = new ArrayList<User>();
		init(list);
		List<AdminUser> adminUserList = new ArrayList<AdminUser>();
		initAdminUser(adminUserList);
		
		// 1. 把User & AdminUser中 age=5的年龄改为50
		List<User> addAgeUsers = doFunction(list, users -> convertToList(users, User::getAge, age -> age == 5, u -> u.setAge(50)));
		System.err.println(addAgeUsers);
	}	
	
	private static <T> List<T> doFunction(Collection<T> values, Function<Collection<T>, List<T>> fun) {
		return fun.apply(values);
	}
	
	private static <T> List<T> convertToList(Collection<T> values, Function<T, Integer> ageMapper, Predicate<Integer> predict, Consumer<T> ageAdd) {
		List<T> result = new ArrayList<>(values.size());
		for (T model : values) {
			if(predict.test(ageMapper.apply(model))) {
				ageAdd.accept(model);
			}
			result.add(model);
		}
		return result;
	}

	private static void init(List<User> list) {
		for (int i = 0; i < 10; i++) {
			int age = i;
			list.add(uf.create(age, "User" + i, BigDecimal.valueOf(i)));
		}
	}
	
	private static void initAdminUser(List<AdminUser> adminUserList) {
		for (int i = 0; i < 10; i++) {
			adminUserList.add(new AdminUser("admin:" + i, i));
		}
	}
}
