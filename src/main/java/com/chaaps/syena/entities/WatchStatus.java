package com.chaaps.syena.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.sql.Timestamp;

/**
 * The persistent class for the watch_status database table.
 * 
 */
//@Entity
//@Table(name = "watch_status")
//@NamedQuery(name = "WatchStatus.findAll", query = "SELECT w FROM WatchStatus w")
public class WatchStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Timestamp createdDate;
	private String description;
	private String status;
	private Timestamp updatedDate;

	public WatchStatus() {
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

	@Column(length = 50)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(nullable = false, length = 16)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

}