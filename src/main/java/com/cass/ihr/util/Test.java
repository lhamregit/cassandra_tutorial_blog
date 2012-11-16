package com.cass.ihr.util;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(UUIDUtils.newTimeUuid());
		String a = "abc";
		String b = "abc";
		String c = "abc";
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"");
		sb.append("message");
		sb.append("\"");
		sb.append(":");
		sb.append("\"");
		sb.append("this is a history event");
		sb.append("\"");
		sb.append(",");
		sb.append("\"");
		sb.append("id");
		sb.append("\"");
		sb.append(":");
		sb.append("1002}");
		byte[] byteArray = sb.toString().getBytes();

		
		System.out.println("sb: " + sb.toString());
		System.out.println("byteArray" + byteArray);

	}

}
