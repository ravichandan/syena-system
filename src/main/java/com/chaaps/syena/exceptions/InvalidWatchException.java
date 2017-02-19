package com.chaaps.syena.exceptions;

public class InvalidWatchException extends SyenaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6967435498368282521L;

	public InvalidWatchException() {
		super("You are not tagged with the member yet");
	}

	public InvalidWatchException(String message) {
		super("You are not tagged with the member yet.. " + message);
	}

}
