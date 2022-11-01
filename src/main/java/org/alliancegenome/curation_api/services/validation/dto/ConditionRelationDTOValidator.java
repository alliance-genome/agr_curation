package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
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
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	
	public ObjectResponse<ConditionRelation> validateConditionRelationDTO(ConditionRelationDTO dto) {
		ObjectResponse<ConditionRelation> crResponse = new ObjectResponse<ConditionRelation>();
		
		ConditionRelation relation;
		
		Reference reference = null;
		if (StringUtils.isNotBlank(dto.getSingleReference())) {
			reference = referenceService.retrieveFromDbOrLiteratureService(dto.getSingleReference());
			if (reference == null)
				crResponse.addErrorMessage("singleReference", ValidationConstants.INVALID_MESSAGE);
		}
		String refCurie = reference == null ? null : reference.getCurie();
		
		
		String uniqueId = DiseaseAnnotationCurie.getConditionRelationUnique(dto, refCurie);
		SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", uniqueId);

		if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
			relation = new ConditionRelation();
			relation.setUniqueId(uniqueId);
		} else {
			relation = searchResponseRel.getSingleResult();
		}
		relation.setSingleReference(reference);
		
		ObjectResponse<ConditionRelation> aoResponse = validateAuditedObjectDTO(relation, dto);
		relation = aoResponse.getEntity();
		crResponse.addErrorMessages(aoResponse.getErrorMessages());
		
		String relationType = dto.getConditionRelationType();
		if (StringUtils.isBlank(relationType)) {
			crResponse.addErrorMessage("conditionRelationType", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, relationType);
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
		
		if (StringUtils.isNotBlank(dto.getHandle())) {
			relation.setHandle(dto.getHandle());
			if (StringUtils.isBlank(dto.getSingleReference())) {
				crResponse.addErrorMessage("handle", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "singleReference");
			}
		} else {
			if (relation.getHandle() != null) {
				crResponse.addErrorMessage("handle", ValidationConstants.REQUIRED_MESSAGE);
			}
			relation.setHandle(null);
		}
		
		crResponse.setEntity(relation);
		
		return crResponse;
	}
}
