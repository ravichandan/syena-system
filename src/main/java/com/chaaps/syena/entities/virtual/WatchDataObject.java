package com.chaaps.syena.entities.virtual;

public interface WatchDataObject {

	boolean getTargetAccepted();
	
	String getWatchStatus();

	String getNickName();

	// @Value("#{target.originMember.email}")
	String getTargetMemberEmail();
}
