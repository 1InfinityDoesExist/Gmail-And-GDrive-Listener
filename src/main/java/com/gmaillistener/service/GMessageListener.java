package com.gmaillistener.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface GMessageListener {
	public String getGoogleOauthToken(MultipartFile file) throws IOException;

}
