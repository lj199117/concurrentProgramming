package chapter3;

import java.util.concurrent.locks.ReentrantLock;


public class TryLockBadTryCode implements Runnable {
	public static ReentrantLock lock1 = new ReentrantLock();
	public static ReentrantLock lock2 = new ReentrantLock();
	int lock;

	public TryLockBadTryCode(int lock) {
		this.lock = lock;
	}

	@Override
	public void run() {
		if (lock == 1) {
			while (true) {
				try {
					if (lock1.tryLock()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (lock2.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getId() + ":My Job done;");
								return;
							} finally {
								lock2.unlock();
							}
						}
					}
				} finally {
					lock1.unlock();
				}
			}
		} else {
			while (true) {
				try {
					if (lock2.tryLock()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (lock1.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getId() + ":My Job done;");
								return;
							} finally {
								lock1.unlock();
							}
						}

					}
				} finally {
					lock2.unlock();
				}
			}

		}
	}

	/**
	 * 会抛出IllegalMonitorStateException
	 *
	 *	try {
			if (lock1.tryLock()) {}
		} finally {
			lock1.unlock(); //如果没有tryLock到资源却finally去unlock所以报错
		}
	 * @param args
	 */
	public static void main(String args[]) {
		TryLockBadTryCode r1 = new TryLockBadTryCode(1);
		TryLockBadTryCode r2 = new TryLockBadTryCode(2);
		Thread thread1 = new Thread(r1);
		Thread thread2 = new Thread(r2);

		thread1.start();
		thread2.start();
	}

}
