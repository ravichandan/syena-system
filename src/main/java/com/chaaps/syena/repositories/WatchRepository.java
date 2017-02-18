package com.chaaps.syena.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.Watch;

@Repository
public interface WatchRepository extends CrudRepository<Watch, Serializable> {

	public List<Watch> findByTargetMember(Member targetMember);

	public Watch findByOriginMemberAndTargetMember(Member originMember, Member targetMember);

	@Query("select om from Watch w left join w.originMember om left join w.targetMember tm where w.targetMember = :targetMember")
	public List<Member> findOriginMembersByTargetMember(@Param("targetMember") Member targetMember);

	@Query("select om from Watch w left join w.originMember om left join w.targetMember tm where w.targetMember = :targetMember and w.status=:status")
	public List<Member> findOriginMembersByTargetMemberAndStatusNotEquals(@Param("targetMember") Member targetMember,
			@Param("status") String status);

	public List<Watch> findByTargetMemberAndStatusNot(Member targetMember, String status);

}
