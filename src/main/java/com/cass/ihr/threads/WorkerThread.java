package com.cass.ihr.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cass.ihr.facade.IHistoryFacade;

public class WorkerThread implements Runnable {

	private static final Logger log = Logger.getLogger(WorkerThread.class);

	private IHistoryFacade historyFacade;

	private String _name;

	private int _size;

	private int _batch;

	private int _events;

	private String _type;

	private int _colType;

	private int _operationType;

	private int _profileId;

	private int _rows;
	
	private int _readSize;

	public WorkerThread(IHistoryFacade historyFacade, String name, int size,
			int batch, int events, String type, int colType, int operationType,
			int profileId, int rows, int readSize) {
		this.historyFacade = historyFacade;
		this._name = name;
		this._size = size;
		this._batch = batch;
		this._events = events;
		this._type = type;
		this._colType = colType;
		this._operationType = operationType;
		this._profileId = profileId;
		this._rows = rows;
		this._readSize = readSize;
	}

	public void run() {
		System.out.println(new Date());
		System.out.println(historyFacade);
		try {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(_name + " : Done");
	}

	private void readAll() throws IOException, ClassNotFoundException {
		historyFacade.iterateAllRowsPaged(1000);
	}

	private void read() throws IOException, ClassNotFoundException {
		List<Integer> profileIds = new ArrayList();

		for (int i = _size/2 ; i < _size/2 + _readSize; i++) {
			Integer in = new Integer(i);
			profileIds.add(in);
		}

		log.info("profileIds: " + profileIds.size());
		for (Integer profileId : profileIds) {
			switch (_colType) {
			case 0:
				historyFacade.readEvent(profileId);
				break;
			case 1:
				historyFacade.readCompositeSlice_String_UUID(profileId, _type,
						_rows);
				break;
			default:
				log.info("did nothing");
			}
		}
	}

	private void load() throws IOException, ClassNotFoundException {
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
