package org.alliancegenome.curation_api.model.document.es;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(View.AlleleSummaryDocument.class)
public class AlleleSummaryDocument extends ESDocument {

	{
		category = "allele_summary";
	}

	private Allele allele;
	private String species;
	private String symbol;
	private String alterationType;
	private List<String> synonyms;
	private String description;
	private Map<String, Object> additionalInformation;
	private String alleleOfGene;
}
