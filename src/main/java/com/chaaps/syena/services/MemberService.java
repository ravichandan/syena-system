package com.chaaps.syena.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chaaps.syena.entities.Member;
import com.chaaps.syena.entities.MemberImage;
import com.chaaps.syena.entities.MemberRegistration;
import com.chaaps.syena.entities.virtual.MemberIdEmailInstallationIdDataObject;
import com.chaaps.syena.repositories.MemberImageRepository;
import com.chaaps.syena.repositories.MemberRepository;

@Service
public class MemberService {
	private Logger logger = Logger.getLogger(MemberService.class);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberImageRepository memberImageRepository;
	
	@PersistenceContext
	EntityManager entityManager;

	/**
	 * @return the memberRepository
	 */
	public MemberRepository getMemberRepository() {
		return memberRepository;
	}

	/**
	 * @param memberRepository
	 *            the memberRepository to set
	 */
	public void setMemberRepository(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public Member save(Member member) {
		logger.debug("In MemberService, saving member : " + member);
		if (member == null)
			return null;
		return memberRepository.save(member);
	}

	public Member findByEmail(String email) {
		logger.debug("In MemberService. Querying for Member by email : " + email);
		if (StringUtils.isBlank(email))
			return null;
		return this.memberRepository.findByEmail(email);
	}

	public Long findIdByEmail(String email) {
		logger.debug("In MemberService. Querying for Member by email : " + email);
		if (StringUtils.isBlank(email))
			return null;
		return this.memberRepository.findIdByEmail(email);
	}

	public Long findIdByEmailAndInstallationId(String email, String installationId) {
		logger.debug("In MemberService. Querying for Member by email : " + email);
		if (StringUtils.isBlank(email))
			return null;
		return this.memberRepository.findIdByEmailAndInstallationId(email, installationId);
	}

	public MemberIdEmailInstallationIdDataObject findEmailInstIdByEmail(String email) {
		logger.debug("In MemberService. Querying for MemberIdEmailInstallationIdDataObject by email : " + email);
		if (StringUtils.isBlank(email))
			return null;
		return this.memberRepository.findEmailInstIdByEmail(email);
	}

	public Long countActiveMembersByEmailAndInstallationId(String email, String installationId) {
		logger.debug("In MemberService. Counting for all 'Member's by email & installationId: " + email);
		if (StringUtils.isBlank(email))
			return null;
		return this.memberRepository.countByEmailAndInstallationIdAndActive(email, installationId, true);
	}

	@Transactional
	public void updateLocation(Member member, double latitude, double longitude, double altitude) {
		logger.debug("In MemberService. Received request to update location ");
		if (member == null)
			return;
		Member m = memberRepository.findOne(member.getId());
		if (m == null)
			return;
		logger.debug("Updating location for member : " + m.getEmail());
		m.setLatitude(latitude);
		m.setLongitude(longitude);
		m.setAltitude(altitude);

		logger.debug("Member location is updated");
	}

	@Transactional
	public void activateMember(String email, String installationId) {
		logger.debug("Received request to activate member " + email);
		Member m = memberRepository.findByEmail(email);
		m.setActive(true);
		m.setInstallationId(installationId);
		logger.debug("Member is activated");
	}

	@Transactional
	public void updateRegistration(String email, String regToken) {
		logger.debug("Received request to update registration for member " + email);
		Member m = memberRepository.findByEmail(email);
		MemberRegistration mr = null;
		if ((mr = m.getMemberRegistration()) == null) {
			logger.debug("No registration entry found for the given member. Creating a new one");
			mr = new MemberRegistration();
			mr.setMember(m);
		}
		mr.setRegistrationToken(regToken);
		m.setMemberRegistration(mr);
		logger.debug("Registration token is successfully updated");
	}

	@Transactional
	public void saveMemberImage(String email, String image) {
		logger.debug("Received request to save member image for email: " + email);
		Member m = memberRepository.findByEmail(email);
		MemberImage mi = new MemberImage();
		mi.setImage(image.getBytes());
		mi.setProfilePic(true);
		entityManager.persist(mi);
		mi = memberImageRepository.save(mi);
		entityManager.flush();
		
		logger.debug("Saved successfully, MemberImage id: " + mi.getId());
		mi = memberImageRepository.findOne(mi.getId());
		logger.debug("Saved data " + mi.getCreatedDate());
		m.setMemberImage(mi);
		// memberRepository.save(m);
	}
}
