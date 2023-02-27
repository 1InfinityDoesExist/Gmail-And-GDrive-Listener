package com.gmaillistener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gmaillistener.listener.EventSubscriberService;

@RestController
public class GMessageReceiverController {

	@Autowired
	EventSubscriberService eventSubscriberService;

	@GetMapping(value = "/g-message")
	public ResponseEntity<?> receiveGMessage(@RequestParam String credUrl, @RequestParam String admin)
			throws Exception {

		eventSubscriberService.gMessageListener(credUrl, admin);
		return ResponseEntity.status(HttpStatus.OK).body(new ModelMap().addAttribute("msg", "Success"));
	}
}
