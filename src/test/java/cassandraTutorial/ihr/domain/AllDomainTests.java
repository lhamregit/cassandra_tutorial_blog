package cassandraTutorial.ihr.domain;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses({ HistoryDomainTest.class })
public class AllDomainTests {

	private static final Logger log = Logger.getLogger(AllDomainTests.class);
	
}
