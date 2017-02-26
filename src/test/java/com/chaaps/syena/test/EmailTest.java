package com.chaaps.syena.test;

import java.io.IOException;

import com.chaaps.syena.web.request.PinValidationRequest;
import com.chaaps.syena.web.response.GetWatchesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailTest {
	public static void main(String[] args) throws IOException {

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
//		String str = mapper.writeValueAsString(req);
//		System.out.println(str);
		String c= "{\"email\": \"CFDHS\",\"watchMembers\": [{\"email\": \"s.ravichandan@gmail.com\",\"name\": \"nulls\",\"enabled\": false}]}";
		GetWatchesResponse gw=mapper.readValue(c, GetWatchesResponse.class);

	}

}
