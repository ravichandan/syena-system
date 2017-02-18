package com.chaaps.syena.exceptions;

public class InvalidEmailException extends SyenaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7686673479589959792L;

	public InvalidEmailException(String email) {
		super("Received email is invalid " + email);
	}
}
