package com.chaaps.syena.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MEMBER_REGISTRATION")
public class MemberRegistration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7093691690647157630L;

	// private long id;

	private Member member;

	private String registrationToken;

	/**
	 * @return the id
	 */
	@Id
	@OneToOne
	@JoinColumn(name = "MEMBER_ID", referencedColumnName = "id")
	public Member getMember() {
		return member;
	}

	/**
	 * @param member
	 *            the member to set
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * @return the registrationToken
	 */
	@Column(name = "REGISTRATION_TOKEN")
	public String getRegistrationToken() {
		return registrationToken;
	}

	/**
	 * @param registrationToken
	 *            the registrationToken to set
	 */
	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

}
