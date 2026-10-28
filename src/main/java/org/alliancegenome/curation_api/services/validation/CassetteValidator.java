package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
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
import org.alliancegenome.curation_api.services.CurieMintService;
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
	@Inject CurieMintService curieMintService;
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

		return validateCassette(uiEntity, dbEntity, true);
	}

	public Cassette validateCassetteCreate(Cassette uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Cassette";

		Cassette dbEntity = new Cassette();

		return validateCassette(uiEntity, dbEntity, false);
	}

	/**
	 * keepComponents (SCRUM-6746): an update keeps the cassette's free text components and its genomic
	 * entity, transgenic tool and STR associations as stored, whatever the request carries. The
	 * Cassettes table edits the cassette's own fields only and sends back the trimmed table view of
	 * those lists, which would otherwise replace the stored ones; editing them is SCRUM-6752.
	 */
	public Cassette validateCassette(Cassette uiEntity, Cassette dbEntity, boolean keepComponents) {

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
		List<CassetteUseSlotAnnotation> uses = validateCassetteUses(uiEntity, dbEntity);

		List<CassetteComponentSlotAnnotation> components = null;
		List<CassetteGenomicEntityAssociation> geAssociations = null;
		List<CassetteTransgenicToolAssociation> ttAssociations = null;
		List<CassetteStrAssociation> strAssociations = null;
		if (keepComponents) {
			// The unique ID is built from the components too, so it must see the stored ones
			uiEntity.setCassetteComponents(dbEntity.getCassetteComponents());
		} else {
			components = validateCassetteComponents(uiEntity, dbEntity);
			geAssociations = validateCassetteGenomicEntityAssociations(uiEntity, dbEntity);
			ttAssociations = validateCassetteTransgenicToolAssociations(uiEntity, dbEntity);
			strAssociations = validateCassetteStrAssociations(uiEntity, dbEntity);
		}

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		// SCRUM-6693: mint an AGRKB curie for a NEW cassette that has none, before persist so the insert
		// writes it. A curator-supplied curie is left alone. The getId() == null guard is load-bearing, as
		// for constructs: this serves create and update, and validateCommonReagentFields ->
		// SubmittedObjectValidator copies the payload's curie unconditionally, so an update omitting curie
		// nulls it and an unguarded mint would then silently replace the cassette's AGRKB id.
		if (dbEntity.getId() == null) {
			curieMintService.mintCurieIfAbsent(dbEntity);
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


		if (dbEntity.getCassetteUses() != null) {
			dbEntity.getCassetteUses().clear();
		}
		if (uses != null) {
			if (dbEntity.getCassetteUses() == null) {
				dbEntity.setCassetteUses(new ArrayList<>());
			}
			dbEntity.getCassetteUses().addAll(uses);
		}

		if (keepComponents) {
			return dbEntity;
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

	/**
	 * SCRUM-6752: the free text components the Cassettes table's pop-up submits to replace the stored ones. Replacing
	 * them changes the cassette's unique ID, which is built from them, so the new one is checked for uniqueness.
	 *
	 * @return the validated components, each assigned to the cassette, with the cassette's new unique ID set
	 */
	public List<CassetteComponentSlotAnnotation> validateComponentReplacement(Cassette dbEntity, List<CassetteComponentSlotAnnotation> incoming) {
		List<CassetteComponentSlotAnnotation> components = validateReplacement(dbEntity, dbEntity.getCassetteComponents(), incoming, "cassetteComponents", "Free Text Components",
			component -> cassetteComponentValidator.validateCassetteComponentSlotAnnotation(component),
			component -> component.setSingleCassette(dbEntity),
			component -> String.join("|", component.getRelation().getName(), component.getComponentSymbol(),
				component.getTaxon() == null ? "" : component.getTaxon().getCurie(), Objects.toString(component.getTaxonText(), "")),
			"componentSymbol");

		Cassette replaced = new Cassette();
		replaced.setCassetteSymbol(dbEntity.getCassetteSymbol());
		replaced.setCassetteFullName(dbEntity.getCassetteFullName());
		replaced.setCassetteComponents(components);
		String uniqueId = validateUniqueId(replaced, dbEntity);
		if (response.hasErrors()) {
			response.setErrorMessage("Could not update Free Text Components for: [" + dbEntity.getId() + "]");
			throw new ApiErrorException(response);
		}
		dbEntity.setUniqueId(uniqueId);
		return components;
	}

	/** SCRUM-6752: the genomic entity associations the pop-up submits to replace the stored ones. */
	public List<CassetteGenomicEntityAssociation> validateGenomicEntityAssociationReplacement(Cassette dbEntity, List<CassetteGenomicEntityAssociation> incoming) {
		return validateReplacement(dbEntity, dbEntity.getCassetteGenomicEntityAssociations(), incoming, "cassetteGenomicEntityAssociations", "Genomic Entity Components",
			association -> cassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(association),
			association -> association.setCassetteAssociationSubject(dbEntity),
			association -> association.getRelation().getName() + "|" + association.getCassetteGenomicEntityAssociationObject().getId(),
			"cassetteGenomicEntityAssociationObject");
	}

	/** SCRUM-6752: the transgenic tool associations the pop-up submits to replace the stored ones. */
	public List<CassetteTransgenicToolAssociation> validateTransgenicToolAssociationReplacement(Cassette dbEntity, List<CassetteTransgenicToolAssociation> incoming) {
		return validateReplacement(dbEntity, dbEntity.getCassetteTransgenicToolAssociations(), incoming, "cassetteTransgenicToolAssociations", "Transgenic Tool Components",
			association -> cassetteTransgenicToolAssociationValidator.validateCassetteTransgenicToolAssociation(association),
			association -> association.setCassetteAssociationSubject(dbEntity),
			association -> association.getRelation().getName() + "|" + association.getCassetteTransgenicToolAssociationObject().getId(),
			"cassetteTransgenicToolAssociationObject");
	}

	/** SCRUM-6752: the STR associations the pop-up submits to replace the stored ones. */
	public List<CassetteStrAssociation> validateStrAssociationReplacement(Cassette dbEntity, List<CassetteStrAssociation> incoming) {
		return validateReplacement(dbEntity, dbEntity.getCassetteStrAssociations(), incoming, "cassetteStrAssociations", "Sequence Targeting Reagent Components",
			association -> cassetteStrAssociationValidator.validateCassetteStrAssociation(association),
			association -> association.setCassetteAssociationSubject(dbEntity),
			association -> association.getRelation().getName() + "|" + association.getCassetteStrAssociationObject().getId(),
			"cassetteStrAssociationObject");
	}

	/**
	 * Validates a list submitted to replace one of the cassette's stored lists. An entry with an id updates that row,
	 * which must be one of the cassette's and named by no other entry; one without creates a row. No two entries may
	 * share an identity. Rows the list omits are deleted with the cassette's list (orphan removal); the linked entities
	 * hold no list of them.
	 *
	 * @param stored the cassette's stored rows
	 * @param incoming the submitted rows
	 * @param field the field each entry's errors are filed under, keyed by its index
	 * @param label what the rows are, for the error title
	 * @param validate the row's own validator
	 * @param attach assigns a validated row to the cassette
	 * @param identity what makes a row a duplicate of another
	 * @param identityField the field a duplicate is reported on
	 * @return the validated rows, in submitted order
	 * @throws ApiErrorException listing each rejected entry's errors
	 */
	private <T extends AuditedObject> List<T> validateReplacement(Cassette dbEntity, List<T> stored, List<T> incoming, String field, String label,
			Function<T, ObjectResponse<T>> validate, Consumer<T> attach, Function<T, String> identity, String identityField) {
		response = new ObjectResponse<>();
		String errorTitle = "Could not update " + label + " for: [" + dbEntity.getId() + "]";

		if (incoming == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		Set<Long> heldIds = new HashSet<>();
		if (stored != null) {
			stored.forEach(row -> heldIds.add(row.getId()));
		}

		List<T> validated = new ArrayList<>();
		boolean allValid = true;
		Set<Long> seenIds = new HashSet<>();
		Set<String> seenIdentities = new HashSet<>();
		for (int ix = 0; ix < incoming.size(); ix++) {
			T row = incoming.get(ix);
			if (row == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of(identityField, ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (row.getId() != null && !heldIds.contains(row.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
				continue;
			}
			if (row.getId() != null && !seenIds.add(row.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			ObjectResponse<T> rowResponse = validate.apply(row);
			if (rowResponse.getEntity() == null) {
				allValid = false;
				response.addErrorMessages(field, ix, rowResponse.getErrorMessages());
				continue;
			}
			row = rowResponse.getEntity();
			if (!seenIdentities.add(identity.apply(row))) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of(identityField, ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			attach.accept(row);
			validated.add(row);
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validated;
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
