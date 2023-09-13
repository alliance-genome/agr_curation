package org.alliancegenome.curation_api.model.event.load;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartLoadProcessingEvent extends LoadProcessingEvent {
	private String message;
	private long startTime;
	private long totalSize;
}
