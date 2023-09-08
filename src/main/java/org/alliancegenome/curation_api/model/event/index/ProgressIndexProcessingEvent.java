package org.alliancegenome.curation_api.model.event.index;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgressIndexProcessingEvent extends IndexProcessingEvent {

	private String message;
	private String data;
	private long startTime;
	private long nowTime;
	private long lastTime;
	private long currentCount;
	private long lastCount;
	private long totalSize;

}
