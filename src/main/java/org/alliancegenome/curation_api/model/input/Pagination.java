package org.alliancegenome.curation_api.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Pagination {

	private Integer page = 0; // This has to be 0 when querying the database and 1 for querying ES
	private Integer limit = 20;
	private Long cursor; // Optional cursor for cursor-based pagination (last seen ID)
	
	// Backward compatibility constructor for existing code
	public Pagination(Integer page, Integer limit) {
		this.page = page;
		this.limit = limit;
		this.cursor = null;
	}

	public long getOffset() {
		return page * limit;
	}

	public void increment() {
		page += 1;
	}

	// if page = 0 and limit = 0 calculate the count of records
	public boolean isCountCondition() {
		return page == 0 && limit == 0;
	}
	
	// Check if cursor-based pagination should be used
	public boolean isCursorBased() {
		return cursor != null;
	}
}
