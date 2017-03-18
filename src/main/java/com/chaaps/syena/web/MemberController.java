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
import com.chaaps.syena.entities.virtual.WatchDataObject;
import com.chaaps.syena.entities.virtual.WatcherDataObject;
import com.chaaps.syena.exceptions.InvalidEmailException;
import com.chaaps.syena.exceptions.InvalidInstallationIdException;
import com.chaaps.syena.exceptions.InvalidMemberException;
import com.chaaps.syena.exceptions.InvalidWatchException;
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
import com.chaaps.syena.web.response.GetWatchesResponse;
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
	public String getStackTrace(Throwable t) {// TODO remove this
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
			@QueryParam(Constants.QP_REQUESTER) String requester) {

		logger.debug("In getOrCreateMember(), received request to '/get-or-create', requester: " + requester);
		EmailVerifyResponse response = new EmailVerifyResponse();
		try {
			{
				StringBuilder emailValidRes = ValidationUtils.validateEmail(requester);
				if (emailValidRes != null) {
					logger.debug("Email validation failed. Returning error response");
					response.setError(new EmailVerifyResponse.Error());
					response.getError().setErrorMessage(emailValidRes.toString());
					response.setStatus(EmailVerifyResponse.INVALID_EMAIL);
					response.setEmail(requester);
					return response;
				}
			}
			Member member = memberService.findByEmail(requester);
			response.setEmail(requester);

			if (StringUtils.isEmpty(installationId)) {// user installed the app
														// for the first time
				logger.debug("Installation-Id is empty. User probably installed the app for the first time");
				installationId = RandomGenerator.generateInstanceId();
			}
			response.setInstallationId(installationId);
			if (member == null) {
				logger.debug("Member entry not found for given email. Creating a new one..");
				response.setStatus(EmailVerifyResponse.NEW_ENTRY);
				response.setEmailSent(memberTransactionService.generatePinAndSendEmail(requester,
						installationId) == EmailVerifyResponse.SUCCESS);
				return response;
			}
			member = memberService.findByEmail(requester);
			if (member.getInstallationId().equals(installationId)) {
				logger.debug("'Installation-Id' matches. Returning success response");
				response.setStatus(EmailVerifyResponse.SUCCESS);
			} else {
				logger.debug("'Installation-Id' matches. Probably user is logging into a new device");

				response.setStatus(EmailVerifyResponse.NEW_DEVICE);
				response.setEmailSent(memberTransactionService.generatePinAndSendEmail(requester,
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
			@QueryParam(Constants.QP_REQUESTER) String requester, PinValidationRequest pinValidationRequest) {
		logger.debug("In addMember(), received request to '/add', requester: " + requester);
		PinValidationResponse response = new PinValidationResponse();
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			response.setStatus(PinValidationResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		{
			StringBuilder validationResult = ValidationUtils.validatePin(pinValidationRequest.getPin());
			if (validationResult != null) {
				logger.debug("PIN validation failed. Returning error response");
				response.setStatus(PinValidationResponse.INVALID_PIN);
				return response;
			}
		}
		logger.debug("Primary validations are successful");
		try {
			int result = memberTransactionService.validatePin(requester, installationId, pinValidationRequest.getPin());
			if (result == PinValidationResponse.SUCCESS) {
				memberService.activateMember(requester, installationId);
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
			@QueryParam(Constants.QP_REQUESTER) String requester) {
		logger.debug("In generateTagCode(), received request to '/tag-code', requester: " + requester);

		TagCodeGenerationResponse response = new TagCodeGenerationResponse();
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is invalid, returning error response");
			response.setStatus(TagCodeGenerationResponse.INVALID_INSTALLATION_ID);
			return response;
		}
		if (ValidationUtils.validateEmail(requester) != null) {
			logger.error("Invalid Email");
			response.setStatus(TagCodeGenerationResponse.INVALID_EMAIL);
			return response;

		}
		logger.debug("Primary validations are successful");
		Member member = memberService.findByEmail(requester);
		if (member == null) {
			logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
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
		logger.debug("TagCode has been generated for : " + requester);
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
			@QueryParam(Constants.QP_REQUESTER) String requester, TagByCodeRequest tagByCodeRequest) {// TODO
																										// send
																										// diplayName
																										// with
																										// email
																										// in
																										// response
		logger.info("Received request in tagMemberByCode() to tag, requester: " + requester);
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
		if (ValidationUtils.validateEmail(requester) != null) {
			logger.debug("Email is invalid. Returning...");
			response.setStatus(TagByCodeResponse.INVALID_EMAIL);
			return response;
		}
		response.setEmail(requester);
		try {
			Member m1 = memberService.findByEmail(requester);
			if (m1 == null) {
				logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
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
			@QueryParam(Constants.QP_REQUESTER) String requester) {// TODO
																	// verify
																	// again
		logger.info("Request received to '/tag-code', requester: " + requester);
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(requester) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity(requester + " is invalid").build();
		}
		try {
			Member m1 = memberService.findByEmail(requester);
			if (m1 == null) {
				logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
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

		logger.info("Request received to '/location'");
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (ValidationUtils.validateEmail(locationUpdateRequest.getRequester()) != null) {
			logger.debug("Email is invalid. Returning...");
			return Response.notModified().entity("Email is invalid.").build();
		}
		try {
			logger.debug("Primary validation successful. Requester: " + locationUpdateRequest.getRequester());
			Member member = memberService.findByEmail(locationUpdateRequest.getRequester());
			if (member == null || !member.isActive() || !member.getInstallationId().equals(installationId)) {
				logger.debug("Valid 'Member' entry not found with email : " + locationUpdateRequest.getRequester()
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
			@QueryParam(Constants.QP_TARGET) String targetEmail,
			@QueryParam(Constants.QP_REQUESTER) String requesterEmail) {

		logger.info("Request received to '/start-watch', requester: " + requesterEmail);
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			throw new InvalidInstallationIdException();
		}
		if (ValidationUtils.validateEmail(requesterEmail) != null
				|| ValidationUtils.validateEmail(targetEmail) != null) {
			logger.debug("Email is invalid. Returning...");
			throw new InvalidEmailException(requesterEmail);
		}
		Member originMember = memberService.findByEmail(requesterEmail);
		if (originMember == null || !originMember.isActive()
				|| !originMember.getInstallationId().equals(installationId)) {
			logger.debug("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...");
			throw new InvalidMemberException(requesterEmail);

		}
		Member targetMember = memberService.findByEmail(targetEmail);
		if (targetMember == null || !targetMember.isActive()) {
			logger.debug("Valid 'Member' entry not found with email : " + targetEmail);
			throw new InvalidMemberException(targetEmail);

		}
		Watch watch = watchService.findByOriginMemberAndTargetMember(originMember, targetMember);
		if (watch == null) {
			logger.debug("Requester has not yet tagged the target member " + targetEmail);
			throw new InvalidWatchException();
		}
		watchService.updateWatchInstance(watch, Constants.WATCH_STATUS_STARTED);
		logger.debug("Watch is started successfully");
		return Response.ok().build();
	}

	@POST
	@Path("/stop-watch")
	public Response stopWatch(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_TARGET) String targetEmail,
			@QueryParam(Constants.QP_REQUESTER) String requesterEmail) {

		logger.info("Request received to '/stop-watch', requester: " + requesterEmail);
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			throw new InvalidInstallationIdException();
		}
		if (ValidationUtils.validateEmail(requesterEmail) != null
				|| ValidationUtils.validateEmail(targetEmail) != null) {
			logger.debug("Email is invalid. Returning...");
			throw new InvalidEmailException(requesterEmail);
		}
		Member originMember = memberService.findByEmail(requesterEmail);
		if (originMember == null || !originMember.isActive()
				|| !originMember.getInstallationId().equals(installationId)) {
			logger.debug("Valid 'Member' entry not found with email : " + requesterEmail + ", returning...");
			throw new InvalidMemberException(requesterEmail);
		}
		Member targetMember = memberService.findByEmail(targetEmail);
		if (targetMember == null || !targetMember.isActive()) {
			logger.debug("Valid 'Member' entry not found with email : " + targetEmail);
			throw new InvalidMemberException(targetEmail);
		}
		Watch watch = watchService.findByOriginMemberAndTargetMember(originMember, targetMember);
		if (watch == null) {
			logger.debug("Requester has not yet tagged the target member " + targetEmail);
			throw new InvalidWatchException();
		}
		watchService.updateWatchInstance(watch, Constants.WATCH_STATUS_IN_ACTIVE);
		logger.debug("Watch is stopped successfully");
		return Response.ok().build();
	}

	@GET
	@Path("/get-location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LocationResponse getLocation(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_TARGET) String targetEmail,
			@QueryParam(Constants.QP_REQUESTER) String requesterEmail) {
		logger.info("Request received to '/get-location', requester: " + requesterEmail);
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
			logger.debug("Primary validations are successful.");
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
			@QueryParam(Constants.QP_REQUESTER) String requester, WatchAccessRequest watchAccessRequest) {

		logger.info("Request received to '/watch-access', requester: " + requester);

		if (StringUtils.isBlank(installationId) || watchAccessRequest == null) {
			logger.debug("Installation-Id is empty, returning error response");
			throw new InvalidInstallationIdException();
		}
		{
			StringBuilder res1 = ValidationUtils.validateEmail(requester);
			if (res1 != null) {
				logger.debug("Email is invalid. Returning...");
				throw new InvalidEmailException(res1 + " " + requester);
			}
			res1 = ValidationUtils.validateEmail(watchAccessRequest.getTarget());
			if (res1 != null) {
				logger.debug("Email is invalid. Returning...");
				throw new InvalidEmailException(res1 + " " + watchAccessRequest.getTarget());
			}
		}
		Member requesterMember = memberService.findByEmail(requester);
		if (requesterMember == null || !requesterMember.isActive()
				|| !requesterMember.getInstallationId().equals(installationId)) {
			logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
			throw new InvalidMemberException(requester);
		}
		Member targetMember = memberService.findByEmail(watchAccessRequest.getTarget());
		if (targetMember == null || !targetMember.isActive()) {
			logger.debug(
					"Valid 'Member' entry not found with email : " + watchAccessRequest.getTarget() + ", returning...");
			throw new InvalidMemberException(watchAccessRequest.getTarget());
		}

		watchService.updateTargetAccess(targetMember, requesterMember, watchAccessRequest.isFlag());
		logger.debug("Successfully updated 'Watch Access' ");

		return Response.ok().build();
	}

	@GET
	@Path("/get-watchers")
	@Produces(MediaType.APPLICATION_JSON)
	public GetWatchersResponse getWatchers(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_REQUESTER) String requester) {
		logger.info("Received request to '/get-watchers', requester: " + requester);
		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			throw new InvalidInstallationIdException();
		}
		{
			StringBuilder validRes1 = ValidationUtils.validateEmail(requester);
			if (validRes1 != null) {
				logger.debug("Email is empty, returning error response");
				throw new InvalidEmailException(validRes1 + " " + requester);
			}
		}
		Long memberCount = memberService.countActiveMembersByEmailAndInstallationId(requester, installationId);
		if (memberCount == null || memberCount <= 0) {
			logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
			throw new InvalidMemberException(requester);
		}

		GetWatchersResponse response = new GetWatchersResponse();
		logger.debug("Querying for origin member email and acceptance");
		List<WatcherDataObject> watchers = watchService.findWatchersByTargetMemberEmail(requester);
		for (WatcherDataObject m : watchers) {
			logger.debug("Adding entry for : " + m.getOriginMemberEmail());
			response.addEntry(m.getOriginMemberEmail(), m.getWatchName(), m.getTargetAccepted());
		}

		logger.debug("Returning response for getWatchers()");
		return response;

	}

	@POST
	@Path("/update-signature")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSignature(@HeaderParam(Constants.INSTALLATION_ID) String installationId, String tokenData) {

		logger.info("Request received to '/update-registration' token signature");

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			return Response.notModified().build();
		}
		if (StringUtils.isBlank(tokenData)) {
			logger.debug("Token Data is empty, returning error response");
			return Response.notModified().build();
		}

		JsonParser parser = new JsonParser();
		logger.debug("Parsing tokenData");
		try {
			JsonObject jo = parser.parse(tokenData).getAsJsonObject();
			String regToken = jo.get(Constants.REGISTRATION_TOKEN).getAsString();
			String email = jo.get(Constants.QP_REQUESTER).getAsString();
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

	@GET
	@Path("/get-watches")
	@Produces(MediaType.APPLICATION_JSON)
	public GetWatchesResponse getWatches(@HeaderParam(Constants.INSTALLATION_ID) String installationId,
			@QueryParam(Constants.QP_REQUESTER) String requester) {
		logger.info("Request received to '/get-watches', requester: " + requester);

		if (StringUtils.isBlank(installationId)) {
			logger.debug("Installation-Id is empty, returning error response");
			throw new InvalidInstallationIdException();
		}
		{
			StringBuilder validRes1 = ValidationUtils.validateEmail(requester);
			if (validRes1 != null) {
				logger.debug("Email is empty, returning error response");
				throw new InvalidEmailException(validRes1 + " " + requester);
			}
		}
		Long memberCount = memberService.countActiveMembersByEmailAndInstallationId(requester, installationId);
		if (memberCount == null || memberCount <= 0) {
			logger.debug("Valid 'Member' entry not found with email : " + requester + ", returning...");
			throw new InvalidMemberException(requester);
		}

		GetWatchesResponse response = new GetWatchesResponse();
		logger.debug("Querying for origin member email and acceptance");
		List<WatchDataObject> watches = watchService.findWatchesByOriginMemberEmail(requester);
		for (WatchDataObject m : watches) {
			logger.debug("Adding entry for : " + m.getTargetMemberEmail());
			response.addEntry(m.getTargetMemberEmail(), m.getWatchName(), m.getTargetAccepted(),
					!(m.getWatchStatus().equals(Constants.WATCH_STATUS_IN_ACTIVE)));
		}

		logger.debug("Returning response for getWatchers()");
		return response;
	}
}
