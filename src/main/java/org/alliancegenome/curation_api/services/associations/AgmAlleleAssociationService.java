package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.AgmAlleleAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AgmAlleleAssociationService extends BaseAssociationDTOCrudService<AgmAlleleAssociation, AgmAlleleAssociationDTO, AgmAlleleAssociationDAO> implements BaseUpsertServiceInterface<AgmAlleleAssociation, AgmAlleleAssociationDTO> {
	
	@Inject AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject AgmAlleleAssociationDTOValidator agmAlleleAssociationDtoValidator;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject NoteDAO noteDAO;
	@Inject AlleleDAO strDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmAlleleAssociationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AgmAlleleAssociation> upsert(AgmAlleleAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return agmAlleleAssociationDtoValidator.validateAgmAlleleAssociationDTO(dto, dataProvider);
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = agmAlleleAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);
		return associationIds;
	}

	public ObjectResponse<AgmAlleleAssociation> getAssociation(Long agmId, String relationName, Long alleleId) {
		AgmAlleleAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT + ".id", agmId);
		params.put(EntityFieldConstants.RELATION + ".name", relationName);
		params.put(EntityFieldConstants.AGM_ALLELE_ASSOCIATION_OBJECT + ".id", alleleId);

		SearchResponse<AgmAlleleAssociation> resp = agmAlleleAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AgmAlleleAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
