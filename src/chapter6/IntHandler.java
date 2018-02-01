package chapter6;
@FunctionalInterface
public interface IntHandler {
	void handle(int i); //是有且只有一个抽象方法
	boolean equals(Object obj); // 其中被Object实现的方法不是抽象方法
}
