package com.chaaps.syena.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.Watch;
import com.chaaps.syena.entities.WatchConfiguration;
import com.chaaps.syena.entities.WatchInstance;
import com.chaaps.syena.entities.virtual.WatchDataObject;
import com.chaaps.syena.entities.virtual.WatcherDataObject;
import com.chaaps.syena.exceptions.NoWatchAccessException;
import com.chaaps.syena.exceptions.SyenaException;
import com.chaaps.syena.repositories.WatchRepository;
import com.chaaps.syena.util.Constants;

@Service
public class WatchService {

	private Logger logger = Logger.getLogger(WatchService.class);

	@Autowired
	private WatchRepository watchRepository;

	@Autowired
	private WatchService watchService;

	/**
	 * @return the watchRepository
	 */
	public WatchRepository getWatchRepository() {
		return watchRepository;
	}

	/**
	 * @param watchRepository
	 *            the watchRepository to set
	 */
	public void setWatchRepository(WatchRepository watchRepository) {
		this.watchRepository = watchRepository;
	}

	/**
	 * @return the watchService
	 */
	public WatchService getWatchService() {
		return watchService;
	}

	/**
	 * @param watchService
	 *            the watchService to set
	 */
	public void setWatchService(WatchService watchService) {
		this.watchService = watchService;
	}

	public List<Watch> findByTargetMember(Member targetMember) {
		if (targetMember == null)
			return null;
		logger.debug("Querying for list of watches by targetMember " + targetMember.getEmail());
		return watchRepository.findByTargetMember(targetMember);
	}

	public List<Watch> findByTargetMemberWithActiveWatch(Member targetMember) {
		if (targetMember == null)
			return null;
		logger.debug("Querying for list of watches by targetMember " + targetMember.getEmail());
		return watchRepository.findByTargetMemberAndStatusNot(targetMember, Constants.WATCH_STATUS_IN_ACTIVE);
	}

	public Watch findByOriginMemberAndTargetMember(Member originMember, Member targetMember) {
		if (originMember == null || targetMember == null)
			return null;
		logger.debug(
				"Querying for watch by originMember " + originMember + " and targetMember " + targetMember.getEmail());
		return watchRepository.findByOriginMemberAndTargetMember(originMember, targetMember);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveBidirectionalWatch(Member m1, Member m2) {
		logger.info("Received request to create bidirectional 'watch'es");
		if (m1 == null || m2 == null)
			return;
		/*
		 * WatchStatus ws =
		 * watchStatusService.findByStatus(Constants.WATCH_STATUS_IN_ACTIVE); if
		 * (ws == null) { ws = new WatchStatus();
		 * ws.setStatus(Constants.WATCH_STATUS_IN_ACTIVE);
		 * ws.setDescription("Watch has been created"); ws =
		 * watchStatusService.save(ws); }
		 */
		logger.debug("Creating watch1 instance");
		if (findByOriginMemberAndTargetMember(m1, m2) == null) {
			Watch watch1 = new Watch();
			watch1.setOriginMember(m1);
			watch1.setTargetMember(m2);
			watch1.setWatchConfigurations(getDefaultConfiguration(watch1));
			watch1.setStatus(Constants.WATCH_STATUS_IN_ACTIVE);
			logger.debug("Saving 'watch1'");
			watchService.getWatchRepository().save(watch1);
		}
		logger.debug("Creating watch2 instance");
		if (findByOriginMemberAndTargetMember(m2, m1) == null) {
			Watch watch2 = new Watch();
			watch2.setOriginMember(m2);
			watch2.setTargetMember(m1);
			watch2.setWatchConfigurations(getDefaultConfiguration(watch2));
			watch2.setStatus(Constants.WATCH_STATUS_IN_ACTIVE);
			logger.debug("Saving 'watch2'");
			watchService.getWatchRepository().save(watch2);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWatch(Member m1, Member m2) {
		logger.info("Received request to create bidirectional 'watch'es");
		if (m1 == null || m2 == null) {
			return;
		}

		logger.debug("Creating watch1 instance");
		if (findByOriginMemberAndTargetMember(m1, m2) == null) {
			Watch watch1 = new Watch();
			watch1.setOriginMember(m1);
			watch1.setTargetMember(m2);
			// watch1.setWatchConfigurations(getDefaultConfiguration(watch1));
			/*
			 * WatchStatus ws =
			 * watchStatusService.findByStatus(Constants.WATCH_STATUS_IN_ACTIVE)
			 * ; if (ws == null) { ws = new WatchStatus();
			 * ws.setStatus(Constants.WATCH_STATUS_IN_ACTIVE);
			 * ws.setDescription("Watch has been created"); ws =
			 * watchStatusService.save(ws); }
			 */
			watch1.setStatus(Constants.WATCH_STATUS_IN_ACTIVE);
			logger.debug("Saving 'watch1'");
			watchService.getWatchRepository().save(watch1);
		}
	}

	private List<WatchConfiguration> getDefaultConfiguration(Watch watch) {
		logger.info("Creating default WatchConfiguration ");
		List<WatchConfiguration> configs = new ArrayList<>();
		WatchConfiguration config = new WatchConfiguration();
		config.setEntry(Constants.REFRESH_INTERVAL);
		config.setValue("10");
		config.setEntryType("int");
		config.setWatch(watch);
		WatchConfiguration config2 = new WatchConfiguration();
		config2.setEntry(Constants.SAFE_DISTANCE);
		config2.setValue("5");
		config2.setEntryType("double");
		config2.setWatch(watch);
		configs.add(config2);
		configs.add(config);
		return configs;
	}

	@Transactional
	public void updateTargetAccess(Member originMember, Member targetMember, boolean flag) {
		logger.info("Received request to update 'Target Accepted' flag");
		if (originMember == null || targetMember == null)
			return;
		Watch watch = findByOriginMemberAndTargetMember(originMember, targetMember);

		if (watch == null) {
			logger.debug("No watch found for this origin and target members");
			return;
		}
		watch.setTargetAccepted(flag);
		logger.debug("Successfully updated 'Target Access' flag");
	}

	public List<Member> findOriginMembersByTargetMember(@Param("targetMember") Member targetMember) {
		if (targetMember == null)
			return null;
		logger.debug("Received request to get all Watching members by target member");
		return watchRepository.findOriginMembersByTargetMember(targetMember);
	}

	public List<WatcherDataObject> findWatchersByTargetMemberEmail(String targetMemberEmail) {
		logger.debug("Received request to get all Watching members by target member email : " + targetMemberEmail);
		if (targetMemberEmail == null)
			return null;
		return watchRepository.findWatchersByTargetMemberEmail(targetMemberEmail);
	}

	public List<WatchDataObject> findWatchesByOriginMemberEmail(String originMemberEmail) {
		logger.debug("Received request to get all Watching members of origin member email : " + originMemberEmail);
		if (originMemberEmail == null)
			return null;
		return watchRepository.findWatchesByOriginMemberEmail(originMemberEmail);
	}

	public List<Member> findOriginMembersByTargetMemberWithActiveWatchStatus(
			@Param("targetMember") Member targetMember) {
		if (targetMember == null)
			return null;
		logger.debug("Received request to get all Watching members by target member");
		return watchRepository.findOriginMembersByTargetMemberAndStatusNotEquals(targetMember,
				Constants.WATCH_STATUS_IN_ACTIVE);
	}

	@Transactional
	public void updateWatchInstance(Member member) {
		if (member == null)
			return;
		List<Watch> watchList = watchService.findByTargetMember(member);
		logger.debug("Received list of watches for member " + member.getEmail());
		updateWatchInstance(watchList);
	}

	@Transactional
	public void updateWatchInstance(List<Watch> watchList) {
		logger.debug("Updating watchInstances for watchList");
		for (Watch watch : watchList) {
			if (watch.getWatchInstance() == null)
				continue;
			logger.debug("Updating watchInstance for watch : " + watch.getId());

			double lat1 = watch.getOriginMember().getLatitude();
			double lat2 = watch.getTargetMember().getLatitude();
			double lon1 = watch.getOriginMember().getLongitude();
			double lon2 = watch.getTargetMember().getLongitude();
			double el1 = watch.getOriginMember().getAltitude();
			double el2 = watch.getTargetMember().getAltitude();
			double distanceApart = calculateDistance(lat1, lat2, lon1, lon2, el1, el2);
			double altitudeApart = Math.abs(el2 - el1);
			watch.getWatchInstance().setDistanceApart(distanceApart);
			watch.getWatchInstance().setAltitudeApart(altitudeApart);

			// TODO status change and fcm sender and config changes
		}
	}

	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 *
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * 
	 * @returns Distance in Meters
	 */
	public double calculateDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);

		// return distance;
	}

	@Transactional
	public void updateWatchInstance(Watch watch, String status) throws SyenaException {
		logger.debug("Received request to create or update 'Watch Instance'");
		if (watch == null) {
			logger.debug("Received watch reference is null");
			return;
		}
		Watch w = watchRepository.findOne(watch.getId());
		if (w.getOriginMember() == null || w.getTargetMember() == null) {
			logger.debug("For the given watch, either the 'Origin Member' or 'Target Member' is null. Returning");
			return;
		}
		if (!w.isTargetAccepted()) {
			logger.debug("Target has not yet given access to the watcher(origin). ");
			throw new NoWatchAccessException();// "Target has not yet given
												// access to the watcher");
		}
		if (w.getWatchInstance() == null) {
			logger.debug("Creating a new WatchInstance");
			WatchInstance wi = new WatchInstance();
			wi.setWatch(w);
			w.setWatchInstance(wi);
			// watchRepository.save(w);
		}
		w.setStatus(status);
		List<Watch> watchList = new ArrayList<>();
		watchList.add(w);
		updateWatchInstance(watchList);
	}

}
