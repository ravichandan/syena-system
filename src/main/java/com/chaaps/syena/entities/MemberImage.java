package com.chaaps.syena.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "MEMBER_IMAGE")
public class MemberImage {

	private int id;

	private byte[] image;

	private boolean isProfilePic;

	private Timestamp createdDate;

	private Timestamp updatedDate;

	// @Transient
	private Member member;

	//
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the member
	 */
	@OneToOne(mappedBy = "memberImage")
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
	 * @return the image
	 */
	@Column(name = "IMAGE")
	@Lob
	public byte[] getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(byte[] image) {
		this.image = image;
	}

	/**
	 * @return the isProfilePic
	 */
	@Column(name = "PROFILE_PIC")
	public boolean isProfilePic() {
		return isProfilePic;
	}

	/**
	 * @param isProfilePic
	 *            the isProfilePic to set
	 */
	public void setProfilePic(boolean isProfilePic) {
		this.isProfilePic = isProfilePic;
	}

	/**
	 * @return the createdDate
	 */
	@Column(name = "CREATED_DATE")
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
	 * @return the updatedDate
	 */
	@Column(name = "UPDATED_DATE")
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

}
