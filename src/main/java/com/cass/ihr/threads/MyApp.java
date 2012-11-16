package com.cass.ihr.threads;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cass.ihr.facade.IHistoryFacade;

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

	public MyApp() {

	}

	public MyApp(String size, String batch, String events, String type,
			String colType, String operationType, String profileId, String rows) {
		_size = Integer.parseInt(size);
		_batch = Integer.parseInt(batch);
		_events = Integer.parseInt(events);
		_type = type;
		_colType = Integer.parseInt(colType);
		_operationType = Integer.parseInt(operationType);
		_profileId = Integer.parseInt(profileId);
		_rows = Integer.parseInt(rows);
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		log.info("Initializing Spring context.");

		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml");

		log.info("_size: " + _size);
		log.info("_batch: " + _batch);
		log.info("_events: " + _events);
		log.info("_type: " + _type);
		log.info("_colType: " + _colType);
		log.info("_colType: " + _colType);
		log.info("_operationType: " + _operationType);
		log.info("_profileId: " + _profileId);
		log.info("_rows: " + _rows);

		switch (_operationType) {
		case 0:
			load();
			break;
		case 1:
			read();
			break;
		case 2:
			readAll();
			break;
		default:
			log.info("did nothing");
		}
	}

	private static void readAll() throws IOException, ClassNotFoundException {
		historyFacade.iterateAllRowsPaged(1000);
	}

	private static void read() throws IOException, ClassNotFoundException {
		List<Integer> profileIds = new ArrayList();

		for (int i = _size / 2; i < (_size / 2) + (_batch / _events); i++) {
			Integer in = new Integer(i);
			profileIds.add(in);
		}

		log.info("profileIds: " + profileIds.size());
		for (Integer profileId : profileIds) {
			switch (_colType) {
			case 0:
				historyFacade.readEvent(_profileId);
				break;
			case 1:
				historyFacade.readCompositeSlice_String_UUID(_profileId, _type,
						_rows);
				break;
			default:
				log.info("did nothing");
			}
		}
	}

	private static void load() throws IOException, ClassNotFoundException {
		List profileIds = new ArrayList();

		for (int i = 0; i < _size; i++) {
			Integer in = new Integer(i);
			profileIds.add(in);
		}

		log.info("profileIds: " + profileIds.size());
		// historyFacade.iterateAllRowsPaged(100);
		int position = 0;
		// add a bunch of regular events with just UUID as key and json as value
		for (int i = 0; i < _size / _batch; i++) {
			log.info("running batch" + i);
			try {
				Thread.sleep(0);

				// get the first batch size
				List newProf = new ArrayList();

				// loop throught the collection starting from beginning and to
				// the
				// batch size, record the last position
				for (int jj = 0; jj < _batch; jj++) {
					newProf.add(profileIds.get(position));
					position++;
				}
				switch (_colType) {
				case 0:
					historyFacade.writeEvent(newProf, _events);
					break;
				case 1:
					historyFacade.writeCompostiteColname_String_UUID(newProf,
							_type, _events);
					break;
				default:
					log.info("did nothing");
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setHistoryFacade(IHistoryFacade historyFacade) {
		this.historyFacade = historyFacade;
	}

}
