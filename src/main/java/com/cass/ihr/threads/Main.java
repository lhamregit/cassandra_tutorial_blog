package com.cass.ihr.threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class WorkerTask implements Runnable {
	// Task name
	private String name;

	public WorkerTask(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println(name + " : Running");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		System.out.println(name + " : Done");
	}

	@Override
	public String toString() {
		return name;
	}
}

class RejectionHandler implements RejectedExecutionHandler {
	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		System.out.println(runnable.toString() + " : Rejected");
	}
}

public class Main {
	// Executor
	private static ThreadPoolExecutor executor = null;
	// Finished flag
	private static volatile boolean finished = false;
	// Report the current executor state
	private static Runnable reporter = new Runnable() {
		public void run() {
			while (!finished) {
				System.out
						.println(String
								.format("Queue: %d/%d(%d), Active: %d, Completed: %d, Queue: %d/%d, Task: %d",
										executor.getPoolSize(), executor
												.getCorePoolSize(), executor
												.getMaximumPoolSize(), executor
												.getActiveCount(), executor
												.getCompletedTaskCount(),
										executor.getQueue().size(), executor
												.getQueue().remainingCapacity()
												+ executor.getQueue().size(),
										executor.getTaskCount()));

				// Sleep - 
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
			}
		}
	};

	public static void main(String[] args) throws InterruptedException {
		int corePool = 3;
		int maxPool = 6;
		long keepAlive = 5;

		// Blocking queue
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(3);

		// Executor
		executor = new ThreadPoolExecutor(corePool, maxPool, keepAlive,
				TimeUnit.SECONDS, queue, new RejectionHandler());

		// Add some tasks
		for (int i = 0; i < 10; i++) {
			executor.execute(new WorkerTask("Task " + (i + 1)));
		}

		// Start reporter
		new Thread(reporter).start();

		// Shutdown
		executor.shutdown();

		// Wait for all tasks to finish
		while (!executor.isTerminated()) {
			Thread.sleep(100);
		}

		System.out.println("Terminated!");

		Thread.sleep(1000);
		finished = true;
	}
}
