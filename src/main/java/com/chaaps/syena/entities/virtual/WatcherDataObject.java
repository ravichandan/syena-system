package com.chaaps.syena.entities.virtual;

public interface WatcherDataObject {

	boolean getTargetAccepted();

	String getNickName();

	// @Value("#{target.originMember.email}")
	String getOriginMemberEmail();
}
