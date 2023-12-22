package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConditionRelationDTOValidator extends BaseDTOValidator {

	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ExperimentalConditionDTOValidator experimentalConditionDtoValidator;
	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceService referenceService;

	public ObjectResponse<ConditionRelation> validateConditionRelationDTO(ConditionRelationDTO dto) {
		ObjectResponse<ConditionRelation> crResponse = new ObjectResponse<ConditionRelation>();

		ConditionRelation relation;

		Reference reference = null;
		if (StringUtils.isNotBlank(dto.getReferenceCurie())) {
			reference = referenceService.retrieveFromDbOrLiteratureService(dto.getReferenceCurie());
			if (reference == null)
				crResponse.addErrorMessage("reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getReferenceCurie() + ")");
		}
		String refCurie = reference == null ? null : reference.getCurie();

		String uniqueId = DiseaseAnnotationUniqueIdHelper.getConditionRelationUniqueId(dto, refCurie);
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

		String relationType = dto.getConditionRelationTypeName();
		if (StringUtils.isBlank(relationType)) {
			crResponse.addErrorMessage("condition_relation_type_name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			VocabularyTerm conditionRelationTypeTerm = vocabularyTermService.getTermInVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, relationType).getEntity();
			if (conditionRelationTypeTerm == null)
				crResponse.addErrorMessage("condition_relation_type_name", ValidationConstants.INVALID_MESSAGE + " (" + relationType + ")");
			relation.setConditionRelationType(conditionRelationTypeTerm);
		}

		List<ExperimentalCondition> conditions = new ArrayList<>();
		if (CollectionUtils.isEmpty(dto.getConditionDtos())) {
			crResponse.addErrorMessage("condition_dtos", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			for (ExperimentalConditionDTO conditionDTO : dto.getConditionDtos()) {
				ObjectResponse<ExperimentalCondition> ecResponse = experimentalConditionDtoValidator.validateExperimentalConditionDTO(conditionDTO);
				if (ecResponse.hasErrors()) {
					crResponse.addErrorMessage("condition_dtos", ecResponse.errorMessagesString());
				} else {
					conditions.add(experimentalConditionDAO.persist(ecResponse.getEntity()));
				}
			}
		}
		relation.setConditions(conditions);

		if (StringUtils.isNotBlank(dto.getHandle())) {
			relation.setHandle(dto.getHandle());
			if (StringUtils.isBlank(dto.getReferenceCurie())) {
				crResponse.addErrorMessage("handle", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "reference_curie");
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
