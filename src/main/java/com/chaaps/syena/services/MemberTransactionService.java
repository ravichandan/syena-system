package com.chaaps.syena.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.MemberTransaction;
import com.chaaps.syena.entities.virtual.MemberIdEmailInstallationIdDataObject;
import com.chaaps.syena.exceptions.InvalidMemberException;
import com.chaaps.syena.repositories.MemberTransactionRepository;
import com.chaaps.syena.util.RandomGenerator;
import com.chaaps.syena.web.response.EmailVerifyResponse;
import com.chaaps.syena.web.response.PinValidationResponse;

@Service
public class MemberTransactionService {
	private Logger logger = Logger.getLogger(MemberTransactionService.class);
	@Autowired
	private MemberTransactionRepository memberTransactionRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private MemberService memberService;

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * @return the memberTransactionRepository
	 */
	public MemberTransactionRepository getMemberTransactionRepository() {
		return memberTransactionRepository;
	}

	/**
	 * @param memberTransactionRepository
	 *            the memberTransactionRepository to set
	 */
	public void setMemberTransactionRepository(MemberTransactionRepository memberTransactionRepository) {
		this.memberTransactionRepository = memberTransactionRepository;
	}

	public MemberTransaction findByMember(Member member) {
		logger.debug("Querying for MemberTransaction by TagCode");
		if (member == null)
			return null;
		return memberTransactionRepository.findByMember(member);
	}

	public MemberTransaction findByTagCode(String tagCode) {
		logger.debug("Querying for MemberTransaction by TagCode");
		if (StringUtils.isBlank(tagCode))
			return null;
		return memberTransactionRepository.findByTagCode(tagCode);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void removeTagCodeForMember(Member member) {
		MemberTransaction mt = memberTransactionRepository.findByMember(member);
		this.removeTagCode(mt.getId());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void removeTagCode(Long id) {
		logger.debug("Removing temporary tagCode and flushing for id : " + id);
		MemberTransaction memberTxn = memberTransactionRepository.findOne(id);
		memberTxn.setTagCode(null);
		memberTransactionRepository.flush();
	}

	@Transactional
	public int generatePinAndSendEmail(String email, String installationId) {
		logger.info("Received pin generation request for email : " + email);
		if (StringUtils.isEmpty(email)) {
			logger.error("'Email' should not be empty");
			return EmailVerifyResponse.INVALID_EMAIL;
		}
		Member member = memberService.findByEmail(email);
		if (member == null) {
			logger.debug("Valid 'Member' entry not found with email : " + email);
			member = new Member();
			member.setEmail(email);
			member.setInstallationId(installationId);
			memberService.save(member);
		}
		String pin = RandomGenerator.generate4DigitPin();
		logger.debug("Pin has been generated for : " + email);
		MemberTransaction mt = memberTransactionRepository.findByMemberAndTxnInstallationId(member, installationId);
		if (mt != null) {
			logger.debug("Member entry found in table MemberTxn. Resetting PIN ...");
			mt.setPin(pin);
			memberTransactionRepository.flush();
		} else {
			logger.debug("Member entry not found in table MemberTxn. Creating a new one ...");
			mt = new MemberTransaction();
			mt.setMember(member);
			mt.setPin(pin);
			mt.setTxnInstallationId(installationId);
			// member.setMemberTransaction(mt);
			memberTransactionRepository.save(mt);
		}
		logger.info("Sending request to send PIN as an email");
		boolean emailSent = emailService.sendPinAsEmail(email, pin);
		return emailSent ? EmailVerifyResponse.SUCCESS : EmailVerifyResponse.SENDING_EMAIL_FAILED;
	}

	@Transactional
	public int validatePin(String email, String installationId, String pin) {
		logger.info("Received request to validate pin for email : " + email);
		if (StringUtils.isEmpty(email)) {
			logger.error("'Email' should not be empty");
			return PinValidationResponse.INVALID_EMAIL;
		}
		if (StringUtils.isEmpty(installationId)) {
			logger.error("'Installation-Id' should not be empty");
			return PinValidationResponse.INVALID_INSTALLATION_ID;
		}
		if (StringUtils.isEmpty(pin)) {
			logger.error("'PIN' should not be empty");
			return PinValidationResponse.INVALID_PIN;
		}
		MemberIdEmailInstallationIdDataObject memberDO = memberService.findEmailInstIdByEmail(email);

		if (memberDO == null || memberDO.getId() == null || memberDO.getId() == 0) {
			logger.debug("Valid 'Member' entry not found with email : " + email + ", returning...");
			throw new InvalidMemberException(email);
		}
		if (memberDO.getInstallationId().equals(installationId)) {
			logger.debug("Member is already registered.");
			return PinValidationResponse.SUCCESS;
		}
		MemberTransaction mt = memberTransactionRepository.findByMemberIdAndTxnInstallationId(memberDO.getId(),
				installationId);
		if (mt == null || StringUtils.isEmpty(mt.getPin()) || (!mt.getPin().equals(pin))) {
			logger.debug("No PIN for this email found in Syena. Returning...");
			return PinValidationResponse.NO_VALID_MEMBER_TXN;
		}
		logger.debug("Given pin is valid. Removing the temporary pin & installation-id");
		memberTransactionRepository.deleteByMemberId(memberDO.getId());
		/*
		 * mt.setPin(null); member.setInstallationId(installationId);
		 * mt.setTxnInstallationId(null);
		 */
		logger.debug("Sending success response");
		return PinValidationResponse.SUCCESS;
	}

	@Transactional
	public void saveTagCode(Member member, String tagCode) {

		if (member == null) {
			logger.debug("Valid 'Member' entry not found" + ", returning...");
			return;
		}
		logger.info("Received Tag Code generation request for member : " + member.getId());
		try {
			MemberTransaction mt = memberTransactionRepository.findByMember(member);
			if (mt != null) {
				logger.debug("Member entry found in table MemberTxn. Resetting TagCode ...");
				mt.setTagCode(tagCode);
				memberTransactionRepository.flush();
			} else {
				logger.debug("Member entry not found in table MemberTxn. Creating a new one ...");
				mt = new MemberTransaction();
				mt.setMember(member);
				mt.setTagCode(tagCode);
				memberTransactionRepository.saveAndFlush(mt);
			}
		} catch (Exception e) {
			logger.error("Exception Occured : " + e.getMessage());
			throw e;
		}
	}

}
