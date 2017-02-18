package com.chaaps.syena.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.MemberTransaction;

@Repository
public interface MemberTransactionRepository extends JpaRepository<MemberTransaction, Long> {

	public MemberTransaction findByMember(Member member);

	public MemberTransaction findByMemberAndTxnInstallationId(Member member, String txnInstallationId);

	@Query("select mt from MemberTransaction mt where mt.member.id = :memberId and mt.txnInstallationId= :txnInstallationId")
	public MemberTransaction findByMemberIdAndTxnInstallationId(@Param("memberId") Long memberId,
			@Param("txnInstallationId") String txnInstallationId);

	public MemberTransaction findByTagCode(String tagCode);

	@Modifying
	@Query("delete from MemberTransaction mt where mt.member.id= :memberId")
	public void deleteByMemberId(@Param("memberId") long memberId);
}
