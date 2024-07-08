package org.alliancegenome.curation_api.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Pagination {

	private Integer page = 0; // This has to be 0 when quering the database and 1 for querying ES
	private Integer limit = 20;

	public long getOffset() {
		return page * limit;
	}

	public void increment() {
		page += 1;
	}
}
