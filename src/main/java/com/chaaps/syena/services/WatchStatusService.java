package com.chaaps.syena.services;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.chaaps.syena.entities.WatchStatus;
import com.chaaps.syena.repositories.WatchStatusRepository;

//@Service
public class WatchStatusService {

	private Logger logger = Logger.getLogger(WatchStatusService.class);

	// @Autowired
	private WatchStatusRepository watchStatusRepository;

	/**
	 * @return the watchStatusRepository
	 */
	public WatchStatusRepository getWatchStatusRepository() {
		return watchStatusRepository;
	}

	/**
	 * @param watchStatusRepository
	 *            the watchStatusRepository to set
	 */
	public void setWatchStatusRepository(WatchStatusRepository watchStatusRepository) {
		this.watchStatusRepository = watchStatusRepository;
	}

	public WatchStatus findByStatus(String status) {
		logger.info("Querying WatchStatus for status : " + status + ".");
		if (StringUtils.isBlank(status))
			return null;
		return watchStatusRepository.findByStatus(status);
	}

	/*
	 * @Transactional public WatchStatus save(WatchStatus watchStatus) {
	 * logger.info("Saving WatchStatus : " + watchStatus); if (watchStatus ==
	 * null) return null; return watchStatusRepository.save(watchStatus); }
	 */

}
