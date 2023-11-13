package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationValidator;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneValidator extends GenomicEntityValidator {

	@Inject
	GeneDAO geneDAO;
	@Inject
	SoTermDAO soTermDAO;
	@Inject
	GeneSymbolSlotAnnotationDAO geneSymbolDAO;
	@Inject
	GeneFullNameSlotAnnotationDAO geneFullNameDAO;
	@Inject
	GeneSystematicNameSlotAnnotationDAO geneSystematicNameDAO;
	@Inject
	GeneSynonymSlotAnnotationDAO geneSynonymDAO;
	@Inject
	GeneSecondaryIdSlotAnnotationDAO geneSecondaryIdDAO;
	@Inject
	GeneSymbolSlotAnnotationValidator geneSymbolValidator;
	@Inject
	GeneFullNameSlotAnnotationValidator geneFullNameValidator;
	@Inject
	GeneSystematicNameSlotAnnotationValidator geneSystematicNameValidator;
	@Inject
	GeneSynonymSlotAnnotationValidator geneSynonymValidator;
	@Inject
	GeneSecondaryIdSlotAnnotationValidator geneSecondaryIdValidator;
	@Inject
	CrossReferenceDAO crossReferenceDAO;

	private String errorMessage;

	public Gene validateGeneUpdate(Gene uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene: [" + uiEntity.getCurie() + "]";

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		Gene dbEntity = geneDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateGene(uiEntity, dbEntity);
	}

	public Gene validateGeneCreate(Gene uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Gene: [" + uiEntity.getCurie() + "]";

		Gene dbEntity = new Gene();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);

		dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateGene(uiEntity, dbEntity);
	}

	private Gene validateGene(Gene uiEntity, Gene dbEntity) {

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

		List<Long> currentXrefIds;
		if (dbEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = dbEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		List<Long> mergedIds = crossReferences == null ? new ArrayList<>() :
			crossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
		for (Long currentXrefId : currentXrefIds) {
			if (!mergedIds.contains(currentXrefId)) {
				crossReferenceDAO.remove(currentXrefId);
			}
		}
		
		SOTerm geneType = validateGeneType(uiEntity, dbEntity);
		dbEntity.setGeneType(geneType);

		removeUnusedSlotAnnotations(uiEntity, dbEntity);

		GeneSymbolSlotAnnotation symbol = validateGeneSymbol(uiEntity, dbEntity);
		GeneFullNameSlotAnnotation fullName = validateGeneFullName(uiEntity, dbEntity);
		GeneSystematicNameSlotAnnotation systematicName = validateGeneSystematicName(uiEntity, dbEntity);
		List<GeneSynonymSlotAnnotation> synonyms = validateGeneSynonyms(uiEntity, dbEntity);

		List<GeneSecondaryIdSlotAnnotation> secondaryIds = validateGeneSecondaryIds(uiEntity, dbEntity);
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = geneDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleGene(dbEntity);
			geneSymbolDAO.persist(symbol);
		}
		dbEntity.setGeneSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleGene(dbEntity);
			geneFullNameDAO.persist(fullName);
		}
		dbEntity.setGeneFullName(fullName);

		if (systematicName != null) {
			systematicName.setSingleGene(dbEntity);
			geneSystematicNameDAO.persist(systematicName);
		}
		dbEntity.setGeneSystematicName(systematicName);

		if (synonyms != null) {
			for (GeneSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleGene(dbEntity);
				geneSynonymDAO.persist(syn);
			}
		}
		dbEntity.setGeneSynonyms(synonyms);
		
		if (secondaryIds != null) {
			for (GeneSecondaryIdSlotAnnotation sid : secondaryIds) {
				sid.setSingleGene(dbEntity);
				geneSecondaryIdDAO.persist(sid);
			}
		}
		dbEntity.setGeneSecondaryIds(secondaryIds);

		return dbEntity;
	}

	private SOTerm validateGeneType(Gene uiEntity, Gene dbEntity) {
		if (uiEntity.getGeneType() == null)
			return null;

		SOTerm soTerm = soTermDAO.find(uiEntity.getGeneType().getCurie());
		if (soTerm == null) {
			addMessageResponse("geneType", ValidationConstants.INVALID_MESSAGE);
			return null;
		} else if (soTerm.getObsolete() && (dbEntity.getGeneType() == null || !soTerm.getCurie().equals(dbEntity.getGeneType().getCurie()))) {
			addMessageResponse("geneType", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return soTerm;
	}

	private GeneSymbolSlotAnnotation validateGeneSymbol(Gene uiEntity, Gene dbEntity) {
		String field = "geneSymbol";

		if (uiEntity.getGeneSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<GeneSymbolSlotAnnotation> symbolResponse = geneSymbolValidator.validateGeneSymbolSlotAnnotation(uiEntity.getGeneSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private GeneFullNameSlotAnnotation validateGeneFullName(Gene uiEntity, Gene dbEntity) {
		if (uiEntity.getGeneFullName() == null)
			return null;

		String field = "geneFullName";

		ObjectResponse<GeneFullNameSlotAnnotation> nameResponse = geneFullNameValidator.validateGeneFullNameSlotAnnotation(uiEntity.getGeneFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			return null;
		}

		return nameResponse.getEntity();
	}

	private GeneSystematicNameSlotAnnotation validateGeneSystematicName(Gene uiEntity, Gene dbEntity) {
		if (uiEntity.getGeneSystematicName() == null)
			return null;

		String field = "geneSystematicName";

		ObjectResponse<GeneSystematicNameSlotAnnotation> nameResponse = geneSystematicNameValidator.validateGeneSystematicNameSlotAnnotation(uiEntity.getGeneSystematicName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<GeneSynonymSlotAnnotation> validateGeneSynonyms(Gene uiEntity, Gene dbEntity) {
		String field = "geneSynonyms";

		List<GeneSynonymSlotAnnotation> validatedSynonyms = new ArrayList<GeneSynonymSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSynonyms())) {
			for (GeneSynonymSlotAnnotation syn : uiEntity.getGeneSynonyms()) {
				ObjectResponse<GeneSynonymSlotAnnotation> synResponse = geneSynonymValidator.validateGeneSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					addMessageResponse(field, synResponse.errorMessagesString());
					return null;
				}
				syn = synResponse.getEntity();
				validatedSynonyms.add(syn);
			}
		}

		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

		return validatedSynonyms;
	}

	private List<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIds(Gene uiEntity, Gene dbEntity) {
		String field = "geneSecondaryIds";

		List<GeneSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<GeneSecondaryIdSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSecondaryIds())) {
			for (GeneSecondaryIdSlotAnnotation sid : uiEntity.getGeneSecondaryIds()) {
				ObjectResponse<GeneSecondaryIdSlotAnnotation> sidResponse = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(sid);
				if (sidResponse.getEntity() == null) {
					addMessageResponse(field, sidResponse.errorMessagesString());
					return null;
				}
				sid = sidResponse.getEntity();
				validatedSecondaryIds.add(sid);
			}
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds))
			return null;

		return validatedSecondaryIds;
	}

	private void removeUnusedSlotAnnotations(Gene uiEntity, Gene dbEntity) {
		if (dbEntity.getGeneSymbol() != null)
			removeUnusedGeneSymbol(uiEntity, dbEntity);

		if (dbEntity.getGeneFullName() != null)
			removeUnusedGeneFullName(uiEntity, dbEntity);

		if (dbEntity.getGeneSystematicName() != null)
			removeUnusedGeneSystematicName(uiEntity, dbEntity);

		if (CollectionUtils.isNotEmpty(dbEntity.getGeneSynonyms()))
			removeUnusedGeneSynonyms(uiEntity, dbEntity);

		if (CollectionUtils.isNotEmpty(dbEntity.getGeneSecondaryIds()))
			removeUnusedGeneSecondaryIds(uiEntity, dbEntity);
	}

	private void removeUnusedGeneSymbol(Gene uiEntity, Gene dbEntity) {
		Long reusedId = uiEntity.getGeneSymbol() == null ? null : uiEntity.getGeneSymbol().getId();
		GeneSymbolSlotAnnotation previousSymbol = dbEntity.getGeneSymbol();

		if (previousSymbol != null && (reusedId == null || !previousSymbol.getId().equals(reusedId))) {
			previousSymbol.setSingleGene(null);
			geneSymbolDAO.remove(previousSymbol.getId());
		}
	}

	private void removeUnusedGeneFullName(Gene uiEntity, Gene dbEntity) {
		Long reusedId = uiEntity.getGeneFullName() == null ? null : uiEntity.getGeneFullName().getId();
		GeneFullNameSlotAnnotation previousFullName = dbEntity.getGeneFullName();

		if (previousFullName != null && (reusedId == null || !previousFullName.getId().equals(reusedId))) {
			previousFullName.setSingleGene(null);
			geneFullNameDAO.remove(previousFullName.getId());
		}
	}

	private void removeUnusedGeneSystematicName(Gene uiEntity, Gene dbEntity) {
		Long reusedId = uiEntity.getGeneSystematicName() == null ? null : uiEntity.getGeneSystematicName().getId();
		GeneSystematicNameSlotAnnotation previousSystematicName = dbEntity.getGeneSystematicName();

		if (previousSystematicName != null && (reusedId == null || !previousSystematicName.getId().equals(reusedId))) {
			previousSystematicName.setSingleGene(null);
			geneSystematicNameDAO.remove(previousSystematicName.getId());
		}
	}

	private void removeUnusedGeneSynonyms(Gene uiEntity, Gene dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSynonyms()))
			reusedIds = uiEntity.getGeneSynonyms().stream().map(GeneSynonymSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getGeneSynonyms())) {
			for (GeneSynonymSlotAnnotation previousSynonym : dbEntity.getGeneSynonyms()) {
				if (!reusedIds.contains(previousSynonym.getId())) {
					previousSynonym.setSingleGene(null);
					geneSynonymDAO.remove(previousSynonym.getId());
				}
			}
		}
	}

	private void removeUnusedGeneSecondaryIds(Gene uiEntity, Gene dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSecondaryIds()))
			reusedIds = uiEntity.getGeneSecondaryIds().stream().map(GeneSecondaryIdSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getGeneSecondaryIds())) {
			for (GeneSecondaryIdSlotAnnotation previousSecondaryId : dbEntity.getGeneSecondaryIds()) {
				if (!reusedIds.contains(previousSecondaryId.getId())) {
					previousSecondaryId.setSingleGene(null);
					geneSecondaryIdDAO.remove(previousSecondaryId.getId());
				}
			}
		}
	}

}
