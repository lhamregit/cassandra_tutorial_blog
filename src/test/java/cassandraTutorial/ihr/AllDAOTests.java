package cassandraTutorial.ihr;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses({ ManualMyHistoryDAO.class, HistoryAstyanaDAOTest.class })
public class AllDAOTests {

	private static final Logger log = Logger.getLogger(AllDAOTests.class);
}
