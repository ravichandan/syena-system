package com.chaaps.syena.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * The persistent class for the watch_configuration database table.
 * 
 */
@Entity
@Table(name = "watch_configuration")
@NamedQuery(name = "WatchConfiguration.findAll", query = "SELECT w FROM WatchConfiguration w")
public class WatchConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Timestamp createdDate;
	private String entry;
	private String entryType;
	private String tag;
	private Timestamp updatedDate;
	private String value;
	private Watch watch;

	public WatchConfiguration() {
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

	@Column(nullable = false, length = 32)
	public String getEntry() {
		return this.entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	@Column(name = "ENTRY_TYPE", length = 20)
	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	@Column(length = 16)
	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(name = "UPDATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Column(length = 32)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the watch
	 */
	@ManyToOne
	public Watch getWatch() {
		return watch;
	}

	/**
	 * @param watch
	 *            the watch to set
	 */
	public void setWatch(Watch watch) {
		this.watch = watch;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WatchConfiguration) {
			return false;
		}
		WatchConfiguration that = (WatchConfiguration) obj;
		return this.entry.equals(that.entry) && this.value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return (entry + value).hashCode();
	}
}