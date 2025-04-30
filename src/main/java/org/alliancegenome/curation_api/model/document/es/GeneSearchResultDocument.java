package org.alliancegenome.curation_api.model.document.es;

import java.util.Set;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Data;

@Data
@JsonView(View.GeneSearchResultDocument.class)
public class GeneSearchResultDocument extends ESDocument {

	{
		category = "gene_search_result";
	}

	private String curie;
	private String automatedGeneDescription;
	private String geneDescription;
	private String name;
	@JsonbProperty("name_key")
	private String nameKey;
	private String species;
	private String symbol;

	private Set<String> chromosomes;
	private Set<String> crossReferences;
	private Set<String> synonyms;
	private Set<String> secondaryIds;
	private Set<String> alleles;

	private Set<String> phenotypeStatements;

	private Set<String> biotypes;
	private Set<String> biotype0;
	private Set<String> biotype1;
	private Set<String> biotype2;
	private String soTermId;
	private String soTermName;
	private Set<String> soTermNameWithParents;

	private Set<String> expressionStages;
	private Set<String> whereExpressed;

	private Set<String> diseases;
	// private Set<String> diseasesAgrSlim;
	private Set<String> diseasesWithParents;
	private Set<String> molecularFunctionAgrSlim;
	private Set<String> molecularFunctionWithParents;
	private Set<String> biologicalProcessAgrSlim;
	private Set<String> biologicalProcessWithParents;
	private Set<String> cellularComponentAgrSlim;
	private Set<String> cellularComponentWithParents;
	private Set<String> subcellularExpressionAgrSlim;
	private Set<String> subcellularExpressionWithParents;

	// private Set<String> anatomicalExpression; // uberon slim
	private Set<String> anatomicalExpressionWithParents;

	private Set<String> strictOrthologySymbols;

	// Gene -> AlleleGeneAssociation -> Allele -> AgmAlleleAssociation -> AGM ->
	// Symbol
	// private Set<String> models;

}
