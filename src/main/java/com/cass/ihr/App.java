package com.cass.ihr;

import java.io.IOException;
import java.util.Map;

import com.cass.ihr.util.SampleGenerator;


/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		Map users = SampleGenerator.createSampleUsers(10000);
		SampleGenerator.createSampleUserData(users, 150);
		System.out.println("size: " + users.size());
		
	}
}
