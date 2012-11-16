package com.cass.ihr;

import java.io.IOException;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.cass.ihr.facade.IHistoryFacade;
import com.cass.ihr.threads.WorkerThread;

public class MyApp {

	private static final Logger log = Logger.getLogger(MyApp.class);

	private static IHistoryFacade historyFacade;

	private static int _size;

	private static int _batch;

	private static int _events;

	private static String _type;

	private static int _colType;

	private static int _operationType;

	private static int _profileId;

	private static int _rows;

	private static int _threads;
	
	private static int _readSize;

	public MyApp() {

	}

	public MyApp(String size, String batch, String events, String type,
			String colType, String operationType, String profileId,
			String rows, String threads, String readSize) {
		_size = Integer.parseInt(size);
		_batch = Integer.parseInt(batch);
		_events = Integer.parseInt(events);
		_type = type;
		_colType = Integer.parseInt(colType);
		_operationType = Integer.parseInt(operationType);
		_profileId = Integer.parseInt(profileId);
		_rows = Integer.parseInt(rows);
		_threads = Integer.parseInt(threads);
		_readSize = Integer.parseInt(readSize);
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {

		log.info("Initializing Spring context.");

		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml");

		log.info("Initializing Spring context DONE.");

		TaskExecutor tE = (TaskExecutor) applicationContext
				.getBean("taskExecutor");

		// Add some tasks
		for (int i = 0; i < _threads; i++) {
			tE.execute(new WorkerThread(historyFacade, "Task_" + i, _size,
					_batch, _events, _type, _colType, _operationType,
					_profileId, _rows, _readSize));
		}

		log.info("done creating the tasks");

	}

	public void setHistoryFacade(IHistoryFacade historyFacade) {
		this.historyFacade = historyFacade;
	}

}
