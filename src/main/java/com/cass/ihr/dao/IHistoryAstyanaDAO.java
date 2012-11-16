package com.cass.ihr.dao;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

public interface IHistoryAstyanaDAO {

	public boolean writeEvents(List<Integer> profileIds) throws IOException;

	public void deleteEvents(List<Integer> profileIds) throws IOException;

	public boolean iterateEvents(List<Integer> profileIds) throws IOException,
			ClassNotFoundException;
	public void iterateEventsCompositeColName(List<Integer> profileIds) ;
}
