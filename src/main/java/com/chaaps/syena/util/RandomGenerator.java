package com.chaaps.syena.util;

import java.util.Calendar;

public class RandomGenerator {

	/**
	 * Generates a random 16-character instance-id
	 * 
	 * @return
	 */
	public static String generateInstanceId() {
		// TODO Use a complex hashing algorithm and generate 16-character
		// string;

		return Calendar.getInstance().getTime().toString().replaceAll(" ", "");
	}

	public static String generate4DigitPin() {
		// TODO Use a complex hashing algorithm and generate 4- digit pin;

		// TODO the following is temporary implementation to generate 4 digits.
		// replace with a better algo in future
		String longTime = String.valueOf(Calendar.getInstance().getTimeInMillis());
		int len = longTime.length();

		return longTime.substring(len - 4);
	}

	public static String generate6CharacterTagCode() {
		// TODO Use a complex hashing algorithm and generate 6- digit pin;

		// TODO the following is temporary implementation to generate 6 digits.
		// replace with a better algo in future
		String longTime = String.valueOf(Calendar.getInstance().getTimeInMillis());
		int len = longTime.length();

		return longTime.substring(len - 6);
	}
}
