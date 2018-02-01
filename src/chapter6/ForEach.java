package chapter6;

import java.util.Arrays;

public class ForEach {
	static int[] arr = {1,2,3,4,5,6,7,8};
	public static void main(String[] args) {
		// 函数式编程不影响原对象的状态,也就是拒绝修改
		Arrays.stream(arr).map(x -> x+1).forEach(System.out::print);
		System.out.println();
		Arrays.stream(arr).forEach(System.out::print);
		System.out.println();
		
		out(arr);
		System.out.println();
		// out的同理写法
		Arrays.stream(arr).map(x -> x%2 == 0 ? x : x+1).forEach(System.out::print);
	}
	
	// 奇数则加一
	public static void out(int[] arr){
		int[] tempArr = Arrays.copyOf(arr, arr.length); // 避免因引用造成的arr数据更改
		for (int i = 0; i < tempArr.length; i++) {
			if(tempArr[i] % 2 != 0){
				tempArr[i] ++;
			}
			System.out.print(tempArr[i]);
		}
	}
}
