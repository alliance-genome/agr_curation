package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.enums.FmsConditionRelation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConditionRelationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ExperimentalConditionFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConditionRelationFmsDTOValidator {
	
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ExperimentalConditionFmsDTOValidator experimentalConditionFmsDtoValidator;

	public ObjectResponse<ConditionRelation> validateConditionRelationFmsDTO (ConditionRelationFmsDTO dto) {
		ObjectResponse<ConditionRelation> crResponse = new ObjectResponse<>();
		
		ConditionRelation relation;
		FmsConditionRelation fmsConditionRelation = null;
		
		if (StringUtils.isBlank(dto.getConditionRelationType())) {
			crResponse.addErrorMessage("conditionRelationType", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			fmsConditionRelation = FmsConditionRelation.findByName(dto.getConditionRelationType());
			if (fmsConditionRelation == null)
				crResponse.addErrorMessage("conditionRelationType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConditionRelationType() + ")");
		}

		String relationTypeString = fmsConditionRelation == null ? null : fmsConditionRelation.agrRelation;
		String uniqueId = AnnotationUniqueIdHelper.getConditionRelationUniqueId(dto, relationTypeString);
		SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", uniqueId);

		if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
			relation = new ConditionRelation();
			relation.setUniqueId(uniqueId);
		} else {
			relation = searchResponseRel.getSingleResult();
		}
		
		if (relationTypeString != null) {
			VocabularyTerm conditionRelationTypeTerm = vocabularyTermService.getTermInVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, relationTypeString).getEntity();
			if (conditionRelationTypeTerm == null)
				crResponse.addErrorMessage("conditionRelationType", ValidationConstants.INVALID_MESSAGE + " (" + relationTypeString + ")");
			relation.setConditionRelationType(conditionRelationTypeTerm);
		}
		
		List<ExperimentalCondition> conditions = new ArrayList<>();
		if (CollectionUtils.isEmpty(dto.getConditions())) {
			crResponse.addErrorMessage("conditions", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			for (ExperimentalConditionFmsDTO conditionFmsDTO : dto.getConditions()) {
				ObjectResponse<ExperimentalCondition> ecResponse = experimentalConditionFmsDtoValidator.validateExperimentalConditionFmsDTO(conditionFmsDTO);
				if (ecResponse.hasErrors()) {
					crResponse.addErrorMessage("condition_dtos", ecResponse.errorMessagesString());
				} else {
					conditions.add(experimentalConditionDAO.persist(ecResponse.getEntity()));
				}
			}
		}
		relation.setConditions(conditions);
		
		crResponse.setEntity(relation);
		
		return crResponse;
	}
}
