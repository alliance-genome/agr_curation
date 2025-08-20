package org.alliancegenome.curation_api.model.document.es;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.TransgenicAlleleConstruct;
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
	/**
	 * This collection is the association between a construct and the corresponding expressedGene, regulatoryGene, or sequenceTargetingReagent.
	 */
	private List<TransgenicAlleleConstruct> transgenicAlleleConstructs;

	@JsonView(View.TransgenicAllelesDocumentView.class)
	public List<Construct> getConstructList() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getConstruct).toList();
	}

	public List<Gene> getExpressedGenes() {
		List<Gene> genes = new ArrayList<>(transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getExpressedGenes).flatMap(Collection::stream).toList());
		genes.addAll(transgenicAlleleConstructs.stream()
				.map(TransgenicAlleleConstruct::getNonBgiComponents)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.toList());
		return genes;
	}

	public List<SequenceTargetingReagent> getSequenceTargetingReagents() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getSequenceTargetingReagents).filter(Objects::nonNull).flatMap(Collection::stream).toList();
	}

	public List<Gene> getRegulatoryGenes() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getRegulatoryGenes).flatMap(Collection::stream).toList();
	}

	private Boolean hasDiseaseAnnotations;
	private Boolean hasPhenotypeAnnotations;
}
