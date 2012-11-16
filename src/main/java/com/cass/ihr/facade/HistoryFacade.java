package com.cass.ihr.facade;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.cass.ihr.dao.IHistoryAstyanaDAO;
import com.cass.ihr.dao.IHistoryDAO;

public class HistoryFacade implements IHistoryFacade {
	private static final Logger log = Logger.getLogger(HistoryFacade.class);

	private IHistoryDAO historyDAO;

	private IHistoryAstyanaDAO historyAstyanaDAO;

	@Override
	public void deleteEvents(List<Integer> profileIds) throws IOException {
		historyDAO.deleteEventsHector(profileIds);
	}

	@Override
	public void readEvent(Integer profileId) throws IOException {
		historyDAO.readEvent(profileId);
	}

	@Override
	public void writeEvent(Integer profileId) throws IOException {
		historyDAO.writeEvent(profileId);
	}

	@Override
	public void writeEvent(List<Integer> profileIds, int events)
			throws IOException {
		historyDAO.writeEvent(profileIds, events);
	}

	@Override
	public void iterateEventsCompositeColName(List<Integer> profileIds) {
		historyAstyanaDAO.iterateEventsCompositeColName(profileIds);
	}

	public void testReadComposite(List<Integer> profileIds) throws IOException {
		historyDAO.testReadComposite(profileIds);
	}

	@Override
	public List readCompositeSlice_String_UUID(Integer profileId,
			String eventType, int rows) throws IOException {
		return historyDAO.readCompositeSlice_String_UUID(profileId, eventType,
				rows);
	}

	@Override
	public void writeCompostiteColname_String_UUID(Integer profileId,
			String type) throws IOException {
		historyDAO.writeCompostiteColname_String_UUID(profileId, type);
	}

	@Override
	public void writeCompostiteColname_String_UUID(List<Integer> profileIds,
			String type, int events) throws IOException {
		historyDAO.writeCompostiteColname_String_UUID(profileIds, type, events);
	}

	// Write the data in the order of the UUID (time) and String (type)
	@Override
	public void writeCompostiteColname_UUID_String(List<Integer> profileIds,
			String type, int events) throws IOException {
		historyDAO.writeCompostiteColname_UUID_String(profileIds, type, events);
	}

	// read the data in the order of the UUID (time) and String (type)
	@Override
	public List readCompositeSlice_UUID_String(Integer profileId,
			String eventType, int rows) throws IOException {
		return historyDAO.readCompositeSlice_UUID_String(profileId, eventType,
				rows);
	}

	@Override
	public void iterateAllRowsPaged(int pageSize) {
		historyDAO.iterateAllRowsPaged(pageSize);
	}

	@Override
	public void iterateAllRowsPagedString_UUID(int pageSize) {
		historyDAO.iterateAllRowsPagedString_UUID(pageSize);
	}

	public void setHistoryDAO(IHistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public void setHistoryAstyanaDAO(IHistoryAstyanaDAO historyAstyanaDAO) {
		this.historyAstyanaDAO = historyAstyanaDAO;
	}

}
