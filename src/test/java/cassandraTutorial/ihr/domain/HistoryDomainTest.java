package cassandraTutorial.ihr.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cass.ihr.domain.HistoryEvent;

public class HistoryDomainTest {

	private static Integer TEST_INT_ONE = new Integer(1);
	private static String TEST_MESSAGE_ONE = "hello this is a message";

	@Test
	public void testEquals() {
		// just a quick check
		HistoryEvent ev1 = new HistoryEvent(TEST_INT_ONE, TEST_MESSAGE_ONE);
		HistoryEvent ev2 = new HistoryEvent(TEST_INT_ONE, TEST_MESSAGE_ONE);

		assertEquals(ev1, ev2);
	}

}
