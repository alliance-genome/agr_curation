package org.alliancegenome.curation_api.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndProcessingEvent extends ProcessingEvent {
	private String message;
	private String data;
	private long currentCount;
	private long totalSize;
	private long duration;
}
