package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.TransgenicAllelesDocumentView.class)
public class TransgenicAlleleDocument extends ESDocument {
	{
		category = "transgenic_allele_annotation";
	}

	private Gene gene;
	private Allele allele;
	private List<Construct> constructs;
	private List<Gene> expressedGenes;
	private List<SequenceTargetingReagent> sequenceTargetingReagents;
	private List<Gene> regulatoryGenes;
	private Boolean hasDiseaseAnnotations;
	private Boolean hasPhenotypeAnnotations;
}
