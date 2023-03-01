package com.gmaillistener.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.gmaillistener.util.GenericRestCalls;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventSubscriberService {

	@Value("${gmail.message.scopes:https://www.googleapis.com/auth/cloud-platform,https://www.googleapis.com/auth/pubsub}")
	private String[] scopes;

	@Value("${gmail.api.scopes:https://www.googleapis.com/auth/drive,https://www.googleapis.com/auth/drive.file,https://www.googleapis.com/auth/drive.readonly,https://www.googleapis.com/auth/forms.body,https://www.googleapis.com/auth/forms.body.readonly,https://www.googleapis.com/auth/forms.responses.readonly,https://mail.google.com/,https://www.googleapis.com/auth/gmail.modify,https://www.googleapis.com/auth/gmail.readonly,https://www.googleapis.com/auth/cloud-platform,https://www.googleapis.com/auth/pubsub}")
	private String[] scopesForGmailApis;

	// @Value("${gamil.message.subscriptionId:Orchestrator-Gmail-Event-Listenr}")
	@Value("${gamil.message.subscriptionId:OauthGMessageListener-sub}")
	private String subscriptionId;

	@Autowired
	private GMessageReceiver gMessageReceiver;

	@Autowired
	private GenericRestCalls genericRestCalls;

	/**
	 * admin : The emailId using which the service account was made.
	 * 
	 * Tempfile delete missing.
	 * 
	 * @param serviceAccFileUrl
	 * @param admin
	 * @throws Exception
	 */
	public void gMessageListener(String serviceAccFileUrl, String admin) throws Exception {

		String filePath = getServiceAccountDestailsAsStream(serviceAccFileUrl);

		GoogleCredential driveService = getGoogleCredential(filePath);

		String tokenValue = GoogleCredentials.fromStream(new FileInputStream(filePath))
				.createScoped(Arrays.asList(scopesForGmailApis)).createDelegated(admin).refreshAccessToken()
				.getTokenValue();

		log.info("----Token : {}", tokenValue);

		log.info("-----ProjectId : {}", driveService.getServiceAccountProjectId());
		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(driveService.getServiceAccountProjectId(),
				subscriptionId);

		Subscriber subscriber = null;
		try {
			subscriber = Subscriber.newBuilder(subscriptionName, gMessageReceiver)
					.setCredentialsProvider(getCredentialsProvider(filePath)).build();

			log.info("------Subscriber : {}", subscriber);
			subscriber.startAsync().awaitRunning();
			log.info("------Listening for messages on {}", subscriptionName.toString());
			subscriber.awaitTerminated();
		} catch (Exception timeoutException) {
			log.info("-----Error : {}", timeoutException.getMessage());
			subscriber.stopAsync();
		} finally {
			if (ObjectUtils.isEmpty(subscriber)) {
				subscriber.stopAsync();
			}
		}
	}

	private String getServiceAccountDestailsAsStream(String serviceAccFileUrl) throws IOException {
		// TODO Auto-generated method stub
		log.info("----getServiceAccountDestailsAsStream : {}", serviceAccFileUrl);

		if (ObjectUtils.isEmpty(serviceAccFileUrl)) {
			throw new RuntimeException("File not found");
		}
		Path tempFile = Files.createTempFile("Google_Cred" + "_" + new Date().getTime(), ".json");
		String response = genericRestCalls.execute(serviceAccFileUrl, HttpMethod.GET, null, null, String.class);
		Files.write(tempFile, response.getBytes(StandardCharsets.UTF_8));
		log.info("-----File path  :{}", tempFile.getParent() + File.separator + tempFile.getFileName());

		return tempFile.getParent() + File.separator + tempFile.getFileName();
	}

	private GoogleCredential getGoogleCredential(String filePath) throws FileNotFoundException, IOException {
		HttpTransport httpTransport = new NetHttpTransport();
		GoogleCredential googleCredential = GoogleCredential
				.fromStream(new FileInputStream(filePath), httpTransport, new JacksonFactory())
				.createScoped(Arrays.asList(scopesForGmailApis));
		return googleCredential;
	}

	public CredentialsProvider getCredentialsProvider(String filePath) throws IOException {
		log.info("--------InputStream : {}", filePath);
		CredentialsProvider credentialsProvider = FixedCredentialsProvider
				.create(ServiceAccountCredentials.fromStream(new FileInputStream(filePath)));
		log.info("-----CredentialsProvider : {}", credentialsProvider);
		return credentialsProvider;
	}

}