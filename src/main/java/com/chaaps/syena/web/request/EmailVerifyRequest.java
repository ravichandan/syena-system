package com.chaaps.syena.web.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sitir on 01-02-2017.
 */

@XmlRootElement
public class EmailVerifyRequest {

	@XmlElement
	private String email;

	@XmlElement
	private String instanceId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}
