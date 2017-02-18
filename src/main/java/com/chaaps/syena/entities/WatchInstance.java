package com.chaaps.syena.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.sql.Timestamp;

/**
 * The persistent class for the watch_instance database table.
 * 
 */
@Entity
@Table(name = "watch_instance")
@NamedQuery(name = "WatchInstance.findAll", query = "SELECT w FROM WatchInstance w")
public class WatchInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private double altitudeApart;
	private Timestamp createdDate;
	private String direction;
	private double distanceApart;
	private Timestamp updatedDate;
	private Watch watch;

	public WatchInstance() {
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

	@Column(name = "ALTITUDE_APART")
	public double getAltitudeApart() {
		return this.altitudeApart;
	}

	public void setAltitudeApart(double altitudeApart) {
		this.altitudeApart = altitudeApart;
	}

	@Column(name = "CREATED_DATE", nullable = false)
	@Generated(GenerationTime.NEVER)
	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Column(length = 8)
	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Column(name = "DISTANCE_APART")
	public double getDistanceApart() {
		return this.distanceApart;
	}

	public void setDistanceApart(double distanceApart) {
		this.distanceApart = distanceApart;
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
	@OneToOne
	@JoinColumn(name = "WATCH_ID", nullable = false)
	public Watch getWatch() {
		return this.watch;
	}

	public void setWatch(Watch watch) {
		this.watch = watch;
	}

}