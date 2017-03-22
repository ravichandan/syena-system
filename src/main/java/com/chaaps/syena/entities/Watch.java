package com.chaaps.syena.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * The persistent class for the watch database table.
 * 
 */
@Entity
@Table(name = "watch")
@NamedQuery(name = "Watch.findAll", query = "SELECT w FROM Watch w")
public class Watch implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Timestamp createdDate;
	private Timestamp updatedDate;
	private Member originMember;
	private Member targetMember;
	private boolean targetAccepted;
	private List<WatchConfiguration> watchConfigurations;
	private String status;
	private String nickName;
	private WatchInstance watchInstance;
	private Set<WatchMessage> watchMessages;

	public Watch() {
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

	@Column(name = "CREATED_DATE", nullable = false, insertable = false, updatable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Column(name = "UPDATED_DATE", nullable = false, insertable = false, updatable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	// bi-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name = "ORIGIN_MEMBER_ID", nullable = false)
	public Member getOriginMember() {
		return this.originMember;
	}

	public void setOriginMember(Member originMember) {
		this.originMember = originMember;
	}

	// bi-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name = "TARGET_MEMBER_ID", nullable = false)
	public Member getTargetMember() {
		return this.targetMember;
	}

	public void setTargetMember(Member targetMember) {
		this.targetMember = targetMember;
	}

	/**
	 * @return the targetAccepted
	 */
	@Column(name = "TARGET_ACCEPTED")
	public boolean isTargetAccepted() {
		return targetAccepted;
	}

	/**
	 * @param targetAccepted
	 *            the targetAccepted to set
	 */
	public void setTargetAccepted(boolean targetAccepted) {
		this.targetAccepted = targetAccepted;
	}

	// uni-directional many-to-one association to WatchConfiguration

	/**
	 * @return the watchName
	 */
	@Column(name = "NICK_NAME")
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param watchName
	 *            the watchName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@OneToMany(mappedBy = "watch", cascade = CascadeType.ALL)
	public List<WatchConfiguration> getWatchConfigurations() {
		if (watchConfigurations == null)
			watchConfigurations = new ArrayList<>();
		return this.watchConfigurations;
	}

	public void setWatchConfigurations(List<WatchConfiguration> watchConfigurations) {
		this.watchConfigurations = watchConfigurations;
	}

	// uni-directional many-to-one association to WatchStatus
	// @ManyToOne
	@Column(name = "STATUS")
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// bi-directional one-to-one association to WatchInstance
	@OneToOne(mappedBy = "watch", cascade = CascadeType.ALL)
	public WatchInstance getWatchInstance() {
		return this.watchInstance;
	}

	public void setWatchInstance(WatchInstance watchInstance) {
		this.watchInstance = watchInstance;
		if (this.watchInstance != null)
			this.watchInstance.setWatch(this);
	}

	/*
	 * public WatchInstance addWatchInstance(WatchInstance watchInstance) {
	 * getWatchInstances().add(watchInstance); watchInstance.setWatch(this);
	 * 
	 * return watchInstance; }
	 * 
	 * public WatchInstance removeWatchInstance(WatchInstance watchInstance) {
	 * getWatchInstances().remove(watchInstance); watchInstance.setWatch(null);
	 * 
	 * return watchInstance; }
	 */

	// bi-directional many-to-one association to WatchMessage
	@OneToMany(mappedBy = "watch")
	public Set<WatchMessage> getWatchMessages() {
		return this.watchMessages;
	}

	public void setWatchMessages(Set<WatchMessage> watchMessages) {
		this.watchMessages = watchMessages;
	}

	public WatchMessage addWatchMessage(WatchMessage watchMessage) {
		getWatchMessages().add(watchMessage);
		watchMessage.setWatch(this);

		return watchMessage;
	}

	public WatchMessage removeWatchMessage(WatchMessage watchMessage) {
		getWatchMessages().remove(watchMessage);
		watchMessage.setWatch(null);

		return watchMessage;
	}

}