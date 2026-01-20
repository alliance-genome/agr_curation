package org.alliancegenome.curation_api.model.document.es;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(CurationView.AccessionSummaryDocument.class)
public class AccessionSummaryDocument extends ESDocument {
	{
		category = "accession_summary";
	}
	
	private Map<String, List<String>> idsByType;
}
