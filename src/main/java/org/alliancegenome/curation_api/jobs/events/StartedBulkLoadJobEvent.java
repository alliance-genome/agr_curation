package org.alliancegenome.curation_api.jobs.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class StartedBulkLoadJobEvent {

	private Long id;
}
