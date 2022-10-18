package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ConditionRelationDTOValidator extends BaseDTOValidator {

	@Inject ConditionRelationDAO conditionRelationDAO;
    @Inject VocabularyTermDAO vocabularyTermDAO;
    @Inject ExperimentalConditionDTOValidator experimentalConditionDtoValidator;
    @Inject ExperimentalConditionDAO experimentalConditionDAO;
    
    public ObjectResponse<ConditionRelation> validateConditionRelationDTO(ConditionRelationDTO dto) {
    	ObjectResponse<ConditionRelation> crResponse = new ObjectResponse<ConditionRelation>();
    	
    	ConditionRelation relation;
    	String uniqueId = DiseaseAnnotationCurie.getConditionRelationUnique(dto);
    	SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", uniqueId);
		if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
			relation = new ConditionRelation();
			relation.setUniqueId(uniqueId);
		} else {
			relation = searchResponseRel.getSingleResult();
		}
    	
    	ObjectResponse<ConditionRelation> aoResponse = validateAuditedObjectDTO(relation, dto);
    	relation = aoResponse.getEntity();
    	crResponse.addErrorMessages(aoResponse.getErrorMessages());
    	
    	String relationType = dto.getConditionRelationType();
    	if (StringUtils.isBlank(relationType)) {
    		crResponse.addErrorMessage("conditionRelationType", ValidationConstants.REQUIRED_MESSAGE);
    	} else {
    		VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(relationType, VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
			if (conditionRelationTypeTerm == null)
				crResponse.addErrorMessage("conditionRelationType", ValidationConstants.INVALID_MESSAGE);
			relation.setConditionRelationType(conditionRelationTypeTerm);
    	}
    	
    	List<ExperimentalCondition> conditions = new ArrayList<>();
    	if (CollectionUtils.isEmpty(dto.getConditions())) {
    		crResponse.addErrorMessage("conditions", ValidationConstants.REQUIRED_MESSAGE);
    	} else {
    		for (ExperimentalConditionDTO conditionDTO : dto.getConditions()) {
    			ObjectResponse<ExperimentalCondition> ecResponse = experimentalConditionDtoValidator.validateExperimentalConditionDTO(conditionDTO);
    			if (ecResponse.hasErrors()) {
    				crResponse.addErrorMessage("conditions", ecResponse.errorMessagesString());
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
