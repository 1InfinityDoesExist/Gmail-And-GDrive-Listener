package com.gmaillistener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gmaillistener.listener.EventSubscriberService;
import com.gmaillistener.service.GMessageListener;

@RestController
public class GMessageReceiverController {

	@Autowired
	private EventSubscriberService eventSubscriberService;

	@Autowired
	private GMessageListener gMessageListener;

	@GetMapping(value = "/g-message")
	public ResponseEntity<?> receiveGMessage(@RequestParam String credUrl, @RequestParam String admin)
			throws Exception {

		eventSubscriberService.gMessageListener(credUrl, admin);
		return ResponseEntity.status(HttpStatus.OK).body(new ModelMap().addAttribute("msg", "Success"));
	}

	@RequestMapping(value = { "/v1.0/google/token" }, produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<ModelMap> getTokenUsingPOST(@RequestPart(value = "file", required = true) MultipartFile file)
			throws Exception {

		return ResponseEntity.status(HttpStatus.OK)
				.body(new ModelMap().addAttribute("goole_oauth_token", gMessageListener.getGoogleOauthToken(file)));
	}

}
