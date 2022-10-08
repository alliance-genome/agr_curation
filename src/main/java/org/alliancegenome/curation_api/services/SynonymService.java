package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.validators.SynonymValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class SynonymService extends BaseEntityCrudService<Synonym, SynonymDAO> {

	@Inject SynonymDAO synonymDAO;
	@Inject SynonymValidator synonymValidator;
	@Inject AuditedObjectService<Synonym, SynonymDTO> auditedObjectService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(synonymDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Synonym> update(Synonym uiEntity) {
		Synonym dbEntity = synonymValidator.validateSynonym(uiEntity, true);
		return new ObjectResponse<>(synonymDAO.persist(dbEntity));
	}
	
	public ObjectResponse<Synonym> validateSynonymDTO(SynonymDTO dto) {
		Synonym synonym = new Synonym();
		ObjectResponse<Synonym> synonymResponse = auditedObjectService.validateAuditedObjectDTO(synonym, dto);
		synonym = synonymResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getName())) {
			synonymResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			synonym.setName(dto.getName());
		}
		
		synonymResponse.setEntity(synonym);
		
		return synonymResponse;
	}
}
