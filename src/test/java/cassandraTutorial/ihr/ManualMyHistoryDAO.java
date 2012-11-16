package cassandraTutorial.ihr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.log4j.Logger;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.CassandraUnit;
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cass.ihr.MyApp;
import com.cass.ihr.dao.IHistoryDAO;

public class ManualMyHistoryDAO extends DAOTest {

	private static final Logger log = Logger
			.getLogger(ManualMyHistoryDAO.class);

	@Autowired
	private IHistoryDAO historyDAO;

	@Autowired
	private Keyspace keyspace;

	/*
	 * Test iterating through the inital list created from startup - should be
	 * fixed return values
	 */
	@Test
	public void iterateEventsTest() throws Exception {
		assertThat(getKeySpace(), notNullValue());
		assertThat(getKeySpace().getKeyspaceName(), is("IHR_local"));
		/* and query all what you want */

		Integer prId1 = new Integer(101);

	}

	/*
	 * Delete all the data and ensure that we cannot retrive anything from the
	 * test ids
	 */
	@Test
	public void deleteEventsTest() throws Exception {
		assertThat(getKeySpace(), notNullValue());
		assertThat(getKeySpace().getKeyspaceName(), is("IHR_local"));
		/* and query all what you want */
		List profileIds = new ArrayList();

		Integer prId1 = new Integer(101);
		Integer prId2 = new Integer(102);

		profileIds.add(prId1);
		profileIds.add(prId2);

		historyDAO.deleteEventsHector(profileIds);
	}

	/*
	 * Insert data again and make sure we can find the test data
	 */
	@Test
	public void writeEventsTest() throws Exception {
		assertThat(getKeySpace(), notNullValue());
		assertThat(getKeySpace().getKeyspaceName(), is("IHR_local"));
		/* and query all what you want */
		List profileIds = new ArrayList();

		Integer prId1 = new Integer(101);
		Integer prId2 = new Integer(102);

		profileIds.add(prId1);
		profileIds.add(prId2);
		//assertTrue(historyDAO.writeEventsHector(profileIds));

	}

	public void setHistoryDAO(IHistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	private Keyspace getKeySpace() {
		System.out.println(keyspace);
		return this.keyspace;
	}

	/*
	 * Test iterating through the inital list created from startup - should be
	 * fixed return values
	 */
	@Test
	public void readCompositeSlice_String_UUIDTest() throws Exception {
		assertThat(getKeySpace(), notNullValue());
		assertThat(getKeySpace().getKeyspaceName(), is("IHR_local"));
		/* and query all what you want */
		List profileIds = new ArrayList();

		Integer prId1 = new Integer(101);
		Integer prId2 = new Integer(102);

		profileIds.add(prId1);
		profileIds.add(prId2);
		assertThat(
				historyDAO.readCompositeSlice_String_UUID(prId1, "track", 100)
						.size(), is(3));
		assertThat(historyDAO
				.readCompositeSlice_String_UUID(prId1, "show", 100).size(),
				is(1));
		assertThat(historyDAO.readCompositeSlice_String_UUID(prId1, null, 100)
				.size(), is(4));

	}

	/*
	 * Test iterating through the inital list created from startup - should be
	 * fixed return values
	 */
	@Test
	public void readCompositeSlice_UUID_String() throws Exception {
		assertThat(getKeySpace(), notNullValue());
		assertThat(getKeySpace().getKeyspaceName(), is("IHR_local"));
		/* and query all what you want */
		List profileIds = new ArrayList();

		Integer prId1 = new Integer(101);
		Integer prId2 = new Integer(102);

		profileIds.add(prId1);
		profileIds.add(prId2);
		assertThat(
				historyDAO.readCompositeSlice_UUID_String(prId1, "track", 100)
						.size(), is(3));
		// assertThat(historyDAO
		// .readCompositeSlice_UUID_String(prId1, "show", 100).size(),
		// is(1));
		assertThat(historyDAO.readCompositeSlice_UUID_String(prId1, null, 100)
				.size(), is(4));
	}

}
