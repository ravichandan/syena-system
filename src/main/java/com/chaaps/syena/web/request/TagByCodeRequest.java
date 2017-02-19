package com.chaaps.syena.web.request;

public class TagByCodeRequest {

	private String requester;
	private String tagCode;

	/**
	 * @return the QP_REQUESTER
	 */
	public String getRequester() {
		return requester;
	}

	/**
	 * @param QP_REQUESTER
	 *            the QP_REQUESTER to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}

	/**
	 * @return the tagCode
	 */
	public String getTagCode() {
		return tagCode;
	}

	/**
	 * @param tagCode
	 *            the tagCode to set
	 */
	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}

}
