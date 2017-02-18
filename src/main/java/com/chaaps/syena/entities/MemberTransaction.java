package com.chaaps.syena.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "MEMBER_TXN")
@NamedQuery(name = "MemberTransaction.findAll", query = "SELECT m FROM MemberTransaction m")
public class MemberTransaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private Member member;
	private Timestamp createdDate;
	private String pin;
	private String tagCode;
	private Timestamp updatedDate;
	private String txnInstallationId;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the member
	 */
	@OneToOne
	@JoinColumn(name = "MEMBER_ID", nullable = false)
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
	 * @return the createdDate
	 */
	@Column(name = "CREATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the pin
	 */
	@Column(name = "PIN", length = 6)
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin
	 *            the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * @return the tagCode
	 */
	@Column(name = "TAG_CODE", length = 8, unique = true, nullable = true)
	public String getTagCode() {
		return tagCode;
	}

	/**
	 * @param tagCode
	 *            the tagCode to set
	 */
	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}

	/**
	 * @return the updatedDate
	 */
	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate
	 *            the updatedDate to set
	 */
	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * @return the txnInstallationId
	 */
	@Column(name = "TXN_INSTALLATION_ID", unique = true)
	public String getTxnInstallationId() {
		return txnInstallationId;
	}

	/**
	 * @param txnInstallationId
	 *            the txnInstallationId to set
	 */
	public void setTxnInstallationId(String txnInstallationId) {
		this.txnInstallationId = txnInstallationId;
	}

}
