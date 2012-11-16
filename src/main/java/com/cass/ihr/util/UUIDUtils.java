package com.cass.ihr.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class UUIDUtils
{
	/*
	 * Magic number obtained from #cassandra's thobbs, who claims to have stolen
	 * it from a Python library.
	 */
	final static long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

	public static java.util.UUID toUUID(byte[] uuid)
	{

		ByteBuffer buffer = ByteBuffer.allocate(16);

		buffer.put(uuid);

		buffer.rewind();

		com.eaio.uuid.UUID u = new com.eaio.uuid.UUID(buffer.getLong(), buffer.getLong());

		return java.util.UUID.fromString(u.toString());

	}

	public static byte[] asByteArray(java.util.UUID uuid)
	{

		ByteBuffer buffer = ByteBuffer.allocate(16);

		buffer.putLong(uuid.getMostSignificantBits());

		buffer.putLong(uuid.getLeastSignificantBits());

		return buffer.array();

	}

	public static byte[] getbytes(Object obj) throws java.io.IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		byte[] data = bos.toByteArray();
		return data;
	}

	public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException
	{
		Object object = null;

		object = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes)).readObject();

		return object;
	}

	/**
	 * Generate a new time UUID insatnce.
	 * 
	 * @return A new time UUID object
	 */
	public static java.util.UUID newTimeUuid()
	{
		return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
	}

	/**
	 * @see #nonUniqueTimeUuidForDate(long)
	 */
	public static java.util.UUID nonUniqueTimeUuidForDate(Date d)
	{
		return nonUniqueTimeUuidForDate(d.getTime());
	}

	/**
	 * @see #nonUniqueTimeUuidForDate(long)
	 */
	public static java.util.UUID nonUniqueTimeUuidForDate(Calendar c)
	{
		return nonUniqueTimeUuidForDate(c.getTime());
	}

	/**
	 * <p>
	 * This method is useful to create a <b>*non-unique*</b> TimeUUID instance
	 * from some time other than the present. For example, to use as the lower
	 * bound in a SlicePredicate to retrieve all columns whose TimeUUID comes
	 * after time X.
	 * 
	 * <p>
	 * <b>WARNING:</b> Never assume such a UUID is unique, use it only as a
	 * marker for a specific time.
	 * <p>
	 * Note: This method and it's doco is taken (almost verbatim) from the
	 * Cassandra WIKI
	 * (http://wiki.apache.org/cassandra/FAQ#working_with_timeuuid_in_java).
	 * 
	 * @param millis
	 *            Gets the milliseconds of the datetime instant from the Java
	 *            epoch of 1970-01-01T00:00:00Z
	 * @return return
	 */
	public static java.util.UUID nonUniqueTimeUuidForDate(long millis)
	{
		long time = millis * 10000 + NUM_100NS_INTERVALS_SINCE_UUID_EPOCH;
		long timeLow = time & 0xffffffffL;
		long timeMid = time & 0xffff00000000L;
		long timeHi = time & 0xfff000000000000L;
		long upperLong = (timeLow << 32) | (timeMid >> 16) | (1 << 12) | (timeHi >> 48);
		return new java.util.UUID(upperLong, 0xC000000000000000L);
	}

	/**
	 * <p>
	 * Extracts the millis from the Java epoch of 1970-01-01T00:00:00Z.
	 * </p>
	 * 
	 * @param uuid
	 *            the time uuid
	 * @return the millis since Java epoch
	 */
	public static long millisFromTimeUuid(UUID uuid)
	{
		return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
	}

}
