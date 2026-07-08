package org.alliancegenome.curation_api.model.document.es;

import java.util.Set;

import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView({ CurationView.GeneSummaryDocument.class, CurationView.GeneCacheDocument.class })
public class GeneSummaryDocument extends ESDocument {

	{
		category = "gene_summary";
	}

	private Gene gene;

	@JsonView(CurationView.GeneSummaryDocument.class)
	private Set<String> referenceCuries;
}
