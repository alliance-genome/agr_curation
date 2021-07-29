package org.alliancegenome.curation_api.mbean;

import javax.ejb.*;
import javax.jms.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@MessageDriven(name = "bgiprocessing", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/bgiprocessing"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class BgiProcessor implements MessageListener {

	@Override
	public void onMessage(Message message) {
		log.info("Message: " + message);
	}

}
