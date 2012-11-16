package com.cass.ihr.facade;

import java.io.IOException;
import java.util.List;

public interface IHistoryFacade {

	public void deleteEvents(List<Integer> profileIds) throws IOException;

	public void readEvent(Integer profileId) throws IOException;
	
	public void writeEvent(Integer profileId) throws IOException;
	
	public void writeEvent(List<Integer> profileIds, int events) throws IOException;
	
	public void iterateEventsCompositeColName(List<Integer> profileIds);

	public void testReadComposite(List<Integer> profileIds) throws IOException;

	// These calls are used to read/write the data String and UUID
	public List readCompositeSlice_String_UUID(Integer profileId,
			String eventType, int rows) throws IOException;

	public void writeCompostiteColname_String_UUID(Integer profileId,
			String type) throws IOException;
	
	public void writeCompostiteColname_String_UUID(List<Integer> profileIds,
			String type, int events) throws IOException;

	// These calls are used to read/write the data UUID and String
	public void writeCompostiteColname_UUID_String(List<Integer> profileIds,
			String type, int events) throws IOException;

	public List readCompositeSlice_UUID_String(Integer profileId,
			String eventType, int rows) throws IOException;

	public void iterateAllRowsPaged(int pageSize);
	
	public void iterateAllRowsPagedString_UUID(int pageSize) ;

}
