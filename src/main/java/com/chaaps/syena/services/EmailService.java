package com.chaaps.syena.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private Logger logger = Logger.getLogger(EmailService.class);

	@Autowired
	@Qualifier("mailSender")
	private MailSender mailSender;

	@Autowired
	@Qualifier("pinTemplateMessage")
	private SimpleMailMessage pinTemplateMessage;

	/**
	 * @return the mailSender
	 */
	public MailSender getMailSender() {
		return mailSender;
	}

	/**
	 * @param mailSender
	 *            the mailSender to set
	 */
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public boolean sendPinAsEmail(String email, String pin) {
		logger.info("Received email request for " + email);
		boolean result = false;
		try {
			SimpleMailMessage message = new SimpleMailMessage(pinTemplateMessage);

			message.setTo(email);
			message.setText("<html><body><h3>Dear " + email
					+ ",<h3> <br/><br/> Syena has generated a temporary one time usable pin to validate your email.<br/> Your PIN is <h2>"
					+ pin + "<h2> . <br/><br/> Note : You can use this only once</body></html>");
			logger.debug("Sending message");
			mailSender.send(message);
			result = true;
			logger.debug("Message sent");
		} catch (Exception e) {
			logger.error("Sending Message failed. Reason : " + e.getCause() + " " + e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
