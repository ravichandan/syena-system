package com.chaaps.syena.entities.virtual;

public interface WatchDataObject {

	boolean getTargetAccepted();
	
	String getWatchStatus();

	String getWatchName();

	// @Value("#{target.originMember.email}")
	String getTargetMemberEmail();
}
