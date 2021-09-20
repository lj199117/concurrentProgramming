package chapter6;

import java.util.Comparator;

public class ComparatorMain {
	/**
	 * 先按照字符串长度排序 -> 大小写不敏感的字母顺序排序
	 */
	static Comparator<String> cmp = Comparator.comparingInt(String::length)
			.thenComparing(String.CASE_INSENSITIVE_ORDER);
	
	public static void main(String[] args) {
		String str1 = "abcd123";
		String str2 = "abcd12345678";
		int res = cmp.compare(str1, str2);
		System.out.println(res);
	}
}
