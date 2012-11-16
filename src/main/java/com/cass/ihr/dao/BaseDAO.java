package com.cass.ihr.dao;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.cass.ihr.domain.HistoryEvent;

public class BaseDAO {

	private static final Logger log = Logger.getLogger(BaseDAO.class);

	protected ObjectMapper objectMapper;

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	protected String getJsonAsString(HistoryEvent event) {

		StringWriter writer = new StringWriter();
		try {
			// /objectMapper.getSerializationConfig().setSerializationInclusion(
			// JsonSerialize.Inclusion.NON_NULL);
			objectMapper.writeValue(writer, event);
		} catch (Exception e) {
			log.error("Unable to serialize event to json:" + event.toString());
		}
		return writer.toString();
	}

	protected HistoryEvent getObjectFromJson(String jsonString) {

		HistoryEvent obj = null;

		try {
			// objectMapper.getSerializationConfig().setSerializationInclusion(
			// JsonSerialize.Inclusion.NON_NULL);
			obj = (HistoryEvent) objectMapper.readValue(jsonString,
					HistoryEvent.class);

		} catch (JsonParseException e) {
			log.error("Unable to map json string to class:" + jsonString);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			log.error("Unable to map json string to class:" + jsonString);
		} catch (IOException e) {
			log.error("Unable to map json string to class:" + jsonString);
		}

		return obj;
	}
}
