package com.ib.util.twilio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SendgridUtil {
	private static final String KEY_APIKEY = "KEY";
	private static final String KEY_FROM = "FROM";
	private static final String KEY_SID = "SID";
	private static final String KEY_SMSTOKEN = "SMSTOKEN";
	private static final String KEY_SMSTO = "SMSTO";
	private static final String KEY_SMSFROM = "SMSFROM";
	private static final String ENV_LOCATION = "./data/sendgrid.env";
	private Map<String, String> env_vars;

	public SendgridUtil() {
		env_vars = new HashMap<>();

		String line = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(ENV_LOCATION))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=");
				env_vars.put(parts[0], parts[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(String subject, String body) {
		String to_email = env_vars.get(KEY_FROM); // We test with dummy emails so send everything to 1 email.
		Email from = new Email(env_vars.get(KEY_FROM));
		Email to = new Email(to_email);
		Content content = new Content("text/html", body);
		Mail mail = new Mail(from, subject, to, content);
		SendGrid sg = new SendGrid(env_vars.get(KEY_APIKEY));
		Request request = new Request();

		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		try {
			request.setBody(mail.build());
			Response response;
			response = sg.api(request);
			System.err.println("Sending email to " + to_email + ":");
			System.err.println(body);
			System.err.println("===========================");
			System.err.println(response.getStatusCode());
			System.err.println(response.getBody());
			System.err.println(response.getHeaders());
			System.err.println("===========================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSMS(String body) {
		Twilio.init(env_vars.get(KEY_SID), env_vars.get(KEY_SMSTOKEN));
		Message msg = Message
				.creator(new PhoneNumber(env_vars.get(KEY_SMSTO)), new PhoneNumber(env_vars.get(KEY_SMSFROM)), body)
				.create();

		System.err.println("Sending sms to " + msg.getTo() + ":");
		System.err.println(msg.getBody());
		System.err.println("===========================");
		System.err.println(msg.getAccountSid());
		System.err.println("===========================");
	}
}
