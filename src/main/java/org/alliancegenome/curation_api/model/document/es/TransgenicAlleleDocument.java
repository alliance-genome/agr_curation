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
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransgenicAlleleDocument extends ESDocument {
	{
		category = "transgenic_allele_annotation";
	}

	@JsonView(View.TransgenicAllelesDocumentView.class)
	private Gene gene;
	@JsonView(View.TransgenicAllelesDocumentView.class)
	private Allele allele;
	/**
	 * This collection is the association between a construct and the corresponding expressedGene, regulatoryGene, or sequenceTargetingReagent.
	 */
	@JsonView(View.TransgenicAllelesDocumentView.class)
	private List<TransgenicAlleleConstruct> transgenicAlleleConstructs;

	@JsonGetter
	@JsonView(View.TransgenicAllelesDocumentView.class)
	public List<Construct> constructList() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getConstruct).toList();
	}

	@JsonGetter
	@JsonView(View.TransgenicAllelesDocumentView.class)
	public List<Gene> expressedGenes() {
		List<Gene> genes = new ArrayList<>(transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getExpressedGenes).flatMap(Collection::stream).toList());
		genes.addAll(transgenicAlleleConstructs.stream()
				.map(TransgenicAlleleConstruct::getNonBgiComponents)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.toList());
		return genes;
	}

	@JsonGetter
	@JsonView(View.TransgenicAllelesDocumentView.class)
	public List<SequenceTargetingReagent> sequenceTargetingReagents() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getSequenceTargetingReagents).filter(Objects::nonNull).flatMap(Collection::stream).toList();
	}

	@JsonGetter
	@JsonView(View.TransgenicAllelesDocumentView.class)
	public List<Gene> regulatoryGenes() {
		return transgenicAlleleConstructs.stream().map(TransgenicAlleleConstruct::getRegulatoryGenes).flatMap(Collection::stream).toList();
	}

	@JsonView(View.TransgenicAllelesDocumentView.class)
	private Boolean hasDiseaseAnnotations;

	@JsonView(View.TransgenicAllelesDocumentView.class)
	private Boolean hasPhenotypeAnnotations;
}
