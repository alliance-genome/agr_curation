package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.helpers.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.associations.ConstructCassetteAssociationValidator;
import org.alliancegenome.curation_api.services.validation.associations.ConstructGenomicEntityAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.ConstructComponentSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.ConstructFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.ConstructSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.ConstructSynonymSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructValidator extends ReagentValidator {

	@Inject ConstructDAO constructDAO;
	@Inject CurieMintService curieMintService;
	@Inject ConstructComponentSlotAnnotationValidator constructComponentValidator;
	@Inject ReferenceValidator referenceValidator;
	@Inject ConstructSymbolSlotAnnotationValidator constructSymbolValidator;
	@Inject ConstructFullNameSlotAnnotationValidator constructFullNameValidator;
	@Inject ConstructSynonymSlotAnnotationValidator constructSynonymValidator;
	@Inject ConstructGenomicEntityAssociationValidator constructGenomicEntityAssociationValidator;
	@Inject ConstructCassetteAssociationValidator constructCassetteAssociationValidator;

	private String errorMessage;

	/**
	 * @param manageSubResourceFields whether the calling endpoint's JSON view carries the construct's lists and
	 *        associations: references, related notes, secondary IDs, synonyms, components and the genomic entity and
	 *        cassette associations. An endpoint whose view omits them leaves the stored ones as they are; the detail
	 *        page writes them through the construct's sub-resources.
	 */
	public Construct validateConstructUpdate(Construct uiEntity, Boolean manageSubResourceFields) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Construct: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Construct ID provided");
			throw new ApiErrorException(response);
		}
		Construct dbEntity = constructDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Construct with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Construct ID has not been
			// found
		}

		return validateConstruct(uiEntity, dbEntity, manageSubResourceFields);
	}

	public Construct validateConstructCreate(Construct uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Construct";

		Construct dbEntity = new Construct();

		return validateConstruct(uiEntity, dbEntity, true);
	}

	public Construct validateConstruct(Construct uiEntity, Construct dbEntity, Boolean manageSubResourceFields) {
		if (manageSubResourceFields) {
			validateConstructReferences(uiEntity, dbEntity);
		}

		dbEntity = (Construct) validateCommonReagentFields(uiEntity, dbEntity, VocabularyConstants.CONSTRUCT_NOTE_TYPES_VOCABULARY_TERM_SET, true, manageSubResourceFields);

		ConstructSymbolSlotAnnotation symbol = validateConstructSymbol(uiEntity, dbEntity);
		ConstructFullNameSlotAnnotation fullName = validateConstructFullName(uiEntity, dbEntity);

		List<ConstructSynonymSlotAnnotation> synonyms = null;
		List<ConstructComponentSlotAnnotation> components = null;
		List<ConstructGenomicEntityAssociation> geAssociations = null;
		List<ConstructCassetteAssociation> cassetteAssociations = null;
		if (manageSubResourceFields) {
			synonyms = validateConstructSynonyms(uiEntity, dbEntity);
			components = validateConstructComponents(uiEntity, dbEntity);
			geAssociations = validateConstructGenomicEntityAssociations(uiEntity, dbEntity);
			cassetteAssociations = validateConstructCassetteAssociations(uiEntity.getConstructCassetteAssociations(), dbEntity);
		}

		String uniqueId = validateUniqueId(manageSubResourceFields ? uiEntity : withStoredComponents(uiEntity, dbEntity), dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		// SCRUM-6536: mint an AGRKB curie for a NEW construct that has none, before persist so the insert
		// writes it. A curator-supplied curie is left alone. The getId() == null guard is load-bearing, as
		// for genes: validateConstruct serves create and update, and validateCommonReagentFields ->
		// SubmittedObjectValidator copies the payload's curie unconditionally, so an update omitting curie
		// nulls it and an unguarded mint would then silently replace the construct's AGRKB id.
		if (dbEntity.getId() == null) {
			curieMintService.mintCurieIfAbsent(dbEntity);
		}
		dbEntity = constructDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleConstruct(dbEntity);
		}
		dbEntity.setConstructSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleConstruct(dbEntity);
		}
		dbEntity.setConstructFullName(fullName);

		if (manageSubResourceFields) {
			applyConstructLists(dbEntity, synonyms, components, geAssociations, cassetteAssociations);
		}

		return dbEntity;
	}

	private void validateConstructReferences(Construct uiEntity, Construct dbEntity) {
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
	}

	private void applyConstructLists(Construct dbEntity, List<ConstructSynonymSlotAnnotation> synonyms, List<ConstructComponentSlotAnnotation> components,
			List<ConstructGenomicEntityAssociation> geAssociations, List<ConstructCassetteAssociation> cassetteAssociations) {
		if (dbEntity.getConstructSynonyms() != null) {
			dbEntity.getConstructSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getConstructSynonyms() == null) {
				dbEntity.setConstructSynonyms(new ArrayList<>());
			}
			dbEntity.getConstructSynonyms().addAll(synonyms);
		}

		if (dbEntity.getConstructComponents() != null) {
			dbEntity.getConstructComponents().clear();
		}
		if (components != null) {
			if (dbEntity.getConstructComponents() == null) {
				dbEntity.setConstructComponents(new ArrayList<>());
			}
			dbEntity.getConstructComponents().addAll(components);
		}

		if (dbEntity.getConstructGenomicEntityAssociations() != null) {
			dbEntity.getConstructGenomicEntityAssociations().clear();
		}
		if (geAssociations != null) {
			if (dbEntity.getConstructGenomicEntityAssociations() == null) {
				dbEntity.setConstructGenomicEntityAssociations(new ArrayList<>());
			}
			dbEntity.getConstructGenomicEntityAssociations().addAll(geAssociations);
		}

		replaceCassetteAssociations(dbEntity, cassetteAssociations);
	}

	/**
	 * Sets the construct's cassette associations to the validated ones: an association the construct holds and the
	 * list omits is removed, and each new one is assigned to the construct.
	 *
	 * @param dbEntity the construct the associations belong to
	 * @param cassetteAssociations the validated associations, the stored ones among them as the construct holds them
	 */
	public void replaceCassetteAssociations(Construct dbEntity, List<ConstructCassetteAssociation> cassetteAssociations) {
		if (dbEntity.getConstructCassetteAssociations() == null) {
			dbEntity.setConstructCassetteAssociations(new ArrayList<>());
		}
		dbEntity.getConstructCassetteAssociations().retainAll(cassetteAssociations);
		for (ConstructCassetteAssociation association : cassetteAssociations) {
			if (association.getId() == null) {
				association.setConstructAssociationSubject(dbEntity);
				dbEntity.getConstructCassetteAssociations().add(association);
			}
		}
	}

	/**
	 * The construct the unique ID is built from when the payload carries none of its components: the submitted
	 * symbol and full name, with the components the construct holds.
	 */
	private Construct withStoredComponents(Construct uiEntity, Construct dbEntity) {
		Construct uniqueIdSource = new Construct();
		uniqueIdSource.setConstructSymbol(uiEntity.getConstructSymbol());
		uniqueIdSource.setConstructFullName(uiEntity.getConstructFullName());
		uniqueIdSource.setConstructComponents(dbEntity.getConstructComponents());
		return uniqueIdSource;
	}

	public List<ConstructSynonymSlotAnnotation> validateSynonymReplacement(Construct dbEntity, List<ConstructSynonymSlotAnnotation> incomingSynonyms) {
		return validateSlotAnnotationReplacement(dbEntity, incomingSynonyms, dbEntity.getConstructSynonyms(), "constructSynonyms", "displayText", "Synonyms",
			constructSynonymValidator::validateConstructSynonymSlotAnnotation, ConstructSynonymSlotAnnotation::setSingleConstruct);
	}

	public List<Note> validateRelatedNoteReplacement(Construct dbEntity, List<Note> incomingNotes) {
		return validateRelatedNoteReplacement(dbEntity, incomingNotes, VocabularyConstants.CONSTRUCT_NOTE_TYPES_VOCABULARY_TERM_SET);
	}

	public List<Reference> validateReferenceReplacement(Construct dbEntity, List<Reference> incomingReferences) {
		return validateReferenceReplacement(dbEntity.getId(), incomingReferences, dbEntity.getReferences());
	}

	/**
	 * Validates the IDs the construct's secondary IDs sub-resource is to hold, in submitted order. Each must be
	 * present and not blank, and no two alike.
	 *
	 * @param dbEntity the construct the secondary IDs belong to
	 * @param incomingSecondaryIds the submitted secondary IDs
	 * @return the secondary IDs, trimmed, one per submitted entry
	 * @throws ApiErrorException when the list is missing or any entry is rejected, with each entry's errors keyed by
	 *         its index
	 */
	public List<String> validateSecondaryIdReplacement(Construct dbEntity, List<String> incomingSecondaryIds) {
		response = new ObjectResponse<>();
		String field = "secondaryIdentifiers";
		String errorTitle = "Could not update Secondary IDs for: [" + dbEntity.getId() + "]";

		if (incomingSecondaryIds == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		List<String> validatedSecondaryIds = new ArrayList<>();
		boolean allValid = true;
		Set<String> seenSecondaryIds = new HashSet<>();
		for (int ix = 0; ix < incomingSecondaryIds.size(); ix++) {
			String secondaryId = StringUtils.trimToNull(incomingSecondaryIds.get(ix));
			if (secondaryId == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("secondaryId", ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (!seenSecondaryIds.add(secondaryId)) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("secondaryId", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			validatedSecondaryIds.add(secondaryId);
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validatedSecondaryIds;
	}

	/**
	 * Validates the associations the construct's cassette associations sub-resource is to hold, with the same rules
	 * as associations saved through the construct itself: an entry with an id is one the construct holds and is kept
	 * as stored, an entry without one is a new association, and no two name the same cassette with the same relation.
	 *
	 * @param dbEntity the construct the associations belong to
	 * @param incomingAssociations the submitted associations
	 * @return the validated associations, one per submitted entry
	 * @throws ApiErrorException when the list is missing or any entry is rejected, with each entry's errors keyed by
	 *         its index
	 */
	public List<ConstructCassetteAssociation> validateCassetteAssociationReplacement(Construct dbEntity, List<ConstructCassetteAssociation> incomingAssociations) {
		response = new ObjectResponse<>();
		String field = "constructCassetteAssociations";
		String errorTitle = "Could not update Cassettes for: [" + dbEntity.getId() + "]";

		if (incomingAssociations == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		List<ConstructCassetteAssociation> validatedAssociations = validateConstructCassetteAssociations(incomingAssociations, dbEntity);
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validatedAssociations;
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

	public String validateUniqueId(Construct uiEntity, Construct dbEntity) {

		if (dbEntity.getDataProvider() == null) {
			return null;
		}

		String uniqueId = ConstructUniqueIdHelper.getConstructUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<Construct> response = constructDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	private ConstructSymbolSlotAnnotation validateConstructSymbol(Construct uiEntity, Construct dbEntity) {
		String field = "constructSymbol";

		if (uiEntity.getConstructSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<ConstructSymbolSlotAnnotation> symbolResponse = constructSymbolValidator.validateConstructSymbolSlotAnnotation(uiEntity.getConstructSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private ConstructFullNameSlotAnnotation validateConstructFullName(Construct uiEntity, Construct dbEntity) {
		if (uiEntity.getConstructFullName() == null) {
			return null;
		}

		String field = "constructFullName";

		ObjectResponse<ConstructFullNameSlotAnnotation> nameResponse = constructFullNameValidator.validateConstructFullNameSlotAnnotation(uiEntity.getConstructFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<ConstructSynonymSlotAnnotation> validateConstructSynonyms(Construct uiEntity, Construct dbEntity) {
		String field = "constructSynonyms";

		List<ConstructSynonymSlotAnnotation> validatedSynonyms = new ArrayList<ConstructSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructSynonyms())) {
			for (int ix = 0; ix < uiEntity.getConstructSynonyms().size(); ix++) {
				ConstructSynonymSlotAnnotation syn = uiEntity.getConstructSynonyms().get(ix);
				ObjectResponse<ConstructSynonymSlotAnnotation> synResponse = constructSynonymValidator.validateConstructSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleConstruct(dbEntity);
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

	private List<ConstructComponentSlotAnnotation> validateConstructComponents(Construct uiEntity, Construct dbEntity) {
		String field = "constructComponents";

		List<ConstructComponentSlotAnnotation> validatedComponents = new ArrayList<ConstructComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructComponents())) {
			for (int ix = 0; ix < uiEntity.getConstructComponents().size(); ix++) {
				ConstructComponentSlotAnnotation comp = uiEntity.getConstructComponents().get(ix);
				ObjectResponse<ConstructComponentSlotAnnotation> synResponse = constructComponentValidator.validateConstructComponentSlotAnnotation(comp);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					comp = synResponse.getEntity();
					comp.setSingleConstruct(dbEntity);
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

	private List<ConstructGenomicEntityAssociation> validateConstructGenomicEntityAssociations(Construct uiEntity, Construct dbEntity) {
		String field = "constructGenomicEntityAssociations";

		List<ConstructGenomicEntityAssociation> validatedAssociations = new ArrayList<ConstructGenomicEntityAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructGenomicEntityAssociations())) {
			for (int ix = 0; ix < uiEntity.getConstructGenomicEntityAssociations().size(); ix++) {
				ConstructGenomicEntityAssociation gea = uiEntity.getConstructGenomicEntityAssociations().get(ix);
				ObjectResponse<ConstructGenomicEntityAssociation> geaResponse = constructGenomicEntityAssociationValidator.validateConstructGenomicEntityAssociation(gea);
				if (geaResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, geaResponse.getErrorMessages());
				} else {
					gea = geaResponse.getEntity();
					gea.setConstructAssociationSubject(dbEntity);
					validatedAssociations.add(gea);
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

	private List<ConstructCassetteAssociation> validateConstructCassetteAssociations(List<ConstructCassetteAssociation> incomingAssociations, Construct dbEntity) {
		String field = "constructCassetteAssociations";

		Map<Long, ConstructCassetteAssociation> storedById = new HashMap<>();
		if (CollectionUtils.isNotEmpty(dbEntity.getConstructCassetteAssociations())) {
			for (ConstructCassetteAssociation stored : dbEntity.getConstructCassetteAssociations()) {
				storedById.put(stored.getId(), stored);
			}
		}

		List<ConstructCassetteAssociation> validatedAssociations = new ArrayList<>();
		if (CollectionUtils.isEmpty(incomingAssociations)) {
			return validatedAssociations;
		}

		Set<String> associationKeys = new HashSet<>();
		Boolean allValid = true;
		for (int ix = 0; ix < incomingAssociations.size(); ix++) {
			ConstructCassetteAssociation cca = incomingAssociations.get(ix);
			if (cca == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("constructCassetteAssociationObject", ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (cca.getId() != null) {
				ConstructCassetteAssociation stored = storedById.get(cca.getId());
				if (stored == null) {
					allValid = false;
					response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
					continue;
				}
				cca = stored;
			} else {
				ObjectResponse<ConstructCassetteAssociation> ccaResponse = constructCassetteAssociationValidator.validateConstructCassetteAssociation(cca);
				if (ccaResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, ccaResponse.getErrorMessages());
					continue;
				}
				cca = ccaResponse.getEntity();
			}
			if (!associationKeys.add(cassetteAssociationKey(cca))) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("constructCassetteAssociationObject", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			validatedAssociations.add(cca);
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
		}

		return validatedAssociations;
	}

	private String cassetteAssociationKey(ConstructCassetteAssociation association) {
		Long cassetteId = association.getConstructCassetteAssociationObject() == null ? null : association.getConstructCassetteAssociationObject().getId();
		String relationName = association.getRelation() == null ? null : association.getRelation().getName();
		return cassetteId + "|" + relationName;
	}

}
