package org.alliancegenome.curation_api.mbean;

import javax.jms.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog

//@MessageDriven(name = "bgiProcessingQueue", activationConfig = {
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/bgiProcessingQueue"),
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
//		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
//})
public class BgiProcessor implements MessageListener {

	@Override
	public void onMessage(Message message) {
		log.info("Message: " + message);
	}

}
