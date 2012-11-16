package com.cass.ihr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SampleGenerator {

	public static Map createSampleUsers(int sampleSize) {
		Map newData = new HashMap();

		for (int i = 0; i < sampleSize; i++) {
			Map data = new HashMap();

			String userId = "" + i;
			newData.put(userId, data);
		}
		return newData;
	}

	public static void createSampleUserData(Map users, int numOfThumbs) {
		Iterator iter = users.keySet().iterator();
		int k = 0;
		while (iter.hasNext()) {

			// get the user ID
			String key = (String) iter.next();
			System.out.println("doing: " + k);
			Map data = (Map) users.get(key);
			for (int i = 0; i < numOfThumbs; i++) {
				try {
					Thread.sleep(1);
					UUID timeListened = UUIDUtils
							.nonUniqueTimeUuidForDate(System
									.currentTimeMillis());
					long l = UUIDUtils.millisFromTimeUuid(timeListened);
					l = l + 1;
					UUID newTime = UUIDUtils.nonUniqueTimeUuidForDate(l);
					data.put(timeListened, "up|123112|" + i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			k++;
		}
	}
}
