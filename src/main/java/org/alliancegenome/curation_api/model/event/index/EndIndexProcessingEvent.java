package org.alliancegenome.curation_api.model.event.index;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndIndexProcessingEvent extends IndexProcessingEvent {
	private String message;
	private String data;
	private long currentCount;
	private long totalSize;
	private long duration;
}
