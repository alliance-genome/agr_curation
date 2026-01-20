package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.TransgenicAlleleConstruct;
import org.alliancegenome.curation_api.view.CurationView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(CurationView.TransgenicAllelesDocument.class)
public class TransgenicAlleleDocument extends ESDocument {
	{
		category = "transgenic_allele_annotation";
	}

	private Gene gene;
	private Allele allele;
	/**
	 * This collection is the association between a construct and the corresponding expressedGene, regulatoryGene, or sequenceTargetingReagent.
	 */
	private List<TransgenicAlleleConstruct> transgenicAlleleConstructs;

	public List<Construct> constructList;

	public List<Gene> expressedGenes;

	public List<SequenceTargetingReagent> sequenceTargetingReagents;

	public List<Gene> regulatoryGenes;

	private Boolean hasDiseaseAnnotations;

	private Boolean hasPhenotypeAnnotations;
}
