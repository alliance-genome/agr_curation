package org.alliancegenome.curation_api.jobs.util;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SlackNotifier {
	
	@ConfigProperty(name = "slack.token")
	String slackToken = null;
	@ConfigProperty(name = "slack.channels")
	List<String> slackChannels = null;
	
	private void slackalert(String message) {

		Slack slack = Slack.getInstance();

		MethodsClient methods = slack.methods(slackToken);

		try {
			for(String channel : slackChannels) {
				ChatPostMessageRequest request = ChatPostMessageRequest.builder()
						.channel(channel)
						.text(message)
						.build();
				ChatPostMessageResponse response = methods.chatPostMessage(request);
			}
			slack.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void slackalert(BulkLoad bulkLoad) {
		
		
	}

	public void slackalert(BulkLoadFile bulkLoadFile) {
		
		
	}
}
