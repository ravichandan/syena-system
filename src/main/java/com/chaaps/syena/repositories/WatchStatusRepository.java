package com.chaaps.syena.repositories;

import com.chaaps.syena.entities.WatchStatus;

//@Repository
public interface WatchStatusRepository {// extends CrudRepository<WatchStatus,
										// Serializable> {

	public WatchStatus findByStatus(String status);
}
