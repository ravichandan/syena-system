package com.chaaps.syena.util;

import org.apache.commons.lang.StringUtils;

public class ValidationUtils {

	/**
	 * returns null if there is no error, errorMessage other wise..
	 * 
	 * @param email
	 * @return
	 */
	public static StringBuilder validateEmail(String email) {
		// TODO
		if (StringUtils.isBlank(email) || !email.contains("@"))
			return new StringBuilder(" Invalid email ");
		return null;
	}

	public static StringBuilder validatePin(String pin) {
		// TODO
		if (StringUtils.isBlank(pin))
			return new StringBuilder("Invalid PIN");
		return null;
	}

	public static StringBuilder validateTagCode(String tagCode) {
		// TODO
		if (StringUtils.isBlank(tagCode))
			return new StringBuilder("Invalid TagCode");
		return null;
	}
}
