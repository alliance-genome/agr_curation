package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class LoadHistoryResponce extends APIResponse {

	@JsonView({ View.FieldsOnly.class })
	private BulkLoadFileHistory history;

	public LoadHistoryResponce(BulkLoadFileHistory history) {
		this.history = history;
	}

}
