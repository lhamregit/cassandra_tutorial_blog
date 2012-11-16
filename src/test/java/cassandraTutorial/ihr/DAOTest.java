package cassandraTutorial.ihr;

import java.io.IOException;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CassandraUnit;
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration("classpath*:test-dao-beans.xml")
@ContextConfiguration(locations = { "classpath:test-dao-beans.xml" })
public class DAOTest {

	private static final Logger log = Logger.getLogger(DAOTest.class);

	@BeforeClass
	public static void before() throws Exception {

		/* start an embedded cassandra */
		EmbeddedCassandraServerHelper.startEmbeddedCassandra();

		/* load data */
		DataLoader dataLoader = new DataLoader("Test Cluster", "localhost:9171");
		dataLoader.load(new ClassPathXmlDataSet("MyDataSet.xml"));
	}

	@AfterClass
	public static void after() throws Exception {
		/* Stop an embedded cassandra */
		EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
	}

	@Test
	public void doTest() throws Exception {

	}

}
