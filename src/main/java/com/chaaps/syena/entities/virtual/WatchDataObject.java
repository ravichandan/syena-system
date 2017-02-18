package com.chaaps.syena.entities.virtual;

public interface WatchDataObject {

	boolean getTargetAccepted();

	String getWatchName();

	// @Value("#{target.originMember.email}")
	String getTargetMemberEmail();
}
