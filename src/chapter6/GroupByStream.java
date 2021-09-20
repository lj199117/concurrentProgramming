package chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupByStream {
	public static void main(String[] args) {
		List<Student> students = new ArrayList<>();
		students.add(new Student("张三", "男", 18));
		students.add(new Student("李四", "男", 20));
		students.add(new Student("韩梅梅", "女", 18));
		students.add(new Student("小红", "女", 45));
		// 这组学生中，按性别分组后每个学生的年龄
		Map<String, List<Integer>> sex2Age = students.parallelStream().collect(
				Collectors.groupingBy(Student::getSex, Collectors.mapping(Student::getAge, Collectors.toList())));
		System.out.println(sex2Age);
	}
}
