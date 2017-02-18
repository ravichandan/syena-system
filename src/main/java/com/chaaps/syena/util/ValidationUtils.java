package com.chaaps.syena.util;

import org.apache.commons.lang.StringUtils;

public class ValidationUtils {

	/**
	 * returns null if there is no error, errorMessage other wise..
	 * 
	 * @param email
	 * @return
	 */
	public static String validateEmail(String email) {
		// TODO
		if (StringUtils.isBlank(email) || !email.contains("@"))
			return "Invalid email";
		return null;
	}

	public static String validatePin(String pin) {
		// TODO
		if (StringUtils.isBlank(pin))
			return "Invalid PIN";
		return null;
	}

	public static String validateTagCode(String tagCode) {
		// TODO
		if (StringUtils.isBlank(tagCode))
			return "Invalid TagCode";
		return null;
	}
}
