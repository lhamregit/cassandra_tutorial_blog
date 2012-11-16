package com.cass.ihr.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.metamodel.EntityType;

import me.prettyprint.cassandra.model.HColumnImpl;
import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.AbstractComposite.Component;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.beans.DynamicComposite;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mortbay.log.Log;

import com.cass.ihr.domain.HistoryEvent;
import com.cass.ihr.util.UUIDUtils;

public class HistoryDAO extends BaseDAO implements IHistoryDAO {

	private static final Logger log = Logger.getLogger(HistoryDAO.class);

	private String historyColFam = "MyHistory";

	private String MasterHistoryString = "MasterHistoryString";

	private String MasterHistoryUUID = "MasterHistoryUUID";

	private Keyspace keyspace;

	@Override
	public void writeEvent(Integer profileId) throws IOException {
		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		HistoryEvent event = new HistoryEvent(1002,
				"this is a history event for " + profileId);
		String eventJson = getJsonAsString(event);
		// log.info("eventJson: " + eventJson);

		UUID timeListened = UUIDUtils.nonUniqueTimeUuidForDate(System
				.currentTimeMillis());

		HColumn<UUID, byte[]> column = HFactory.createColumn(timeListened,
				UUIDUtils.getbytes(eventJson), UUIDSerializer.get(),
				BytesArraySerializer.get());
		column.setTtl(60 * 60 * 24 * 90);

		mutator.addInsertion(profileId.toString(), historyColFam, column);
		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		log.info("Write took: " + (System.currentTimeMillis() - start));
	}

	@Override
	public void writeEvent(List<Integer> profileIds, int events)
			throws IOException {
		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		for (Integer profileId : profileIds) {
			for (int i = 0; i < events; i++) {
				try {
					Thread.sleep(0);

					HistoryEvent event = new HistoryEvent(i,
							"this is a history event for " + profileId);
					String eventJson = getJsonAsString(event);
					//log.info("eventJson: " + eventJson);

					UUID timeListened = UUIDUtils
							.nonUniqueTimeUuidForDate(System
									.currentTimeMillis() + i);

					HColumn<UUID, byte[]> column = HFactory.createColumn(
							timeListened, UUIDUtils.getbytes(eventJson),
							UUIDSerializer.get(), BytesArraySerializer.get());
					column.setTtl(60 * 60 * 24 * 90);

					mutator.addInsertion(profileId.toString(), historyColFam,
							column);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		log.info("Write took: " + (System.currentTimeMillis() - start) + " on "
				+ result.getHostUsed());
	}

	// read the events reversed (latest entry first)
	@Override
	public void readEvent(Integer profileId) throws IOException {
		long start = System.currentTimeMillis();
		try {
			SliceQuery<String, UUID, byte[]> q = HFactory.createSliceQuery(
					keyspace, StringSerializer.get(), UUIDSerializer.get(),
					BytesArraySerializer.get());
			q.setColumnFamily(historyColFam);
			q.setKey(profileId.toString());

			ColumnSliceIterator<String, UUID, byte[]> iterator = new ColumnSliceIterator<String, UUID, byte[]>(
					q, UUIDUtils.nonUniqueTimeUuidForDate(System
							.currentTimeMillis()),
					UUIDUtils.nonUniqueTimeUuidForDate(0), true);
			
			while (iterator.hasNext()) {
				HColumn<UUID, byte[]> col = iterator.next();
				UUID u = col.getName();
				Date d = new Date(UUIDUtils.millisFromTimeUuid(u));
				String jsonString;
				jsonString = (String) UUIDUtils.toObject(col.getValue());
				//log.info("jsonString: " + jsonString + " " + d);
				HistoryEvent e = getObjectFromJson(jsonString);
				e.setDate(d);
				e.setProfileId(profileId);
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("Read time: " + profileId + " "
				+ (System.currentTimeMillis() - start));
	}

	public void deleteEventsHector(List<Integer> profileIds) throws IOException {

		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		for (Integer profileId : profileIds) {
			mutator.addDeletion(profileId.toString(), historyColFam);
		}
		MutationResult result = mutator.execute();
		log.info("Delete took: " + result.getExecutionTimeMicro()
				+ ". Batch size: " + profileIds.size());
		mutator.discardPendingMutations();
	}

	public boolean iterateCompositeEventsHector(List<Integer> profileIds)
			throws IOException, ClassNotFoundException {

		boolean success = false;
		try {

			for (Integer profileId : profileIds) {

				SliceQuery<String, UUID, byte[]> q = HFactory.createSliceQuery(
						keyspace, StringSerializer.get(), UUIDSerializer.get(),
						BytesArraySerializer.get());
				q.setColumnFamily(historyColFam);
				q.setKey(profileId.toString());
				long start = System.currentTimeMillis();
				ColumnSliceIterator<String, UUID, byte[]> iterator = new ColumnSliceIterator<String, UUID, byte[]>(
						q, UUIDUtils.nonUniqueTimeUuidForDate(0),
						UUIDUtils.nonUniqueTimeUuidForDate(System
								.currentTimeMillis()), false);

				while (iterator.hasNext()) {
					success = true;
					HColumn<UUID, byte[]> col = iterator.next();
					UUID u = col.getName();
					Date d = new Date(UUIDUtils.millisFromTimeUuid(u));

					String jsonString = (String) UUIDUtils.toObject(col
							.getValue());
					//log.info("jsonString: " + jsonString);

					HistoryEvent e = getObjectFromJson(jsonString);
					e.setDate(d);
					e.setProfileId(profileId);

					log.info("Read: " + profileId + " " + d);
				}
				log.info("Read time: " + profileId + " "
						+ (System.currentTimeMillis() - start));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	/*
	 * Would never use this, just left for testing - Remove from the official
	 * version
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.cass.ihr.dao.IHistoryDAO#writeCompostiteEvents(java.util.List)
	 */
	public void writeCompostiteKeyEvents(List<Integer> profileIds)
			throws IOException {

		Mutator<Composite> mutator = HFactory.createMutator(keyspace,
				CompositeSerializer.get());
		for (Integer profileId : profileIds) {

			HistoryEvent event = new HistoryEvent(1002,
					"this is a history event");
			String eventJson = getJsonAsString(event);
			log.info("eventJson: " + eventJson);

			UUID timeListened = UUIDUtils.nonUniqueTimeUuidForDate(System
					.currentTimeMillis());

			HColumn<UUID, byte[]> column = HFactory.createColumn(timeListened,
					UUIDUtils.getbytes(eventJson), UUIDSerializer.get(),
					BytesArraySerializer.get());
			column.setTtl(60 * 60 * 24 * 90);

			Composite rowKey = new Composite();
			rowKey.add(0, timeListened);
			rowKey.add(1, profileId.toString());

			mutator.insert(rowKey, MasterHistoryString, column);
		}
		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		log.info("Write took: " + (System.currentTimeMillis() - start)
				+ ". Batch size: " + profileIds.size());
	}

	public void writeCompostiteColname_String_UUID(Integer profileId,
			String type) throws IOException {

		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());

		HistoryEvent event = new HistoryEvent(1002, "this is a history event");
		String eventJson = getJsonAsString(event);
		// log.info("eventJson: " + eventJson);

		UUID timeListened = UUIDUtils.nonUniqueTimeUuidForDate(System
				.currentTimeMillis());

		Composite colname = new Composite();
		colname.addComponent(type, StringSerializer.get());
		colname.addComponent(timeListened, UUIDSerializer.get());

		HColumn<Composite, byte[]> column = HFactory.createColumn(colname,
				UUIDUtils.getbytes(eventJson), CompositeSerializer.get(),
				BytesArraySerializer.get());
		column.setTtl(60 * 60 * 24 * 90);

		mutator.addInsertion(profileId.toString(), MasterHistoryString, column);

		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		// log.info("Write took: " + (System.currentTimeMillis() - start));
	}

	public void writeCompostiteColname_String_UUID(List<Integer> profileIds,
			String type, int events) throws IOException {

		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		for (Integer profileId : profileIds) {

			for (int i = 0; i < events; i++) {
				try {
					Thread.sleep(0);

					HistoryEvent event = new HistoryEvent(i,
							"this is a history event ");
					String eventJson = getJsonAsString(event);
					// log.info("eventJson: " + eventJson);
					long time = System.currentTimeMillis();
					long newTime = time + (i*1000);
					//System.out.println("time    = " + time);
					//System.out.println("newTime = " +newTime);

					UUID timeListened = UUIDUtils
							.nonUniqueTimeUuidForDate(newTime);

					Composite colname = new Composite();
					colname.addComponent(type, StringSerializer.get());
					colname.addComponent(timeListened, UUIDSerializer.get());

					HColumn<Composite, byte[]> column = HFactory.createColumn(
							colname, UUIDUtils.getbytes(eventJson),
							CompositeSerializer.get(),
							BytesArraySerializer.get());
					column.setTtl(60 * 60 * 24 * 90);

					mutator.addInsertion(profileId.toString(),
							MasterHistoryString, column);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		log.info("Write took: " + (System.currentTimeMillis() - start));
	}

	public void writeCompostiteColname_UUID_String(List<Integer> profileIds,
			String type, int events) throws IOException {

		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		for (Integer profileId : profileIds) {
			for (int i = 0; i < events; i++) {
				HistoryEvent event = new HistoryEvent(1002,
						"this is a history event");
				String eventJson = getJsonAsString(event);
				log.info("eventJson: " + eventJson);

				UUID timeListened = UUIDUtils.nonUniqueTimeUuidForDate(System
						.currentTimeMillis());

				Composite colname = new Composite();
				colname.addComponent(timeListened, UUIDSerializer.get());
				colname.addComponent(type, StringSerializer.get());

				HColumn<Composite, byte[]> column = HFactory.createColumn(
						colname, UUIDUtils.getbytes(eventJson),
						CompositeSerializer.get(), BytesArraySerializer.get());
				column.setTtl(60 * 60 * 24 * 90);

				mutator.addInsertion(profileId.toString(), MasterHistoryUUID,
						column);
			}
		}
		long start = System.currentTimeMillis();
		MutationResult result = mutator.execute();
		log.info("Write took: " + (System.currentTimeMillis() - start)
				+ ". Batch size: " + profileIds.size());
	}

	public List readCompositeSlice_UUID_String(Integer profileId,
			String eventType, int rows) throws IOException {
		List result = new ArrayList();

		String startString;
		String endString;

		if (eventType == null) {
			startString = Character.toString(Character.MAX_VALUE);
			endString = Character.toString(Character.MIN_VALUE);
		} else {
			startString = eventType;
			endString = eventType;
		}

		SliceQuery<String, Composite, byte[]> query = HFactory
				.createSliceQuery(keyspace, StringSerializer.get(),
						CompositeSerializer.get(), BytesArraySerializer.get());
		query.setColumnFamily(MasterHistoryUUID);
		query.setKey(profileId.toString());

		Composite start = new Composite();
		start.addComponent(
				UUIDUtils.nonUniqueTimeUuidForDate(System.currentTimeMillis()),
				UUIDSerializer.get());
		start.addComponent(1, eventType, StringSerializer.get(), "UTF8Type",
				AbstractComposite.ComponentEquality.EQUAL);
		// start.addComponent(startString, StringSerializer.get());
		// start.addComponent(0, "bob", Composite.ComponentEquality.EQUAL);

		Composite finish = new Composite();
		finish.addComponent(UUIDUtils.nonUniqueTimeUuidForDate(0),
				UUIDSerializer.get());
		// finish.addComponent(endString, StringSerializer.get());
		finish.addComponent(1, eventType, StringSerializer.get(), "UTF8Type",
				AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);

		query.setRange(start, finish, true, rows);

		// Now search.
		ColumnSlice<Composite, byte[]> columnSlice = query.execute().get();

		List columns = columnSlice.getColumns();

		Iterator colIter = columns.iterator();

		while (colIter.hasNext()) {
			try {
				HColumn<Composite, byte[]> col = (HColumn<Composite, byte[]>) colIter
						.next();

				Composite name = col.getName();
				Component cType = name.getComponent(1);

				String type = (String) cType.getValue(StringSerializer.get());

				Component cTime = name.getComponent(0);

				UUID uuid = UUIDSerializer.get().fromByteBuffer(
						cTime.getBytes());

				Date d = new Date(UUIDUtils.millisFromTimeUuid(uuid));

				String jsonString;
				jsonString = (String) UUIDUtils.toObject(col.getValue());
				log.info("type: " + type + "jsonString: " + jsonString
						+ " date : " + d);

				HistoryEvent e = getObjectFromJson(jsonString);
				e.setDate(d);
				e.setProfileId(profileId);
				result.add(e);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return result;
	}

	public void testReadComposite(List<Integer> profileIds) throws IOException {
		boolean success = false;

		for (Integer profileId : profileIds) {

			RangeSlicesQuery<String, Composite, byte[]> query = HFactory
					.createRangeSlicesQuery(keyspace, StringSerializer.get(),
							CompositeSerializer.get(),
							BytesArraySerializer.get());
			query.setKeys(profileId.toString(), profileId.toString());
			query.setColumnFamily(MasterHistoryString);

			Composite start = new Composite();
			start.addComponent("show", StringSerializer.get());
			start.addComponent(UUIDUtils.nonUniqueTimeUuidForDate(System
					.currentTimeMillis()), UUIDSerializer.get());

			Composite finish = new Composite();
			finish.addComponent("show", StringSerializer.get());
			finish.addComponent(UUIDUtils.nonUniqueTimeUuidForDate(0),
					UUIDSerializer.get());
			query.setRange(start, finish, true, 100);
			// query.setRange(null, null, true, 100);

			QueryResult<OrderedRows<String, Composite, byte[]>> result = query
					.execute();
			List<Row<String, Composite, byte[]>> rows = result.get().getList();

			for (Row row : rows) {
				ColumnSlice cs = row.getColumnSlice();
				List columns = cs.getColumns();

				Iterator it = columns.iterator();
				while (it.hasNext()) {
					try {
						HColumn<Composite, byte[]> hcolumn = (HColumn<Composite, byte[]>) it
								.next();

						Composite name = hcolumn.getName();

						Component cType = name.getComponent(0);

						String type = (String) cType.getValue(StringSerializer
								.get());
						log.info("type: " + type);

						Component cTime = name.getComponent(1);

						UUID uuid = UUIDSerializer.get().fromByteBuffer(
								cTime.getBytes());

						log.info("uuid: " + uuid);
						Date d = new Date(UUIDUtils.millisFromTimeUuid(uuid));
						log.info("d: " + d);

						String jsonString;

						jsonString = (String) UUIDUtils.toObject(hcolumn
								.getValue());

						//log.info("jsonString: " + jsonString);

						HistoryEvent e = getObjectFromJson(jsonString);
						e.setDate(d);
						e.setProfileId(profileId);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void testIndexedRead(Integer profileId) throws IOException {
		boolean success = false;

		IndexedSlicesQuery<String, UUID, byte[]> indexedSlicesQuery = new IndexedSlicesQuery<String, UUID, byte[]>(
				keyspace, StringSerializer.get(), UUIDSerializer.get(),
				BytesArraySerializer.get());

	}

	public List readCompositeSlice_String_UUID(Integer profileId,
			String eventType, int rows) throws IOException {

		List result = new ArrayList();
		String startString;
		String endString;

		SliceQuery<String, Composite, byte[]> query = HFactory
				.createSliceQuery(keyspace, StringSerializer.get(),
						CompositeSerializer.get(), BytesArraySerializer.get());
		query.setColumnFamily(MasterHistoryString);
		query.setKey(profileId.toString());

		Composite start = new Composite();
		if (eventType == null || eventType.equals("")) {
			startString = Character.toString(Character.MAX_VALUE);
			endString = Character.toString(Character.MIN_VALUE);
		} else {
			startString = eventType;
			endString = eventType;
		}
		start.addComponent(startString, StringSerializer.get());
		start.addComponent(
				UUIDUtils.nonUniqueTimeUuidForDate(System.currentTimeMillis()),
				UUIDSerializer.get());

		// start.addComponent(UUIDUtils.nonUniqueTimeUuidForDate(0),
		// UUIDSerializer.get());

		Composite finish = new Composite();
		finish.addComponent(endString, StringSerializer.get());
		finish.addComponent(UUIDUtils.nonUniqueTimeUuidForDate(0),
				UUIDSerializer.get());

		// finish.addComponent(
		// UUIDUtils.nonUniqueTimeUuidForDate(System.currentTimeMillis()),
		// UUIDSerializer.get());

		query.setRange(start, finish, true, rows);
		long startTime = System.currentTimeMillis();
		// Now search.
		ColumnSlice<Composite, byte[]> columnSlice = query.execute().get();

		List columns = columnSlice.getColumns();

		Iterator colIter = columns.iterator();

		while (colIter.hasNext()) {
			try {
				HColumn<Composite, byte[]> col = (HColumn<Composite, byte[]>) colIter
						.next();

				Composite name = col.getName();
				Component cType = name.getComponent(0);
				String type = (String) cType.getValue(StringSerializer.get());

				Component cTime = name.getComponent(1);

				UUID uuid = UUIDSerializer.get().fromByteBuffer(
						cTime.getBytes());

				Date d = new Date(UUIDUtils.millisFromTimeUuid(uuid));

				String jsonString;
				jsonString = (String) UUIDUtils.toObject(col.getValue());
				//log.info("type: " + type + "jsonString: " + jsonString
				//		+ " date : " + d);
				HistoryEvent e = getObjectFromJson(jsonString);
				e.setDate(d);
				e.setProfileId(profileId);
				result.add(e);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		log.info("Read took: " + (endTime - startTime) +" for "+profileId);
		log.info("# of results: " + result.size());
		return result;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}

	@Override
	public void iterateAllRowsPaged(int pageSize) {

		RangeSlicesQuery<String, UUID, byte[]> query = HFactory
				.createRangeSlicesQuery(keyspace, StringSerializer.get(),
						UUIDSerializer.get(), BytesArraySerializer.get());
		query.setColumnFamily(historyColFam);
		query.setRange(null, null, false, pageSize);
		String lastRowKey = "";
		String iterationLastRowKey = null;
		int counter = 0;
		while (true) {
			query.setKeys(lastRowKey, "");
			query.setRowCount(pageSize + 1);
			QueryResult<OrderedRows<String, UUID, byte[]>> result = query
					.execute();
			OrderedRows<String, UUID, byte[]> ores = result.get();
			Log.info("result ms: " + result.getExecutionTimeMicro() / 1000);

			Row<String, UUID, byte[]> lastRow = ores.peekLast();
			Iterator it = ores.iterator();

			if (lastRowKey.equals(lastRow.getKey())) {
				log.info("done, we reached the last rows");
				break;
			} else {

				while (it.hasNext()) {
					Row<String, UUID, byte[]> r = (Row) it.next();
					String key = r.getKey();
					if (!key.equals(lastRowKey)) {
						log.info("row key " + key);
						counter++;
					}
				}
				if (lastRow != null) {
					log.info("lastRow: " + lastRow.getKey());
					lastRowKey = lastRow.getKey();
				}
			}
		}
		log.info("counter: " + counter);
	}

	public void iterateAllRowsPagedString_UUID(int pageSize) {

		RangeSlicesQuery<String, Composite, byte[]> query = HFactory
				.createRangeSlicesQuery(keyspace, StringSerializer.get(),
						CompositeSerializer.get(), BytesArraySerializer.get());
		query.setColumnFamily(MasterHistoryString);
		query.setRange(null, null, false, pageSize);
		String lastRowKey = "";
		String iterationLastRowKey = null;
		int counter = 0;
		while (true) {
			query.setKeys(lastRowKey, "");
			query.setRowCount(pageSize + 1);
			QueryResult<OrderedRows<String, Composite, byte[]>> result = query
					.execute();
			OrderedRows<String, Composite, byte[]> ores = result.get();
			Log.info("result ms: " + result.getExecutionTimeMicro() / 1000);

			Row<String, Composite, byte[]> lastRow = ores.peekLast();
			Iterator it = ores.iterator();
			log.info("lastRowKey: " + lastRowKey);
			if (lastRow != null && lastRowKey.equals(lastRow.getKey())) {
				log.info("done, we reached the last rows");
				break;
			} else {

				while (it.hasNext()) {
					Row<String, UUID, byte[]> r = (Row) it.next();
					String key = r.getKey();
					if (!key.equals(lastRowKey)) {
						log.info("row key " + key);
						counter++;
					}
				}
				if (lastRow != null) {
					log.info("lastRow: " + lastRow.getKey());
					lastRowKey = lastRow.getKey();
				}
			}
		}
		log.info("counter: " + counter);
	}

}
