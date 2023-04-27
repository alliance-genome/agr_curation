package org.alliancegenome.curation_api.services.validation.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

public class SlotAnnotationValidator<E extends SlotAnnotation> extends AuditedObjectValidator<E> {

	@Inject
	InformationContentEntityService informationContentEntityService;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	GeneDAO geneDAO;

	public E validateSlotAnnotationFields(E uiEntity, E dbEntity, Boolean newObject) {
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newObject);

		List<InformationContentEntity> evidence = validateEvidence(uiEntity, dbEntity);
		dbEntity.setEvidence(evidence);

		return dbEntity;
	}

	public List<InformationContentEntity> validateEvidence(E uiEntity, E dbEntity) {
		String field = "evidence";

		if (CollectionUtils.isEmpty(uiEntity.getEvidence()))
			return null;

		List<InformationContentEntity> validatedEntities = new ArrayList<>();
		for (InformationContentEntity evidenceEntity : uiEntity.getEvidence()) {
			evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceEntity.getCurie());
			if (evidenceEntity == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (evidenceEntity.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getEvidence()) || !dbEntity.getEvidence().contains(evidenceEntity))) {
				response.addErrorMessage(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			validatedEntities.add(evidenceEntity);
		}

		return validatedEntities;
	}

	public Allele validateSingleAllele(Allele uiAllele, Allele dbAllele) {
		String field = "singleAllele";

		Allele allele = alleleDAO.find(uiAllele.getCurie());
		if (allele == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		if (allele.getObsolete() && (dbAllele != null && !dbAllele.getObsolete())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return allele;
	}

	public Gene validateSingleGene(Gene uiGene, Gene dbGene) {
		String field = "singleGene";

		Gene gene = geneDAO.find(uiGene.getCurie());
		if (gene == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		if (gene.getObsolete() && (dbGene != null && !dbGene.getObsolete())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return gene;
	}

}
