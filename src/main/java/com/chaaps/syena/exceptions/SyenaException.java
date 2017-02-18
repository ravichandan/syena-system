package com.chaaps.syena.exceptions;

import org.apache.log4j.Logger;

public class SyenaException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8610236846906781923L;

	private Logger logger = Logger.getLogger(SyenaException.class);

	public SyenaException(String message) {
		super(message);
		logger.debug("Registered exception with message : " + message);

	}

}
