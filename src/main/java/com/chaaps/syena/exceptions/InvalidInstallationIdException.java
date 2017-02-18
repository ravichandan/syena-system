package com.chaaps.syena.exceptions;

public class InvalidInstallationIdException extends SyenaException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2769196869962733256L;

	public InvalidInstallationIdException() {
		super("InstallationId is empty or invalid");
	}

}
