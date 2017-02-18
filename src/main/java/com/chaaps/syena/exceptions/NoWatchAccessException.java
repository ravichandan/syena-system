package com.chaaps.syena.exceptions;

public class NoWatchAccessException extends SyenaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6000836595170669845L;

	public NoWatchAccessException() {
		super("Don't have access to watch the member. Request for the same");
	}

	public NoWatchAccessException(String message) {
		super(message);
	}

}
