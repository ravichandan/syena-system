package com.chaaps.syena.repositories;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chaaps.syena.entities.Member;

@Repository
public interface MemberRepository extends CrudRepository<Member, Serializable> {

	// @Query("select m from member m where m.email=:email and m.active=true")
	// public Member findByEmail(@Param("email") String email);
	public Member findByEmail(String email);
}
