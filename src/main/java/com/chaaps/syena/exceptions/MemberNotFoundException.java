package com.chaaps.syena.exceptions;

public class MemberNotFoundException extends SyenaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3561616105078354437L;

	public MemberNotFoundException(String email) {
		super("Member not found with the given email-id: " + email);
	}
}