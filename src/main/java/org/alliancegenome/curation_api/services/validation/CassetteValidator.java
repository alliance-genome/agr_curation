package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.CassetteUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.associations.CassetteGenomicEntityAssociationValidator;
import org.alliancegenome.curation_api.services.validation.associations.CassetteStrAssociationValidator;
import org.alliancegenome.curation_api.services.validation.associations.CassetteTransgenicToolAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteComponentSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSynonymSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteUseSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: mirrors ConstructValidator.
 *
 * Validates all three cassette association lists, as ConstructValidator does for its one, so a
 * curator editing a cassette in the UI can maintain its components in either form.
 */
@RequestScoped
public class CassetteValidator extends ReagentValidator {

	@Inject CassetteDAO cassetteDAO;
	@Inject ReferenceValidator referenceValidator;
	@Inject CassetteSymbolSlotAnnotationValidator cassetteSymbolValidator;
	@Inject CassetteFullNameSlotAnnotationValidator cassetteFullNameValidator;
	@Inject CassetteSynonymSlotAnnotationValidator cassetteSynonymValidator;
	@Inject CassetteComponentSlotAnnotationValidator cassetteComponentValidator;
	@Inject CassetteUseSlotAnnotationValidator cassetteUseValidator;
	@Inject CassetteGenomicEntityAssociationValidator cassetteGenomicEntityAssociationValidator;
	@Inject CassetteTransgenicToolAssociationValidator cassetteTransgenicToolAssociationValidator;
	@Inject CassetteStrAssociationValidator cassetteStrAssociationValidator;

	private String errorMessage;

	public Cassette validateCassetteUpdate(Cassette uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Cassette: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Cassette ID provided");
			throw new ApiErrorException(response);
		}
		Cassette dbEntity = cassetteDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Cassette with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		return validateCassette(uiEntity, dbEntity);
	}

	public Cassette validateCassetteCreate(Cassette uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Cassette";

		Cassette dbEntity = new Cassette();

		return validateCassette(uiEntity, dbEntity);
	}

	public Cassette validateCassette(Cassette uiEntity, Cassette dbEntity) {

		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences())) {
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences())) {
			List<Reference> references = new ArrayList<Reference>();
			for (Reference uiReference : uiEntity.getReferences()) {
				Reference reference = validateReference(uiReference, previousReferenceCuries);
				if (reference != null) {
					references.add(reference);
				}
			}
			dbEntity.setReferences(references);
		} else {
			dbEntity.setReferences(null);
		}

		dbEntity = (Cassette) validateCommonReagentFields(uiEntity, dbEntity, VocabularyConstants.CASSETTE_AND_TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET);

		CassetteSymbolSlotAnnotation symbol = validateCassetteSymbol(uiEntity);
		CassetteFullNameSlotAnnotation fullName = validateCassetteFullName(uiEntity);
		List<CassetteSynonymSlotAnnotation> synonyms = validateCassetteSynonyms(uiEntity, dbEntity);
		List<CassetteComponentSlotAnnotation> components = validateCassetteComponents(uiEntity, dbEntity);
		List<CassetteUseSlotAnnotation> uses = validateCassetteUses(uiEntity, dbEntity);

		List<CassetteGenomicEntityAssociation> geAssociations = validateCassetteGenomicEntityAssociations(uiEntity, dbEntity);
		List<CassetteTransgenicToolAssociation> ttAssociations = validateCassetteTransgenicToolAssociations(uiEntity, dbEntity);
		List<CassetteStrAssociation> strAssociations = validateCassetteStrAssociations(uiEntity, dbEntity);

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = cassetteDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleCassette(dbEntity);
		}
		dbEntity.setCassetteSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleCassette(dbEntity);
		}
		dbEntity.setCassetteFullName(fullName);

		if (dbEntity.getCassetteSynonyms() != null) {
			dbEntity.getCassetteSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getCassetteSynonyms() == null) {
				dbEntity.setCassetteSynonyms(new ArrayList<>());
			}
			dbEntity.getCassetteSynonyms().addAll(synonyms);
		}

		if (dbEntity.getCassetteComponents() != null) {
			dbEntity.getCassetteComponents().clear();
		}
		if (components != null) {
			if (dbEntity.getCassetteComponents() == null) {
				dbEntity.setCassetteComponents(new ArrayList<>());
			}
			dbEntity.getCassetteComponents().addAll(components);
		}

		if (dbEntity.getCassetteUses() != null) {
			dbEntity.getCassetteUses().clear();
		}
		if (uses != null) {
			if (dbEntity.getCassetteUses() == null) {
				dbEntity.setCassetteUses(new ArrayList<>());
			}
			dbEntity.getCassetteUses().addAll(uses);
		}

		if (dbEntity.getCassetteGenomicEntityAssociations() != null) {
			dbEntity.getCassetteGenomicEntityAssociations().clear();
		}
		if (geAssociations != null) {
			if (dbEntity.getCassetteGenomicEntityAssociations() == null) {
				dbEntity.setCassetteGenomicEntityAssociations(new ArrayList<>());
			}
			dbEntity.getCassetteGenomicEntityAssociations().addAll(geAssociations);
		}

		if (dbEntity.getCassetteTransgenicToolAssociations() != null) {
			dbEntity.getCassetteTransgenicToolAssociations().clear();
		}
		if (ttAssociations != null) {
			if (dbEntity.getCassetteTransgenicToolAssociations() == null) {
				dbEntity.setCassetteTransgenicToolAssociations(new ArrayList<>());
			}
			dbEntity.getCassetteTransgenicToolAssociations().addAll(ttAssociations);
		}

		if (dbEntity.getCassetteStrAssociations() != null) {
			dbEntity.getCassetteStrAssociations().clear();
		}
		if (strAssociations != null) {
			if (dbEntity.getCassetteStrAssociations() == null) {
				dbEntity.setCassetteStrAssociations(new ArrayList<>());
			}
			dbEntity.getCassetteStrAssociations().addAll(strAssociations);
		}

		return dbEntity;
	}

	private Reference validateReference(Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse("references", singleRefResponse.errorMessagesString());
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (CollectionUtils.isEmpty(previousCuries) || !previousCuries.contains(singleRefResponse.getEntity().getCurie()))) {
			addMessageResponse("references", "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	public String validateUniqueId(Cassette uiEntity, Cassette dbEntity) {

		if (dbEntity.getDataProvider() == null) {
			return null;
		}

		String uniqueId = CassetteUniqueIdHelper.getCassetteUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<Cassette> response = cassetteDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	private CassetteSymbolSlotAnnotation validateCassetteSymbol(Cassette uiEntity) {
		String field = "cassetteSymbol";

		if (uiEntity.getCassetteSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<CassetteSymbolSlotAnnotation> symbolResponse = cassetteSymbolValidator.validateCassetteSymbolSlotAnnotation(uiEntity.getCassetteSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private CassetteFullNameSlotAnnotation validateCassetteFullName(Cassette uiEntity) {
		if (uiEntity.getCassetteFullName() == null) {
			return null;
		}

		String field = "cassetteFullName";

		ObjectResponse<CassetteFullNameSlotAnnotation> nameResponse = cassetteFullNameValidator.validateCassetteFullNameSlotAnnotation(uiEntity.getCassetteFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<CassetteSynonymSlotAnnotation> validateCassetteSynonyms(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteSynonyms";

		List<CassetteSynonymSlotAnnotation> validatedSynonyms = new ArrayList<CassetteSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteSynonyms())) {
			for (int ix = 0; ix < uiEntity.getCassetteSynonyms().size(); ix++) {
				CassetteSynonymSlotAnnotation syn = uiEntity.getCassetteSynonyms().get(ix);
				ObjectResponse<CassetteSynonymSlotAnnotation> synResponse = cassetteSynonymValidator.validateCassetteSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleCassette(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<CassetteComponentSlotAnnotation> validateCassetteComponents(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteComponents";

		List<CassetteComponentSlotAnnotation> validatedComponents = new ArrayList<CassetteComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteComponents())) {
			for (int ix = 0; ix < uiEntity.getCassetteComponents().size(); ix++) {
				CassetteComponentSlotAnnotation comp = uiEntity.getCassetteComponents().get(ix);
				ObjectResponse<CassetteComponentSlotAnnotation> compResponse = cassetteComponentValidator.validateCassetteComponentSlotAnnotation(comp);
				if (compResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, compResponse.getErrorMessages());
					allValid = false;
				} else {
					comp = compResponse.getEntity();
					comp.setSingleCassette(dbEntity);
					validatedComponents.add(comp);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedComponents)) {
			return null;
		}

		return validatedComponents;
	}

	private List<CassetteUseSlotAnnotation> validateCassetteUses(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteUses";

		List<CassetteUseSlotAnnotation> validatedUses = new ArrayList<CassetteUseSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteUses())) {
			for (int ix = 0; ix < uiEntity.getCassetteUses().size(); ix++) {
				CassetteUseSlotAnnotation use = uiEntity.getCassetteUses().get(ix);
				ObjectResponse<CassetteUseSlotAnnotation> useResponse = cassetteUseValidator.validateCassetteUseSlotAnnotation(use);
				if (useResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, useResponse.getErrorMessages());
					allValid = false;
				} else {
					use = useResponse.getEntity();
					use.setSingleCassette(dbEntity);
					validatedUses.add(use);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedUses)) {
			return null;
		}

		return validatedUses;
	}

	private List<CassetteGenomicEntityAssociation> validateCassetteGenomicEntityAssociations(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteGenomicEntityAssociations";

		List<CassetteGenomicEntityAssociation> validatedAssociations = new ArrayList<CassetteGenomicEntityAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteGenomicEntityAssociations())) {
			for (int ix = 0; ix < uiEntity.getCassetteGenomicEntityAssociations().size(); ix++) {
				CassetteGenomicEntityAssociation assoc = uiEntity.getCassetteGenomicEntityAssociations().get(ix);
				ObjectResponse<CassetteGenomicEntityAssociation> assocResponse = cassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(assoc);
				if (assocResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, assocResponse.getErrorMessages());
				} else {
					assoc = assocResponse.getEntity();
					assoc.setCassetteAssociationSubject(dbEntity);
					validatedAssociations.add(assoc);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedAssociations)) {
			return null;
		}

		return validatedAssociations;
	}

	private List<CassetteTransgenicToolAssociation> validateCassetteTransgenicToolAssociations(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteTransgenicToolAssociations";

		List<CassetteTransgenicToolAssociation> validatedAssociations = new ArrayList<CassetteTransgenicToolAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteTransgenicToolAssociations())) {
			for (int ix = 0; ix < uiEntity.getCassetteTransgenicToolAssociations().size(); ix++) {
				CassetteTransgenicToolAssociation assoc = uiEntity.getCassetteTransgenicToolAssociations().get(ix);
				ObjectResponse<CassetteTransgenicToolAssociation> assocResponse = cassetteTransgenicToolAssociationValidator.validateCassetteTransgenicToolAssociation(assoc);
				if (assocResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, assocResponse.getErrorMessages());
				} else {
					assoc = assocResponse.getEntity();
					assoc.setCassetteAssociationSubject(dbEntity);
					validatedAssociations.add(assoc);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedAssociations)) {
			return null;
		}

		return validatedAssociations;
	}

	private List<CassetteStrAssociation> validateCassetteStrAssociations(Cassette uiEntity, Cassette dbEntity) {
		String field = "cassetteStrAssociations";

		List<CassetteStrAssociation> validatedAssociations = new ArrayList<CassetteStrAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCassetteStrAssociations())) {
			for (int ix = 0; ix < uiEntity.getCassetteStrAssociations().size(); ix++) {
				CassetteStrAssociation assoc = uiEntity.getCassetteStrAssociations().get(ix);
				ObjectResponse<CassetteStrAssociation> assocResponse = cassetteStrAssociationValidator.validateCassetteStrAssociation(assoc);
				if (assocResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, assocResponse.getErrorMessages());
				} else {
					assoc = assocResponse.getEntity();
					assoc.setCassetteAssociationSubject(dbEntity);
					validatedAssociations.add(assoc);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedAssociations)) {
			return null;
		}

		return validatedAssociations;
	}

}
