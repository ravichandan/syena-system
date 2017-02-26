package com.chaaps.syena.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.virtual.MemberIdEmailInstallationIdDataObject;

@Repository
public interface MemberRepository extends CrudRepository<Member, Serializable> {

	// @Query("select m from member m where m.email=:email and m.active=true")
	// public Member findByEmail(@Param("email") String email);
	public Member findByEmail(String email);

	@Query("select m.id from Member m where m.email = :email")
	public Long findIdByEmail(@Param("email") String email);

	public MemberIdEmailInstallationIdDataObject findEmailInstIdByEmail(String email);

	@Query("select m.id from Member m where m.email = :email and m.installationId= :installationId")
	public Long findIdByEmailAndInstallationId(@Param("email") String email,
			@Param("installationId") String installationId);

	public Long countByEmailAndInstallationIdAndActive(String email, String installationId, boolean active);

	@Query("select m.registrationToken from MemberRegistration m")
	public List<String> findAllRegistrationTokens();
}
