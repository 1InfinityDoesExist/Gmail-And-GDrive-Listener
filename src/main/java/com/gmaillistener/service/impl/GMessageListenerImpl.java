package com.gmaillistener.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.gmaillistener.service.GMessageListener;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GMessageListenerImpl implements GMessageListener {

	@Value("${gmail.api.scopes:https://www.googleapis.com/auth/drive,https://www.googleapis.com/auth/drive.file,https://www.googleapis.com/auth/drive.readonly,https://www.googleapis.com/auth/forms.body,https://www.googleapis.com/auth/forms.body.readonly,https://www.googleapis.com/auth/forms.responses.readonly,https://mail.google.com/,https://www.googleapis.com/auth/gmail.modify,https://www.googleapis.com/auth/gmail.readonly,https://www.googleapis.com/auth/cloud-platform,https://www.googleapis.com/auth/pubsub}")
	private String[] googleScopes;

	@Override
	public String getGoogleOauthToken(MultipartFile file) throws IOException {
		log.info("----UtilityServiceImpl Class to generate token-----");

		String fileName = file.getOriginalFilename();
		log.info("-----Google Cred File Name : {}", fileName);
		if (file.isEmpty()) {
			throw new RuntimeException("File not found.");
		}

		Path targetLocation = Files.createTempFile(FilenameUtils.getBaseName(fileName),
				"." + FilenameUtils.getExtension(file.getOriginalFilename()));

		File credFile = new File(targetLocation.toString());
		credFile.setWritable(true);
		FileUtils.copyInputStreamToFile(file.getInputStream(), credFile);

		InputStream resourceAsStream = new FileInputStream(credFile);

		log.info("-----InputStream : {}", resourceAsStream);

		String tokenValue = GoogleCredentials.fromStream(resourceAsStream).createScoped(Arrays.asList(googleScopes))
				.refreshAccessToken().getTokenValue();

		log.info("-----Deleting the google cred file.");
		Files.deleteIfExists(targetLocation);

		return StringUtils.stripEnd(tokenValue, ".");

	}
}
