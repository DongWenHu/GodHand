package com.rzx.godhand.mark.util.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池。
 * 
 * @author E
 */
public class ThreadPool {

	private static ExecutorService pool;

	static {
		pool = Executors.newFixedThreadPool(5); // 固定线程池
	}

	public static void add(Runnable runnable) {
		pool.execute(runnable);
	}
}
