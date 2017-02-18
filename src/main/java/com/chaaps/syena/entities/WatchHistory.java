package com.chaaps.syena.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.sql.Timestamp;
import java.math.BigInteger;

/**
 * The persistent class for the watch_history database table.
 * 
 */
@Entity
@Table(name = "watch_history")
@NamedQuery(name = "WatchHistory.findAll", query = "SELECT w FROM WatchHistory w")
public class WatchHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Timestamp createdDate;
	private double originLatitude;
	private double originLongitude;
	private double targetLatitude;
	private double targetLongitude;
	private Timestamp updatedDate;
	private BigInteger watchId;
	private Member originMember;
	private Member targetMember;
	private String status;

	public WatchHistory() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "CREATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Column(name = "ORIGIN_LATITUDE")
	public double getOriginLatitude() {
		return this.originLatitude;
	}

	public void setOriginLatitude(double originLatitude) {
		this.originLatitude = originLatitude;
	}

	@Column(name = "ORIGIN_LONGITUDE")
	public double getOriginLongitude() {
		return this.originLongitude;
	}

	public void setOriginLongitude(double originLongitude) {
		this.originLongitude = originLongitude;
	}

	@Column(name = "TARGET_LATITUDE")
	public double getTargetLatitude() {
		return this.targetLatitude;
	}

	public void setTargetLatitude(double targetLatitude) {
		this.targetLatitude = targetLatitude;
	}

	@Column(name = "TARGET_LONGITUDE")
	public double getTargetLongitude() {
		return this.targetLongitude;
	}

	public void setTargetLongitude(double targetLongitude) {
		this.targetLongitude = targetLongitude;
	}

	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Column(name = "WATCH_ID", nullable = false)
	public BigInteger getWatchId() {
		return this.watchId;
	}

	public void setWatchId(BigInteger watchId) {
		this.watchId = watchId;
	}

	// uni-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name = "ORIGIN_MEMBER_ID", nullable = false)
	public Member getOriginMember() {
		return this.originMember;
	}

	public void setOriginMember(Member originMember) {
		this.originMember = originMember;
	}

	// uni-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name = "TARGET_MEMBER_ID", nullable = false)
	public Member getTargetMember() {
		return this.targetMember;
	}

	public void setTargetMember(Member targetMember) {
		this.targetMember = targetMember;
	}

	/**
	 * @return the status
	 */
	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	// uni-directional many-to-one association to WatchStatus

}