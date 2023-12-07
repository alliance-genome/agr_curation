package org.alliancegenome.curation_api.jobs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

@ApplicationScoped
public class SlackNotifier {
	
	@ConfigProperty(name = "net")
	Instance<String> systemName;
	@ConfigProperty(name = "slack.token")
	Instance<String> slackToken;
	@ConfigProperty(name = "slack.channels")
	Instance<List<String>> slackChannels;
	
	private void slackalert(String groupName, String loadName, String message, List<Field> fields) {
		
		if(!systemName.get().equals("\"\"") && !slackToken.get().equals("\"\"")) {

			Slack slack = Slack.getInstance();
	
			MethodsClient methods = slack.methods(slackToken.get());
			
			String systemNameString = systemName.get();
			
			try {
				for(String channel : slackChannels.get()) {
					
					Attachment attachment = new Attachment();
					attachment.setServiceName(groupName);
					attachment.setPretext("An error has occured on Curation " + systemNameString);
					attachment.setTitle(loadName);
					if(systemNameString.equals("production")) {
						attachment.setTitleLink("https://curation.alliancegenome.org/#/dataloads");
						attachment.setServiceUrl("https://curation.alliancegenome.org/#/dataloads");
					}
					else {
						attachment.setTitleLink("https://" + systemNameString + "-curation.alliancegenome.org/#/dataloads");
						attachment.setServiceUrl("https://" + systemNameString + "-curation.alliancegenome.org/#/dataloads");
					}
					attachment.setFooter("Failure Time: ");
					attachment.setTs(String.valueOf((new Date()).getTime() / 1000));
					attachment.setColor("danger");
					if(fields != null) {
						attachment.setFields(fields);
					}
					List<Attachment> attachments = new ArrayList<>();
					attachments.add(attachment);
					ChatPostMessageRequest request = ChatPostMessageRequest.builder()
							.channel(channel)
							.text(message)
							.attachments(attachments)
							.build();
					methods.chatPostMessage(request);
				}
				slack.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void slackalert(BulkLoad bulkLoad) {
		
		if(bulkLoad.getBulkloadStatus() == JobStatus.FAILED) {
		
			List<Field> fields = new ArrayList<>();
			fields.add(new Field("Load Type", String.valueOf(bulkLoad.getBackendBulkLoadType()), true));
			if(bulkLoad.getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
				fields.add(new Field("Ontology Type", String.valueOf(bulkLoad.getOntologyType()), true));
			}
			slackalert(
					
					bulkLoad.getGroup().getName(), 
					bulkLoad.getName(),
					bulkLoad.getErrorMessage(),
					fields
					);
		}
	}

	public void slackalert(BulkLoadFile bulkLoadFile) {
		
		
		if(bulkLoadFile.getBulkloadStatus() == JobStatus.FAILED) {
			List<Field> fields = new ArrayList<>();
			fields.add(new Field("Load Type", String.valueOf(bulkLoadFile.getBulkLoad().getBackendBulkLoadType()), true));
			if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
				fields.add(new Field("Ontology Type", String.valueOf(bulkLoadFile.getBulkLoad().getOntologyType()), true));
			}
			fields.add(new Field("MD5Sum", bulkLoadFile.getMd5Sum(), true));
			fields.add(new Field("File Size", String.valueOf(bulkLoadFile.getFileSize()), true));
			fields.add(new Field("LinkML Version", bulkLoadFile.getLinkMLSchemaVersion(), true));
			fields.add(new Field("Alliance Member Release Version", bulkLoadFile.getAllianceMemberReleaseVersion(), false));
			
			slackalert(
					bulkLoadFile.getBulkLoad().getGroup().getName(),
					bulkLoadFile.getBulkLoad().getName(),
					bulkLoadFile.getErrorMessage(),
					fields
					);
		}
	}
}
