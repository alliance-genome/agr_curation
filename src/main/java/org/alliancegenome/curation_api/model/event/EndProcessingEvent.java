package org.alliancegenome.curation_api.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndProcessingEvent extends ProcessingEvent {
	private String message;
	private String data;
	private long current;
	private long duration;
}
