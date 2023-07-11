package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AffectedGenomicModelDTOValidator extends BaseDTOValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	public AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto) throws ObjectValidationException {
		ObjectResponse<AffectedGenomicModel> agmResponse = new ObjectResponse<AffectedGenomicModel>();

		AffectedGenomicModel agm = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			agmResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			agm = affectedGenomicModelDAO.find(dto.getCurie());
		}

		if (agm == null)
			agm = new AffectedGenomicModel();

		agm.setCurie(dto.getCurie());

		if (StringUtils.isNotBlank(dto.getName())) {
			agm.setName(dto.getName());
		} else {
			agm.setName(null);
		}

		ObjectResponse<AffectedGenomicModel> geResponse = validateGenomicEntityDTO(agm, dto);
		agmResponse.addErrorMessages(geResponse.getErrorMessages());

		agm = geResponse.getEntity();
		
		VocabularyTerm subtype = null;
		if (StringUtils.isBlank(dto.getSubtypeName())) {
			agmResponse.addErrorMessage("subtype_name", ValidationConstants.REQUIRED_MESSAGE);
		
		} else {
			subtype = vocabularyTermService.getTermInVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY, dto.getSubtypeName()).getEntity();
			if (subtype == null)
				agmResponse.addErrorMessage("subtype_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSubtypeName() + ")");
		}
		agm.setSubtype(subtype);

		if (agmResponse.hasErrors()) {
			throw new ObjectValidationException(dto, agmResponse.errorMessagesString());
		}

		return agm;
	}
}
