package com.chaaps.syena.test;

import com.chaaps.syena.web.request.PinValidationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailTest {
	public static void main(String[] args) throws JsonProcessingException {

		/*EmailVerifyRequest req= new EmailVerifyRequest();
		req.setEmail("chandan@gmail.com");
		req.setInstanceId("sdfdfssd");
		ObjectMapper mapper= new ObjectMapper();
		String str=mapper.writeValueAsString(req);
		System.out.println(str);
		*/

		PinValidationRequest req = new PinValidationRequest();
		//req.setRequester("chan@g.com");
//		req.setInstallationId("sfd");
		req.setPin("3424");
		ObjectMapper mapper = new ObjectMapper();
		String str = mapper.writeValueAsString(req);
		System.out.println(str);

	}

}
