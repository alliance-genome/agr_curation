package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.CassetteValidator;
import org.alliancegenome.curation_api.services.validation.dto.CassetteDTOValidator;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.nilosplace.process_display.ProcessDisplayHelper;

/**
 * SCRUM-6535.
 *
 * upsert drives the bulk load through the DTO validator; create and update drive the curation UI
 * through the curator validator. findByIdentifierString and findIdsByIdentifierString come from
 * SubmittedObjectCrudService and are what the association validators use to resolve a Cassette from a
 * submitted identifier.
 */
@RequestScoped
public class CassetteService extends SubmittedObjectCrudService<Cassette, CassetteDTO, CassetteDAO> {

	@Inject CassetteDAO cassetteDAO;
	@Inject CassetteDTOValidator cassetteDtoValidator;
	@Inject CassetteValidator cassetteValidator;
	@Inject ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteDAO);
	}

	@Override
	public ObjectResponse<Cassette> upsert(CassetteDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteDtoValidator.validateCassetteDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> update(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteUpdate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> create(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteCreate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

	/**
	 * Hard-deletes a cassette on a curator's request (SCRUM-6749). The cassette's own slot annotations and its genomic
	 * entity, transgenic tool and STR associations go with it. A construct using the cassette blocks the deletion: its
	 * construct cassette association has to be removed first.
	 *
	 * A cassette saved without any identifier (one created while MaTI was down) is deleted by its database ID, which is
	 * tried when no identifier matches.
	 *
	 * @param identifierString curie, primary external ID or MOD internal ID of the cassette, else its database ID
	 * @return response holding the deleted cassette
	 * @throws ApiErrorException when no cassette matches or the cassette is still in use
	 */
	@Override
	@Transactional
	public ObjectResponse<Cassette> deleteByIdentifier(String identifierString) {
		Cassette cassette = findByIdentifierString(identifierString);
		if (cassette == null && identifierString != null && identifierString.matches("\\d+")) {
			cassette = cassetteDAO.find(Long.valueOf(identifierString));
		}
		if (cassette == null) {
			ObjectResponse<Cassette> response = new ObjectResponse<>();
			response.setErrorMessage("Could not find Cassette with identifier: " + identifierString);
			throw new ApiErrorException(response);
		}
		return delete(cassette);
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> deleteById(Long id) {
		Cassette cassette = cassetteDAO.find(id);
		if (cassette == null) {
			ObjectResponse<Cassette> response = new ObjectResponse<>();
			response.setErrorMessage("Could not find Cassette with id: " + id);
			throw new ApiErrorException(response);
		}
		return delete(cassette);
	}

	private ObjectResponse<Cassette> delete(Cassette cassette) {
		if (CollectionUtils.isNotEmpty(cassette.getConstructCassetteAssociations())) {
			ObjectResponse<Cassette> response = new ObjectResponse<>();
			response.setErrorMessage("Cassette " + cassette.getIdentifier() + " is in use and cannot be deleted: Cassette is a cassette of construct(s)");
			throw new ApiErrorException(response);
		}

		cassetteDAO.remove(cassette.getId());
		return new ObjectResponse<>(cassette);
	}

	// SCRUM-6752: the cassette's four component lists, each read and replaced through a sub-resource of its own for
	// the Cassettes table's component pop-ups. A replacement returns nothing: its rows would be serialized after the
	// transaction closes, so the caller reads them afresh.

	public ObjectListResponse<CassetteComponentSlotAnnotation> getComponents(Long id) {
		return listResponse(findCassetteOrThrow(id).getCassetteComponents());
	}

	@Transactional
	public void replaceComponents(Long id, List<CassetteComponentSlotAnnotation> components) {
		replace(id, components, cassetteValidator::validateComponentReplacement, Cassette::getCassetteComponents, Cassette::setCassetteComponents);
	}

	public ObjectListResponse<CassetteGenomicEntityAssociation> getGenomicEntityAssociations(Long id) {
		return listResponse(findCassetteOrThrow(id).getCassetteGenomicEntityAssociations());
	}

	@Transactional
	public void replaceGenomicEntityAssociations(Long id, List<CassetteGenomicEntityAssociation> associations) {
		replace(id, associations, cassetteValidator::validateGenomicEntityAssociationReplacement, Cassette::getCassetteGenomicEntityAssociations, Cassette::setCassetteGenomicEntityAssociations);
	}

	public ObjectListResponse<CassetteTransgenicToolAssociation> getTransgenicToolAssociations(Long id) {
		return listResponse(findCassetteOrThrow(id).getCassetteTransgenicToolAssociations());
	}

	@Transactional
	public void replaceTransgenicToolAssociations(Long id, List<CassetteTransgenicToolAssociation> associations) {
		replace(id, associations, cassetteValidator::validateTransgenicToolAssociationReplacement, Cassette::getCassetteTransgenicToolAssociations, Cassette::setCassetteTransgenicToolAssociations);
	}

	public ObjectListResponse<CassetteStrAssociation> getStrAssociations(Long id) {
		return listResponse(findCassetteOrThrow(id).getCassetteStrAssociations());
	}

	@Transactional
	public void replaceStrAssociations(Long id, List<CassetteStrAssociation> associations) {
		replace(id, associations, cassetteValidator::validateStrAssociationReplacement, Cassette::getCassetteStrAssociations, Cassette::setCassetteStrAssociations);
	}

	private <T> void replace(Long id, List<T> incoming, BiFunction<Cassette, List<T>, List<T>> validate, Function<Cassette, List<T>> getter, BiConsumer<Cassette, List<T>> setter) {
		Cassette cassette = findCassetteOrThrow(id);
		List<T> validated = validate.apply(cassette, incoming);
		if (getter.apply(cassette) == null) {
			setter.accept(cassette, new ArrayList<>());
		}
		getter.apply(cassette).clear();
		getter.apply(cassette).addAll(validated);
	}

	private static <T> ObjectListResponse<T> listResponse(List<T> rows) {
		return new ObjectListResponse<>(rows == null ? new ArrayList<>() : new ArrayList<>(rows));
	}

	private Cassette findCassetteOrThrow(Long id) {
		Cassette cassette = cassetteDAO.find(id);
		if (cassette == null) {
			ObjectResponse<Cassette> response = new ObjectResponse<>();
			response.addErrorMessage("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		return cassette;
	}

	/** Ids of the cassettes a given MOD owns, for the load's cleanup pass. */
	public List<Long> getCassetteIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> ids = cassetteDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

	/**
	 * Resolves every reference the file names up front, so a load does not make one literature
	 * service call per record.
	 */
	public void preLoadReferences(Set<String> refList) {
		referenceService.cacheReferences();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("Pre Load References", refList.size());
		for (String curie : refList) {
			referenceService.retrieveFromDbOrLiteratureService(curie);
			ph.progressProcess();
		}
		ph.finishProcess();
	}
}
