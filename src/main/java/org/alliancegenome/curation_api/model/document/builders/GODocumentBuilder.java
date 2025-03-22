package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.document.es.GOSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GODocumentBuilder {

	public static GOSearchResultDocument buildSearchResultDocument(GOTerm goTerm) {
		GOSearchResultDocument doc = new GOSearchResultDocument();
		doc.setCurie(goTerm.getCurie());
		doc.setBranch(goTerm.getNamespace());
		doc.setDefinition(goTerm.getDefinition());
		doc.setName(goTerm.getName());
		doc.setNameKey(goTerm.getName());

		List<GeneOntologyAnnotation> goAnnotations = goTerm.getGeneOntologyAnnotations();
		if (goAnnotations != null) {
			Set<String> genes = new HashSet<>();
			Set<String> associatedSpecies = new HashSet<>();
			for (GeneOntologyAnnotation goAnnotation : goAnnotations) {
				String geneSymbol = goAnnotation.getSingleGene().getGeneSymbol().getDisplayText();
				String speciesAbbreviation = goAnnotation.getSingleGene().getTaxon().getSpecies().get(0).getAbbreviation();
				String geneDisplayString = geneSymbol + " (" + speciesAbbreviation + ")";
				
				String taxonName = goAnnotation.getSingleGene().getTaxon().getName();

				genes.add(geneDisplayString);
				associatedSpecies.add(taxonName);
			}
			doc.setGenes(genes);
			doc.setAssociatedSpecies(associatedSpecies);
		}
		List<Synonym> synonyms = goTerm.getSynonyms();
		if (synonyms != null) {
			Set<String> synonymSet = new HashSet<>();
			for (Synonym synonym : synonyms) {
				synonymSet.add(synonym.getName());
			}
			doc.setSynonyms(synonymSet);
		}

		return doc;

	}

}
