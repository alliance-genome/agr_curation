package org.alliancegenome.curation_api.model.event.load;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndLoadProcessingEvent extends LoadProcessingEvent {
	private String message;
	private String data;
	private long currentCount;
	private long totalSize;
	private long duration;
}
