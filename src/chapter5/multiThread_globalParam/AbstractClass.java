package chapter5.multiThread_globalParam;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractClass {
	int globalVal = 0;
	AtomicInteger globalAtomicVal = new AtomicInteger(0);
	
	public void print(int name) {
		globalVal ++; // 模拟多线程对Service的全局变量进行修改 (肯定有线程安全问题)
		int v = globalAtomicVal.incrementAndGet(); //模拟多线程对Service的全局原子变量更改
		System.out.println(Thread.currentThread().getName()+":name="+name+",globalVal="+globalVal+",globalAtomicVal="+v);
	}
}
