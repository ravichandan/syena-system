package com.chaaps.syena.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.sql.Timestamp;

/**
 * The persistent class for the watch_message database table.
 * 
 */
@Entity
@Table(name = "WATCH_MESSAGE")
@NamedQuery(name = "WatchMessage.findAll", query = "SELECT w FROM WatchMessage w")
public class WatchMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Timestamp createdDate;
	private String description;
	private String message;
	private Timestamp updatedDate;
	private Watch watch;

	public WatchMessage() {
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

	@Column(length = 256)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = 32)
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	// bi-directional many-to-one association to Watch
	@ManyToOne
	@JoinColumn(name = "WATCH_ID", nullable = false)
	public Watch getWatch() {
		return this.watch;
	}

	public void setWatch(Watch watch) {
		this.watch = watch;
	}

}