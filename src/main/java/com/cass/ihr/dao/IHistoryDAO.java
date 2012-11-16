package com.cass.ihr.dao;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import me.prettyprint.hector.api.Keyspace;

public interface IHistoryDAO {

	public void writeEvent(Integer profileId) throws IOException;

	public void writeEvent(List<Integer> profileIds, int events) throws IOException;
	
	public void readEvent(Integer profileId) throws IOException;

	public void deleteEventsHector(List<Integer> profileIds) throws IOException;

	public void setKeyspace(Keyspace keyspace);

	public void setObjectMapper(ObjectMapper objectMapper);

	public void testReadComposite(List<Integer> profileIds) throws IOException;

	public List readCompositeSlice_UUID_String(Integer profileId,
			String eventType, int rows) throws IOException;

	public void writeCompostiteColname_UUID_String(List<Integer> profileIds,
			String type, int events) throws IOException;

	public List readCompositeSlice_String_UUID(Integer profileId,
			String eventType, int rows) throws IOException;

	public void writeCompostiteColname_String_UUID(Integer profileId,
			String type) throws IOException;
	
	public void writeCompostiteColname_String_UUID(List<Integer> profileIds,
			String type, int events) throws IOException;

	public void iterateAllRowsPaged(int pageSize);
	
	public void iterateAllRowsPagedString_UUID(int pageSize) ;

}
