package com.chaaps.syena.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * The persistent class for the member database table.
 * 
 */
@Entity
@Table(name = "MEMBER")
@NamedQuery(name = "Member.findAll", query = "SELECT m FROM Member m")
public class Member implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private double altitude;
	private Timestamp createdDate;
	private String direction;
	private String displayName;
	private String email;
	private String installationId;
	private boolean active;
	private double latitude;
	private double longitude;
	private Timestamp updatedDate;
	private Set<Watch> watches;
	private MemberRegistration memberRegistration;
	private MemberImage memberImage;
	// private MemberTransaction memberTransaction;

	public Member() {
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

	public double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "CREATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Column(length = 2)
	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Column(name = "DISPLAY_NAME", length = 32)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(nullable = false, length = 40)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "INSTALLATION_ID", length = 32)
	public String getInstallationId() {
		return this.installationId;
	}

	public void setInstallationId(String installationId) {
		this.installationId = installationId;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	// bi-directional many-to-one association to Watch
	@OneToMany(mappedBy = "originMember", fetch = FetchType.LAZY)
	public Set<Watch> getWatches() {
		return this.watches;
	}

	public void setWatches(Set<Watch> watches) {
		this.watches = watches;
	}

	public Watch addWatch(Watch watch) {
		getWatches().add(watch);
		watch.setOriginMember(this);

		return watch;
	}

	public Watch removeWatch(Watch watch) {
		getWatches().remove(watch);
		watch.setOriginMember(null);

		return watch;
	}

	/**
	 * @return the active
	 */
	@Column(name = "ACTIVE", nullable = false)
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the memberRegistration
	 */
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "member", fetch = FetchType.LAZY)
	public MemberRegistration getMemberRegistration() {
		return memberRegistration;
	}

	/**
	 * @param memberRegistration
	 *            the memberRegistration to set
	 */
	public void setMemberRegistration(MemberRegistration memberRegistration) {
		this.memberRegistration = memberRegistration;
	}

	@OneToOne  (cascade = CascadeType.ALL, fetch = FetchType.LAZY,
				 orphanRemoval = true)
	@JoinColumn(name = "MEMBER_IMAGE_ID")
	public MemberImage getMemberImage() {
		return memberImage;
	}

	public void setMemberImage(MemberImage memberImage) {
		this.memberImage = memberImage;
	}
	/**
	 * @return the memberTransaction
	 */
	// @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
	// public MemberTransaction getMemberTransaction() {
	// return memberTransaction;
	// }

	/**
	 * @param memberTransaction
	 *            the memberTransaction to set
	 */
	// public void setMemberTransaction(MemberTransaction memberTransaction) {
	// this.memberTransaction = memberTransaction;
	// }

}