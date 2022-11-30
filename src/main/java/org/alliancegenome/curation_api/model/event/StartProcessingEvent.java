package org.alliancegenome.curation_api.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartProcessingEvent extends ProcessingEvent {
	private String message;
	private long startTime;
	private long totalSize;
}
