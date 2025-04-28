package org.alliancegenome.curation_api.services.associations;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.AlleleConstructAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.AlleleConstructAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.AlleleConstructAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleConstructAssociationService extends BaseAssociationDTOCrudService<AlleleConstructAssociation, AlleleConstructAssociationDTO, AlleleConstructAssociationDAO> implements BaseUpsertServiceInterface<AlleleConstructAssociation, AlleleConstructAssociationDTO> {

	@Inject AlleleConstructAssociationDAO alleleConstructAssociationDAO;
	@Inject AlleleConstructAssociationValidator alleleConstructAssociationValidator;
	@Inject AlleleConstructAssociationDTOValidator alleleConstructAssociationDtoValidator;
	@Inject AlleleDAO alleleDAO;
	@Inject NoteDAO noteDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleConstructAssociationDAO);
	}

	@Transactional
	public ObjectResponse<AlleleConstructAssociation> upsert(AlleleConstructAssociation uiEntity) {
		AlleleConstructAssociation dbEntity = alleleConstructAssociationValidator.validateAlleleConstructAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = alleleConstructAssociationDAO.persist(dbEntity);
		return new ObjectResponse<AlleleConstructAssociation>(dbEntity);
	}

	public ObjectResponse<AlleleConstructAssociation> validate(AlleleConstructAssociation uiEntity) {
		AlleleConstructAssociation aga = alleleConstructAssociationValidator.validateAlleleConstructAssociation(uiEntity, true, false);
		return new ObjectResponse<AlleleConstructAssociation>(aga);
	}

	@Override
	@Transactional
	public AlleleConstructAssociation upsert(AlleleConstructAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AlleleConstructAssociation association = alleleConstructAssociationDtoValidator.validateAlleleConstructAssociationDTO(dto, dataProvider);
		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.ALLELE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = alleleConstructAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public AlleleConstructAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AlleleConstructAssociation association = alleleConstructAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find AlleleConstructAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AlleleConstructAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!association.getObsolete()) {
				association.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					association.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					association.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				association.setDateUpdated(OffsetDateTime.now());
				return alleleConstructAssociationDAO.persist(association);
			}
			return association;
		}

		Long noteId = null;
		if (association.getRelatedNote() != null) {
			noteId = association.getRelatedNote().getId();
		}
		alleleConstructAssociationDAO.remove(association.getId());
		if (noteId != null) {
			noteDAO.remove(noteId);
		}

		return null;
	}

	public ObjectResponse<AlleleConstructAssociation> getAssociation(Long alleleId, String relationName, Long constructId) {
		AlleleConstructAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("alleleAssociationSubject.id", alleleId);
		params.put("relation.name", relationName);
		params.put("alleleConstructAssociationObject.id", constructId);

		SearchResponse<AlleleConstructAssociation> resp = alleleConstructAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AlleleConstructAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
}
