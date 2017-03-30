package com.chaaps.syena.entities.virtual;

import java.sql.Timestamp;

public interface WatcherDataObject {

	boolean getTargetAccepted();

	String getNickName();

	// @Value("#{target.originMember.email}")
	String getOriginMemberEmail();
	
	String getStatus();

	Timestamp getUpdatedDate();
}
