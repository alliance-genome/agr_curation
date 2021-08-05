package org.alliancegenome.curation_api.model.dto;

import lombok.*;

@Setter
@Getter
public class Pagination {

	private Integer page = 1;
	private Integer limit = 20;
	private String sortBy;

}
