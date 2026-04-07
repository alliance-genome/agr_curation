package org.alliancegenome.curation_api.model.document.builders;

import java.util.List;

import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDocument;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.apache.commons.collections4.CollectionUtils;

public class TransgenicAlleleDocumentBuilder {

	public TransgenicAlleleDocument buildTransgenicAlleleDocument(AlleleConstructAssociation association) {
		Allele allele = association.getAlleleAssociationSubject();

		TransgenicAlleleDocument transgenicAlleleDocument = new TransgenicAlleleDocument();
		transgenicAlleleDocument.setAllele(allele);
		transgenicAlleleDocument.setConstructList(List.of(association.getAlleleConstructAssociationObject()));
		// need disease and phenotype data only for constructs with genes (genomic entities)
		if (CollectionUtils.isNotEmpty(association.getAlleleConstructAssociationObject().getConstructGenomicEntityAssociations())) {
			// check AlleleDiseaseAnnotations and AGMDiseaseAnnotations with inferred or asserted alleles for disease annotations
			List<AGMDiseaseAnnotation> agmDiseaseAnnotations = allele.getAgmDiseaseAssertedAlleleAnnotations();
			agmDiseaseAnnotations.addAll(allele.getAgmDiseaseInferredAlleleAnnotations());
			Boolean hasDiseaseAnnotation = CollectionUtils.isNotEmpty(agmDiseaseAnnotations) || CollectionUtils.isNotEmpty(allele.getAlleleDiseaseAnnotations());

			// check AllelePhenotypeAnnotations and AGMPhenotypeAnnotations with inferred or asserted alleles for phenotype annotations
			List<AGMPhenotypeAnnotation> agmPhenotypeAnnotations = allele.getAgmPhenotypeAssertedAlleleAnnotations();
			agmPhenotypeAnnotations.addAll(allele.getAgmPhenotypeInferredAlleleAnnotations());
			Boolean hasPhenotypeAnnotation = CollectionUtils.isNotEmpty(agmPhenotypeAnnotations) || CollectionUtils.isNotEmpty(allele.getAllelePhenotypeAnnotations());
			transgenicAlleleDocument.setHasDiseaseAnnotations(hasDiseaseAnnotation);
			transgenicAlleleDocument.setHasPhenotypeAnnotations(hasPhenotypeAnnotation);
		}
		return transgenicAlleleDocument;
	}

}
