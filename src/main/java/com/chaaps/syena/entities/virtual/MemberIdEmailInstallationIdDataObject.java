package com.chaaps.syena.entities.virtual;

public interface MemberIdEmailInstallationIdDataObject {

	Long getId();

	String getEmail();

	// @Value("#{target.originMember.email}")
	String getInstallationId();
}
