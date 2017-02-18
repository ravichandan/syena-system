package com.chaaps.syena.exceptions;

public class InvalidMemberException extends SyenaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7492246874977877239L;

	public InvalidMemberException(String email) {
		super(email + " is either in-active or doesn't exist ");
	}
}
