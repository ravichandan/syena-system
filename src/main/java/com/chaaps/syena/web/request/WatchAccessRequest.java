package com.chaaps.syena.web.request;

public class WatchAccessRequest {

	private String email;

	private String revokeeEmail;

	private boolean flag;

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the revokeeEmail
	 */
	public String getRevokeeEmail() {
		return revokeeEmail;
	}

	/**
	 * @param revokeeEmail
	 *            the revokeeEmail to set
	 */
	public void setRevokeeEmail(String revokeeEmail) {
		this.revokeeEmail = revokeeEmail;
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
