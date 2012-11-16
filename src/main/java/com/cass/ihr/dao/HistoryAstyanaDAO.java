package com.cass.ihr.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.cass.ihr.domain.HistoryEvent;
import com.cass.ihr.domain.MasterHistoryEvent;
import com.cass.ihr.util.UUIDUtils;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.astyanax.util.RangeBuilder;
import com.netflix.astyanax.util.TimeUUIDUtils;

public class HistoryAstyanaDAO extends BaseDAO implements IHistoryAstyanaDAO {

	private static final Logger log = Logger.getLogger(HistoryAstyanaDAO.class);

	private Keyspace keyspace;

	private ConnectionPoolConfigurationImpl connectionPoolConfigurationImpl;

	private void setup() {
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forCluster("Test Cluster")
				.forKeyspace("IHR_local")
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
								.setDiscoveryType(NodeDiscoveryType.NONE))
				.withConnectionPoolConfiguration(
						connectionPoolConfigurationImpl)
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		keyspace = context.getEntity();
	}

	@Override
	public boolean writeEvents(List<Integer> profileIds) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteEvents(List<Integer> profileIds) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean iterateEvents(List<Integer> profileIds) throws IOException,
			ClassNotFoundException {
		setup();
		for (Integer profileId : profileIds) {

			ColumnFamily<String, byte[]> cFam = new ColumnFamily<String, byte[]>(
					"MyHistory", // Column Family Name
					StringSerializer.get(), // Key Serializer
					BytesArraySerializer.get()); // Column Serializer

			ColumnList<byte[]> columns;
			try {
				RowQuery<String, byte[]> query = keyspace
						.prepareQuery(cFam)
						.getKey(profileId.toString())
						.autoPaginate(true)
						.withColumnRange(
								new RangeBuilder().setLimit(10).build());

				columns = query.execute().getResult();
				Iterator it = columns.iterator();
				while (it.hasNext()) {
					Column c = (Column) it.next();
					String jsonString = (String) UUIDUtils.toObject(c
							.getByteArrayValue());
					//log.info("jsonString: " + jsonString);
					HistoryEvent e = getObjectFromJson(jsonString);

					UUID uuid = UUIDSerializer.get().fromBytes(
							c.getRawName().array());
					Date d = new Date(UUIDUtils.millisFromTimeUuid(uuid));
					e.setDate(d);
					e.setProfileId(profileId);
					log.info("Read: " + profileId + " " + e.getDate());
				}

			} catch (ConnectionException e) {
				log.error(e.getCause());
			}
		}
		return true;
	}

	public void iterateEventsCompositeColName(List<Integer> profileIds) {
		setup();
		AnnotatedCompositeSerializer<MasterHistoryEvent> eventSerializer = new AnnotatedCompositeSerializer<MasterHistoryEvent>(
				MasterHistoryEvent.class);
		ColumnFamily<String, MasterHistoryEvent> CF_SESSION_EVENTS = new ColumnFamily<String, MasterHistoryEvent>(
				"MasterHistory2", StringSerializer.get(), eventSerializer);
		for (Integer profileId : profileIds) {
			// Querying cassandra for an entire row
			try {
				OperationResult<ColumnList<MasterHistoryEvent>> result = keyspace
						.prepareQuery(CF_SESSION_EVENTS)
						.getKey(profileId.toString())
						.withColumnRange(
								eventSerializer.makeEndpoint("track",
										Equality.EQUAL).toBytes(),
								eventSerializer.makeEndpoint("track",
										Equality.EQUAL).toBytes(),
								false, 100).execute();
				Iterator it = result.getResult().iterator();
				//System.out.println("it " + it);
				while (it.hasNext()) {
					Column c = (Column) it.next();
					//System.out.println("c " + c.getTimestamp());
				}

			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void setConnectionPoolConfigurationImpl(
			ConnectionPoolConfigurationImpl connectionPoolConfigurationImpl) {

		this.connectionPoolConfigurationImpl = connectionPoolConfigurationImpl;
	}
}
