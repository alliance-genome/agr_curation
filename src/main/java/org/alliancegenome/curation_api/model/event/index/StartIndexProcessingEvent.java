package org.alliancegenome.curation_api.model.event.index;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartIndexProcessingEvent extends IndexProcessingEvent {
	private String message;
	private long startTime;
	private long totalSize;
}
