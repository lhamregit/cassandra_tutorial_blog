package com.cass.ihr.domain;

import org.apache.cassandra.utils.avro.UUID;

import com.netflix.astyanax.annotations.Component;

//Annotated composite class
public class MasterHistoryEvent {

	@Component(ordinal = 0)
	String type;
	@Component(ordinal = 1)
	UUID timestamp;

	// Must have public default constructor
	public MasterHistoryEvent() {
	}
}