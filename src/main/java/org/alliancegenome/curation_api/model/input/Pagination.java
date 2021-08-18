package org.alliancegenome.curation_api.model.input;

import lombok.Data;

@Data
public class Pagination {

    private Integer page = 1;
    private Integer limit = 20;

}
