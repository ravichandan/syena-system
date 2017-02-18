package com.chaaps.syena.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.MemberTransaction;
import com.chaaps.syena.entities.Watch;
import com.chaaps.syena.exceptions.SyenaException;
import com.chaaps.syena.services.MemberService;
import com.chaaps.syena.services.MemberTransactionService;
import com.chaaps.syena.services.WatchService;
import com.chaaps.syena.util.Constants;
import com.chaaps.syena.util.RandomGenerator;
import com.chaaps.syena.util.ValidationUtils;
import com.chaaps.syena.web.request.LocationUpdateRequest;
import com.chaaps.syena.web.request.PinValidationRequest;
import com.chaaps.syena.web.request.TagByCodeRequest;
import com.chaaps.syena.web.request.WatchAccessRequest;
import com.chaaps.syena.web.response.EmailVerifyResponse;
import com.chaaps.syena.web.response.GetWatchersResponse;
import com.chaaps.syena.web.response.LocationResponse;
import com.chaaps.syena.web.response.PinValidationResponse;
import com.chaaps.syena.web.response.TagByCodeResponse;
import com.chaaps.syena.web.response.TagCodeGenerationResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
@Path("/member")
public class MemberController {

	Logger logger = Logger.getLogger(MemberController.class);

	@Autowired
	MemberService memberService;

	@Autowired
	MemberTransactionService memberTransactionService;

	@Autowired
	WatchService watchService;

	@Autowired
	JmsTemplate jmsTemplate;

	@GET
	@Path("test")
	public String tes() {
		System.out.println("Test");
		return "test";
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

	/**
	 * Its basically a 'get or create' method. receives a EmailVerifyRequest as
	 * body with 'Instance-Id' as a header. It then creates email in db. If the
	 * email already exists, it then verifies the stored 'Instance-id' to
	 * identify whether its a new device or not. Returns either of the following
	 * values as per the result
	 * 
	 * SUCCESS = 1; NEW_ENTRY = 2; NEW_DEVICE = 3;
	 * 
	 * @param instanceId
	 * @param emailVerifyRequest
	 * @return
	 */
	@GET
	@Path("/get-or-create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public EmailVerifyResponse getOrCreateMember(@HeaderParam(value = Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.EMAIL) String email) {

		logger.debug("In getOrCreateMember() " + email + ", InstallationId " + installationId);
		EmailVerifyResponse response = new EmailVerifyResponse();
		try {
			String emailValidRes = ValidationUtils.validateEmail(email);
			if (!StringUtils.isBlank(emailValidRes)) {
				logger.debug("Email validation failed. Returning error response");
				response.setError(new EmailVerifyResponse.Error());
				response.getError().setErrorMessage(emailValidRes);
				response.setStatus(EmailVerifyResponse.INVALID_EMAIL);
				response.setEmail(email);
				return response;
			}
			Member member = memberService.findByEmail(email);
			response.setEmail(email);

			if (StringUtils.isEmpty(installationId)) {// user installed the app
														// for the first time
				logger.debug("Installation-Id is empty. User probably installed the app for the first time");
				installationId = RandomGenerator.generateInstanceId();
			}
			response.setInstallationId(installationId);
			if (member == null) {
				logger.debug("Member entry not found for given email. Creating a new one..");
				response.setStatus(EmailVerifyResponse.NEW_ENTRY);
				response.setEmailSent(memberTransactionService.generatePinAndSendEmail(email,
						installationId) == EmailVerifyResponse.SUCCESS);
				return response;
			}
			member = memberService.findByEmail(email);
			if (member.getInstallationId().equals(installationId)) {
				logger.debug("'Installation-Id' matches. Returning success response");
				response.setStatus(EmailVerifyResponse.SUCCESS);
			} else {
				logger.debug("'Installation-Id' matches. Probably user is logging into a new device");

				response.setStatus(EmailVerifyResponse.NEW_DEVICE);
				response.setEmailSent(memberTransactionService.generatePinAndSendEmail(email,
						installationId) == EmailVerifyResponse.SUCCESS);
			}
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			response.setInstallationId(null);
			response.setError(new EmailVerifyResponse.Error());
			response.getError().setErrorMessage(e.getMessage());
			response.setStatus(EmailVerifyResponse.ERROR);
		}
		return response;
	}

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PinValidationResponse addMember(@HeaderParam(value = Constants.INSTALLATION_ID) String installationId,
			PinValidationRequest pinValidationRequest) {
		logger.debug("In addMember() " + pinValidationRequest + ", installationId " + installationId);
		PinValidationResponse response = new PinValidationResponse();
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			response.setStatus(PinValidationResponse.INVALID_INSTALLATION_ID);
			return response;
		}

		String validationResult = ValidationUtils.validatePin(pinValidationRequest.getPin());
		if (StringUtils.isNotBlank(validationResult)) {
			logger.debug("PIN validation failed. Returning error response");
			response.setStatus(PinValidationResponse.INVALID_PIN);
			return response;
		}
		try {
			int result = memberTransactionService.validatePin(pinValidationRequest.getEmail(), installationId,
					pinValidationRequest.getPin());
			if (result == PinValidationResponse.SUCCESS) {
				memberService.activateMember(pinValidationRequest.getEmail());
			}
			response.setStatus(result);
		} catch (Exception e) {
			logger.error("PIN verification failed. Returning error response\n" + getStackTrace(e));
			e.printStackTrace();
			response.setStatus(PinValidationResponse.INVALID_PIN);
		}
		return response;
	}

	@GET
	@Path("/tag-code")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TagCodeGenerationResponse generateTagCode(
			@HeaderParam(value = Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.EMAIL) String email) {
		logger.debug("In generateTagCode() " + email + ", installationId " + installationId);

		TagCodeGenerationResponse response = new TagCodeGenerationResponse();
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is invalid, returning error response");
			response.setStatus(TagCodeGenerationResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		if (ValidationUtils.validateEmail(email) != null) {
			logger.error("Invalid Email");
			response.setStatus(TagCodeGenerationResponse.INVALID_EMAIL);
			return response;

		}
		Member member = memberService.findByEmail(email);
		if (member == null) {
			logger.debug("Valid 'Member' entry not found with email : " + email + ", returning...");
			response.setStatus(TagCodeGenerationResponse.NO_VALID_MEMBER);
			return response;

		}
		if (!member.isActive()) {
			logger.debug("Member is not active, returning error response");
			response.setStatus(TagCodeGenerationResponse.INACTIVE_MEMBER);
			return response;
		}
		if (!member.getInstallationId().equals(installationId)) {
			logger.debug("Installation-Id is invalid, returning error response");
			response.setStatus(TagCodeGenerationResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		String tagCode = RandomGenerator.generate6CharacterTagCode();
		logger.debug("TagCode has been generated for : " + email);
		try {
			memberTransactionService.saveTagCode(member, tagCode);
			response.setTagCode(tagCode);
			logger.debug("Returning success response");
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			response.setStatus(TagCodeGenerationResponse.SYSTEM_ERROR);
		}
		return response;

	}

	@POST
	@Path("/tag")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TagByCodeResponse tagMemberByCode(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			TagByCodeRequest tagByCodeRequest) {// TODO send diplayName with
												// email in response
		logger.info("Received request in tagMemberByCode() to tag, Installation-Id : " + installationId);
		TagByCodeResponse response = new TagByCodeResponse();

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			response.setStatus(TagByCodeResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		if (ValidationUtils.validateTagCode(tagByCodeRequest.getTagCode()) != null) {
			logger.debug("TagCode is invalid. Returning...");
			response.setStatus(TagByCodeResponse.INVALID_TAG_CODE);
			return response;
		}
		if (ValidationUtils.validateEmail(tagByCodeRequest.getEmail()) != null) {
			logger.debug("Email is invalid. Returning...");
			response.setStatus(TagByCodeResponse.INVALID_EMAIL);
			return response;
		}
		response.setEmail(tagByCodeRequest.getEmail());
		try {
			Member m1 = memberService.findByEmail(tagByCodeRequest.getEmail());
			if (m1 == null) {
				logger.debug("Valid 'Member' entry not found with email : " + tagByCodeRequest.getEmail()
						+ ", returning...");
				response.setStatus(TagByCodeResponse.NO_VALID_MEMBER);
				return response;

			}
			if (!m1.isActive()) {
				logger.debug("Member is not active, returning error response");
				response.setStatus(TagByCodeResponse.INACTIVE_MEMBER);
				return response;
			}
			if (!m1.getInstallationId().equals(installationId)) {
				logger.debug("Installation-Id is invalid, returning error response");
				response.setStatus(TagByCodeResponse.INVALID_INSTALLATION_ID);
				return response;
			}
			MemberTransaction mt = memberTransactionService.findByTagCode(tagByCodeRequest.getTagCode());
			if (mt == null) {
				logger.debug("TagCode is invalid. Returning...");
				response.setStatus(TagByCodeResponse.INVALID_TAG_CODE);
				return response;
			}
			if (mt.getMember() == null || !mt.getMember().isActive()) {
				logger.debug("Member is not active, returning error response");
				response.setStatus(TagByCodeResponse.INACTIVE_MEMBER);
				return response;
			}
			// watchService.saveBidirectionalWatch(m1, mt.getMember());
			watchService.saveWatch(m1, mt.getMember());
			// creating watch is successful. remove temporary tagCode.
			logger.debug("Tagging is successful. Removing temporary tagCode and flushing");
			memberTransactionService.removeTagCode(mt.getId());
			response.setStatus(TagByCodeResponse.SUCCESS);
			response.setEmail(mt.getMember().getEmail());
			logger.debug("Successfully returning from tagMemberByCode()");

		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			response.setStatus(TagByCodeResponse.SYSTEM_ERROR);
		}
		return response;
	}

	@DELETE
	@Path("/tag-code")
	public Response removeTagCode(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.EMAIL) String email) {// TODO verify again
		logger.info("Received request in removeTagCode() to remove tagCode ");
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();

		}
		if (ValidationUtils.validateEmail(email) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity(email + " is invalid").build();
		}
		try {
			Member m1 = memberService.findByEmail(email);
			if (m1 == null) {
				logger.debug("Valid 'Member' entry not found with email : " + email + ", returning...");
				return Response.notModified().entity("Valid 'Member' entry not found with given email").build();
			}
			if (!m1.isActive()) {
				logger.debug("Member is not active, returning error response");
				return Response.notModified().entity("Member is not active").build();
			}
			if (!m1.getInstallationId().equals(installationId)) {
				logger.debug("Installation-Id is invalid, returning error response");
				return Response.notModified().build();
			}
			memberTransactionService.removeTagCodeForMember(m1);
			logger.debug("Successfully returning from removeTagCode()");
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			return Response.notModified().entity(e.getMessage()).build();
		}
		return Response.ok().build();
	}

	@POST
	@Path("/location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateLocation(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			LocationUpdateRequest locationUpdateRequest) {

		logger.info("Received request in updateLocation() to update location");
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(locationUpdateRequest.getEmail()) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity("Email is invalid.").build();
		}
		try {
			Member member = memberService.findByEmail(locationUpdateRequest.getEmail());
			if (member == null || !member.isActive() || !member.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + locationUpdateRequest.getEmail()
						+ ", returning...");
				return Response.notModified().entity("Valid 'Member' entry not found with given email").build();
			}

			jmsTemplate.convertAndSend(locationUpdateRequest);
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			return Response.notModified().entity("System Error occured : " + e.getMessage()).build();
		}
		return Response.ok().build();
	}

	@POST
	@Path("/start-watch")
	@Produces(MediaType.TEXT_PLAIN)
	public Response startWatch(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_REQUESTER_EMAIL) String requesterEmail,
			@QueryParam(Constants.EMAIL) String targetEmail) {

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(requesterEmail) != null
				|| ValidationUtils.validateEmail(targetEmail) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity("Invalid Email").build();
		}
		// try {
		Member originMember = memberService.findByEmail(requesterEmail);
		if (originMember == null || !originMember.isActive()
				|| !originMember.getInstallationId().equals(installationId)) {
			logger.debug("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...");
			return Response.notModified()
					.entity("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...").build();

		}
		Member targetMember = memberService.findByEmail(targetEmail);
		if (targetMember == null || !targetMember.isActive()) {
			logger.debug("Valid 'Member' entry not found with email : " + targetEmail);
			return Response.notModified().entity("Valid 'Member' entry not found with email : " + targetEmail).build();

		}
		Watch watch = watchService.findByOriginMemberAndTargetMember(originMember, targetMember);
		if (watch == null) {
			logger.debug("Requester has not yet tagged the target member " + targetEmail);
			return Response.notModified().entity("Requester has not yet tagged the target member " + targetEmail)
					.build();
		}
		watchService.updateWatchInstance(watch, Constants.WATCH_STATUS_STARTED);
		logger.debug("Watch is created successfully");
		/*
		 * } catch (Exception e) { logger.error("Exception Occured : " +
		 * getStackTrace(e)); e.printStackTrace(); return
		 * Response.status(Response.Status.NOT_FOUND).entity("Ravi " +
		 * e.getMessage()).build(); }
		 */
		return Response.ok("CHandan").build();
	}

	@POST
	@Path("/stop-watch")
	public Response stopWatch(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_REQUESTER_EMAIL) String requesterEmail,
			@QueryParam(Constants.EMAIL) String targetEmail) {

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(requesterEmail) != null
				|| ValidationUtils.validateEmail(targetEmail) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity("Invalid Email").build();
		}
		try {
			Member originMember = memberService.findByEmail(requesterEmail);
			if (originMember == null || !originMember.isActive()
					|| !originMember.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...");
				return Response.notModified()
						.entity("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...")
						.build();

			}
			Member targetMember = memberService.findByEmail(targetEmail);
			if (targetMember == null || !targetMember.isActive()) {
				logger.debug("Valid 'Member' entry not found with email : " + targetEmail);
				return Response.notModified().entity("Valid 'Member' entry not found with email : " + targetEmail)
						.build();

			}
			Watch watch = watchService.findByOriginMemberAndTargetMember(originMember, targetMember);
			if (watch == null) {
				logger.debug("Requester has not yet tagged the target member " + targetEmail);
				return Response.notModified().entity("Requester has not yet tagged the target member " + targetEmail)
						.build();
			}
			watchService.updateWatchInstance(watch, Constants.WATCH_STATUS_IN_ACTIVE);
			logger.debug("Watch is created successfully");
		} catch (Exception e) {
			logger.error("Exception Occured : " + getStackTrace(e));
			e.printStackTrace();
			return Response.notModified().entity(e.getMessage()).build();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LocationResponse getLocation(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_REQUESTER_EMAIL) String requesterEmail,
			@QueryParam(Constants.EMAIL) String targetEmail) {
		logger.info("Request received to get location");
		LocationResponse response = new LocationResponse();
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			response.setStatus(LocationResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		if (ValidationUtils.validateEmail(requesterEmail) != null
				|| ValidationUtils.validateEmail(targetEmail) != null) {
			logger.debug("Email is invalid. Returning...");
			response.setStatus(LocationResponse.INVALID_EMAIL);
			return response;
		}
		try {
			Member originMember = memberService.findByEmail(requesterEmail);
			if (originMember == null || !originMember.isActive()
					|| !originMember.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...");
				response.setStatus(LocationResponse.NO_VALID_MEMBER);
				return response;
			}
			Member targetMember = memberService.findByEmail(targetEmail);
			if (targetMember == null || !targetMember.isActive()) {
				logger.debug("Valid 'Member' entry not found with email : " + targetEmail);
				response.setStatus(LocationResponse.NO_VALID_MEMBER);
				return response;
			}
			Watch watch = watchService.findByOriginMemberAndTargetMember(originMember, targetMember);
			if (watch == null || watch.getStatus().equals(Constants.WATCH_STATUS_IN_ACTIVE)) {
				logger.debug("Requester is not watching the target member : " + targetEmail);
				response.setStatus(LocationResponse.INVALID_WATCH);
				return response;
			}
			if (watch.getWatchInstance() == null) {
				logger.debug("No valid 'Watch Instance' is found. Something wrong must have happened.");
				response.setStatus(LocationResponse.ERROR);
				return response;
			}
			response.setAltitude(targetMember.getAltitude());
			response.setLatitude(targetMember.getLatitude());
			response.setLongitude(targetMember.getLongitude());
			response.setWatchStatus(watch.getStatus());
			response.setDistanceApart(watch.getWatchInstance().getDistanceApart());
			response.setStatus(LocationResponse.SUCCESS);
			logger.debug("Returning success response for location request.");
		} catch (Exception e) {
			logger.error("Exception Occured : " + getStackTrace(e));
			e.printStackTrace();
			response.setStatus(LocationResponse.ERROR);
		}
		return response;
	}

	@POST
	@Path("/watch-access")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWatchAccess(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			WatchAccessRequest watchAccessRequest) {

		logger.info("Request received to update registration token signature");

		if (StringUtils.isBlank(installationId) || watchAccessRequest == null) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(watchAccessRequest.getEmail()) != null
				|| ValidationUtils.validateEmail(watchAccessRequest.getRevokeeEmail()) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity("Invalid Email").build();
		}
		try {
			Member requesterMember = memberService.findByEmail(watchAccessRequest.getEmail());
			if (requesterMember == null || !requesterMember.isActive()
					|| !requesterMember.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + watchAccessRequest.getEmail()
						+ ", returning...");
				return Response.notModified().entity("Valid Member not found").build();
			}
			Member revokeeMember = memberService.findByEmail(watchAccessRequest.getRevokeeEmail());
			if (revokeeMember == null) {
				logger.debug("Valid 'Member' entry not found with email : " + watchAccessRequest.getRevokeeEmail()
						+ ", returning...");
				return Response.notModified().entity("Valid Member not found").build();
			}

			watchService.updateTargetAccess(revokeeMember, requesterMember, watchAccessRequest.isFlag());
			logger.debug("Successfully updated 'Target Access' ");
		} catch (Exception e) {
			logger.error("Exception Occured : " + getStackTrace(e));
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/get-watchers")
	@Produces(MediaType.APPLICATION_JSON)
	public GetWatchersResponse getWatchers(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.EMAIL) String email) {
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return null;
		}
		if (StringUtils.isBlank(email)) {
			logger.debug("Email is empty, returning error response");
			return null;
		}
		try {
			Member member = memberService.findByEmail(email);
			if (member == null || !member.isActive() || !member.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + email + ", returning...");
				return null;
			}
			GetWatchersResponse response = new GetWatchersResponse();
			List<Member> watchers = watchService.findOriginMembersByTargetMember(member);

			for (Member m : watchers) {
				response.addEntry(m.getEmail(), m.getDisplayName());
			}

			return response;
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			return null;
		}

	}

	@POST
	@Path("/update-signature")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSignature(@HeaderParam(Constants.INSTALLATION_ID) String installationId, String tokenData) {

		logger.info("Request received to update registration token signature");

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (StringUtils.isBlank(tokenData)) {
			logger.debug("Token Data is empty, returning error response");
			return Response.notModified().build();
		}

		JsonParser parser = new JsonParser();
		try {
			JsonObject jo = parser.parse(tokenData).getAsJsonObject();
			String regToken = jo.get(Constants.REGISTRATION_TOKEN).getAsString();
			String email = jo.get(Constants.EMAIL).getAsString();
			memberService.updateRegistration(email, regToken);
			logger.debug("Successfully updated registration for member : " + email);
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			e.printStackTrace();
			return Response.notModified().build();
		}
		logger.debug("Returning success response");
		return Response.ok().build();
	}
	// @GET
	// @Path("/get-watches")
	// @Produces(MediaType.APPLICATION_JSON)

}
