package chapter3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 问题：假设有三个线程，一个主线程mainThread和两个子线程subThread1、subThread2，
 * 要求按顺序输出，mainThread先输出1——3，然后subThread1输出4——6，最后subThread2输出7——9，循环执行。
 * 
 * 问题分析：有三个线程要顺序执行，所以我们可以定义三个Condition分别表示这三个线程，
 * 然后定义一个判断标记flag，flag==0表示mainThread、flag==1表示subThread1、flag==2表示subThread2。
 */
public class ReenterLockCondition2 {
	private static int count = 1;

	private Lock lock = new ReentrantLock();
	private Condition mainCondition = lock.newCondition();
	private Condition subCondition1 = lock.newCondition();
	private Condition subCondition2 = lock.newCondition();

	/*
	 * flag = 0代表mainCondition 
	 * flag = 1代表subCondition1 
	 * flag = 2代表subCondition2
	 */
	private int flag = 0;

	/**
	 * mainThread线程调的方法
	 */
	public void mainThread() {
		lock.lock();
		try {
			while (flag != 0) {
				mainCondition.await(); // 如果flag != 0，mainThread线程等待
			}

			// 如果flag == 0，mainThread线程往下执行
			System.out.println("第" + count++ + "次输出");
			for (int i = 1; i <= 3; i++) {
				System.out.println("mainThread : " + i);
			}
			System.out.println();

			// 修改flag = 1，并唤醒subCondition1执行
			flag = 1;
			subCondition1.signal();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * subThread1线程调的方法
	 */
	public void subThread1() {
		lock.lock();
		try {
			while (flag != 1) {
				subCondition1.await();
			}

			for (int i = 4; i <= 6; i++) {
				System.out.println("subCondition1 : " + i);
			}
			System.out.println();

			flag = 2;
			subCondition2.signal();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
	
	public void subThread2() {
		lock.lock();
		try {
			while (flag != 2) {
				subCondition2.await();
			}

			for (int i = 7; i <= 9; i++) {
				System.out.println("subCondition2 : " + i);
			}
			System.out.println();

			flag = 0;
			mainCondition.signal();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	public static void main(String args[]) throws InterruptedException {
		final int COUNT = 3;
		ReenterLockCondition2 cd = new ReenterLockCondition2();
		
		for (int i=1; i<=COUNT; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					cd.mainThread();	// mainThread线程
				}	
			}).start();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					cd.subThread1();	// subThread1线程
				}
			}).start();
 
			new Thread(new Runnable() {
				@Override
				public void run() {
					cd.subThread2();	// subThread2线程
				}
			}).start();
		}
	}
}