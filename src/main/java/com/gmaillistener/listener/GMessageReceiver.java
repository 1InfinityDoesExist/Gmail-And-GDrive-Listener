package com.gmaillistener.listener;

import org.springframework.stereotype.Component;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GMessageReceiver implements MessageReceiver {

	@Override
	public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
		log.info("------Logging message and consumer : {}", message.getData().toStringUtf8());
		consumer.ack();
	}
}
