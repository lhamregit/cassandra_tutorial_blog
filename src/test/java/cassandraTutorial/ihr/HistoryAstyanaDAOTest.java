package cassandraTutorial.ihr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

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
import com.cass.ihr.dao.IHistoryAstyanaDAO;
import com.cass.ihr.dao.IHistoryDAO;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class HistoryAstyanaDAOTest extends DAOTest {

	private static final Logger log = Logger
			.getLogger(HistoryAstyanaDAOTest.class);

	@Autowired
	private IHistoryAstyanaDAO historyAstyanaDAO;

	@Autowired
	private ConnectionPoolConfigurationImpl connectionPoolConfigurationImpl;

	@Test
	public void writeEventsTest() throws Exception {

	}

	@Test
	public void iterateEventsTest() throws Exception {
		List profileIds = new ArrayList();

		Integer prId1 = new Integer(101);
		Integer prId2 = new Integer(102);

		profileIds.add(prId1);
		profileIds.add(prId2);
		// historyDAO.iterateEventsHector(profileIds);
		assertTrue(historyAstyanaDAO.iterateEvents(profileIds));
	}

	@Test
	public void deleteEventsTest() throws Exception {

	}

	public void setHistoryAstyanaDAO(IHistoryAstyanaDAO historyAstyanaDAO) {
		this.historyAstyanaDAO = historyAstyanaDAO;
	}

	public void setKeyspaceAstyana(Keyspace keyspaceAstyana) {
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
	}

}
