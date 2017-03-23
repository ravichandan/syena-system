package com.chaaps.syena.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chaaps.syena.repositories.MemberRepository;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Notification;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component
public class FcmSender {
	private Logger logger = Logger.getLogger(FcmSender.class);

	@Autowired
	MemberRepository memberRepository;

	// sender key is taken from firebase console registered by gmail. its
	// constant.
	Sender sender = new Sender(
			"AAAAvBvE15E:APA91bFuslAOXFBYXkGIBWSof5tc3WDe6c1SIgPACu-LmDvP5wsZ7XVwNvKRZuyjdLENYV8yIGEkj6FE8y6Tu_190WV93qBVbq9je3jvwEutzyy6hSVze2FnUP0KF--B1jqKaTk3pqf1");

	// regToken is per device.
	static String regToken = "d2JkI9R32Kk:APA91bHyLA4hPJnk-WI1ZxtTXR3HUq8IA6CwYtE9sS_KDpJeOU_voEMxykSzs30Cr12cTaCb6zsSjtHPDaPOJWC9crwg4SRsoBsoB321w5oZKvYY_xEOWW2UVbiU3MADOzQK0NDJzO1H";

	public FcmSender() {
		//startIpUpdateTask();
	}

	/**
	 * This is a temporary method to push dynamic ip address of server to
	 * phones. Remove this when u get a static public ip
	 */
	public void startIpUpdateTask() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				//while (true) {
					logger.debug("FCMSender: looping...");
					URL whatismyip;
					try {
						Thread.sleep(1000);
						whatismyip = new URL("http://checkip.amazonaws.com");

						BufferedReader in;
						in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
						String ip = in.readLine();
						logger.debug("FCMSender: IP..." + ip);

						List<String> regTokens = memberRepository.findAllRegistrationTokens();
						for (String regTkn : regTokens) {
							logger.debug("FCMSender: regTkn..." + regTkn);
							if (StringUtils.isBlank(regTkn))
								continue;
							sendIpAddress(regTkn, ip);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			//}
		});
		t.start();
	}

	public com.google.android.gcm.server.Result send(String regToken, JsonObject jsonObject) throws IOException {
		Gson gson = new Gson();
		Notification notification = new Notification.Builder("myicon").title("Hello world!")
				.body("Here is a more detailed description").build();
		Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
				.notification(notification).addData("message", gson.toJson(jsonObject)).build();
		return sender.send(message, regToken, 1);
	}

	public com.google.android.gcm.server.Result send(String regTkn, String name, String status, double distanceApart)
			throws IOException {
		logger.debug(
				"Sending notification to member " + name + ", status: " + status + ", distanceApart: " + distanceApart);
		Notification notification = new Notification.Builder("myicon").title(name + " is " + status)
				.body(name + " is now " + status + " and at " + distanceApart + " from you.").build();
		Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
				.notification(notification).build();
		return sender.send(message, regTkn, 1);
	}

	public Result sendIpAddress(String regTkn, String ipAddress) throws IOException {
		logger.debug("Sending notification to member, ipAddress: " + ipAddress);

		Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
				.addData("ipAddress", ipAddress).build();
		return sender.send(message, regTkn, 1);
	}

	public static void main(String[] args) throws IOException {
		FcmSender s = new FcmSender();
		JsonObject jData = new JsonObject();
		jData.addProperty("action", "testData");
		jData.addProperty("clients", "elementsElements");
		JsonObject jSendClientList = new JsonObject();
		jSendClientList.add("data", jData);
		s.send(regToken, jSendClientList);
	}
}
