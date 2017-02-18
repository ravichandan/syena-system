package com.chaaps.syena.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.MemberTransaction;

@Repository
public interface MemberTransactionRepository extends JpaRepository<MemberTransaction, Long> {

	public MemberTransaction findByMember(Member member);

	public MemberTransaction findByMemberAndTxnInstallationId(Member member,String txnInstallationId);

	public MemberTransaction findByTagCode(String tagCode);
}
