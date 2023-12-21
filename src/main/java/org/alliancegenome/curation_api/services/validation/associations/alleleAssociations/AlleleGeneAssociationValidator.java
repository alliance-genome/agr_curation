package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGeneAssociationValidator extends AlleleGenomicEntityAssociationValidator<AlleleGeneAssociation> {

	@Inject
	GeneDAO geneDAO;
	@Inject
	AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	private String errorMessage;
	
	public ObjectResponse<AlleleGeneAssociation> validateAlleleGeneAssociation(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation geneAssociation = validateAlleleGeneAssociation(uiEntity, false, false);
		response.setEntity(geneAssociation);
		return response;
	}

	public AlleleGeneAssociation validateAlleleGeneAssociation(AlleleGeneAssociation uiEntity, Boolean throwError, Boolean validateAllele) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Allele Gene Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleGeneAssociation dbEntity = null;
		if (id != null) {
			dbEntity = alleleGeneAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find Allele Gene Association with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleGeneAssociation();
		}
		
		dbEntity = (AlleleGeneAssociation) validateAlleleGenomicEntityAssociationFields(uiEntity, dbEntity);

		if (validateAllele) {
			Allele subject = validateSubject(uiEntity, dbEntity);
			dbEntity.setSubject(subject);
		}
		
		Gene object = validateObject(uiEntity, dbEntity);
		dbEntity.setObject(object);

		VocabularyTerm relation = validateRelation(uiEntity, dbEntity);
		dbEntity.setRelation(relation);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}
	
	private Allele validateSubject(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		String field = "subject";
		if (ObjectUtils.isEmpty(uiEntity.getSubject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Allele subjectEntity = null;
		if (uiEntity.getSubject().getId() != null)
			subjectEntity = alleleDAO.find(uiEntity.getSubject().getId());
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getSubject() == null || !subjectEntity.getId().equals(dbEntity.getSubject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private Gene validateObject(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getObject())) {
			addMessageResponse("object", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Gene objectEntity = null;
		if (uiEntity.getObject().getId() != null)
			objectEntity = geneDAO.find(uiEntity.getObject().getId());
		if (objectEntity == null) {
			addMessageResponse("object", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (objectEntity.getObsolete() && (dbEntity.getObject() == null || !objectEntity.getId().equals(dbEntity.getObject().getId()))) {
			addMessageResponse("object", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return objectEntity;

	}

	private VocabularyTerm validateRelation(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		String field = "relation";
		if (uiEntity.getRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation().getName()).getEntity();

		if (relation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (relation.getObsolete() && (dbEntity.getRelation() == null || !relation.getName().equals(dbEntity.getRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return relation;
	}
}
