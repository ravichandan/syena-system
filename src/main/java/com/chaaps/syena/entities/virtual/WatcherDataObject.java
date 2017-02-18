package com.chaaps.syena.entities.virtual;

public interface WatcherDataObject {

	boolean getTargetAccepted();

	String getWatchName();

	// @Value("#{target.originMember.email}")
	String getOriginMemberEmail();
}
