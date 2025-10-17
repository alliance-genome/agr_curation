package org.alliancegenome.curation_api.model.document.es;

import java.util.Map;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.CrossReference;
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
	private String alterationType;
	private String description;
	private Map<String, Object> additionalInformation;
	private Gene alleleOfGene;
	private CrossReference crossReference;
}
