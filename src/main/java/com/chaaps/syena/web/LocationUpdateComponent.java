package com.chaaps.syena.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.Watch;
import com.chaaps.syena.entities.WatchConfiguration;
import com.chaaps.syena.services.MemberService;
import com.chaaps.syena.services.WatchService;
import com.chaaps.syena.util.Constants;
import com.chaaps.syena.web.request.LocationUpdateRequest;

@Component
public class LocationUpdateComponent {
	Logger logger = Logger.getLogger(LocationUpdateComponent.class);

	@Autowired
	MemberService memberService;

	@Autowired
	WatchService watchService;

	@Autowired
	FcmSender fcmSender;

	@JmsListener(destination = "location_update_queue", concurrency = "1")
	public void receiveMessage(LocationUpdateRequest locationUpdateRequest) {
		logger.info("JmsMessage received to update member's location : " + locationUpdateRequest);

		Member member = memberService.findByEmail(locationUpdateRequest.getEmail());
		if (member == null) {
			return;
		}
		memberService.updateLocation(member, locationUpdateRequest.getLatitude(), locationUpdateRequest.getLongitude(),
				locationUpdateRequest.getAltitude());
		logger.debug("Successfully updated location for " + member.getEmail());
		logger.debug("Updating watch instance now");
		watchService.updateWatchInstance(member);
		List<Watch> watches = watchService.findByTargetMemberWithActiveWatch(member);
		for (Watch w : watches) {
			logger.debug("Watch Id : " + w.getId());
			if (w.getOriginMember() == null || w.getOriginMember().getMemberRegistration() == null
					|| w.getOriginMember().getMemberRegistration().getRegistrationToken() == null) {
				continue;
			}
			if (w.getWatchInstance() != null && w.getWatchConfigurations() != null
					&& w.getWatchConfigurations().size() > 0) {

				for (WatchConfiguration wc : w.getWatchConfigurations()) {
					logger.debug("Watch Configuration Id: " + wc.getId());
					try {
						if (wc.getEntry().equals(Constants.SAFE_DISTANCE)) {
							double safeDistance = Double.valueOf(wc.getValue());
							logger.debug("Safe Distance : " + safeDistance);
							if (safeDistance < w.getWatchInstance().getDistanceApart()) {
								fcmSender.send(w.getOriginMember().getMemberRegistration().getRegistrationToken(),
										w.getOriginMember().getEmail(), w.getStatus(), safeDistance);
							}
						}
					} catch (Exception e) {
						logger.error(getStackTrace(e));
						e.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * Creates and returns a {@link java.lang.String} from t’s stacktrace
	 * 
	 * @param t
	 *            Throwable whose stack trace is required
	 * @return String representing the stack trace of the exception
	 */
	public String getStackTrace(Throwable t) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();
		printWritter.close();
		String trace = stringWritter.toString();
		try {
			stringWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trace;
	}
}
