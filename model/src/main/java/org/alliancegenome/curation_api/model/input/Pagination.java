package org.alliancegenome.curation_api.model.input;

import lombok.Data;

@Data
public class Pagination {

	private Integer page = 0; // This has to be 0 when quering the database and 1 for querying ES
	private Integer limit = 20;
	
	public Pagination(Integer page, Integer limit) {
		this.page = page;
		this.limit = limit;
	}


}
