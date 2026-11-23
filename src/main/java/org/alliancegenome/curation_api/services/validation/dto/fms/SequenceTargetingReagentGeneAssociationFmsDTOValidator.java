package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.dao.associations.SequenceTargetingReagentGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.VocabularyTermService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SequenceTargetingReagentGeneAssociationFmsDTOValidator {
	@Inject
	SequenceTargetingReagentDAO sqtrDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	GeneService geneService;
	@Inject
	SequenceTargetingReagentGeneAssociationDAO sqtrGeneAssociationDAO;

	public List<SequenceTargetingReagentGeneAssociation> validateSQTRGeneAssociationFmsDTO(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		List<SequenceTargetingReagentGeneAssociation> strGeneAssociations = new ArrayList<>();
		ObjectResponse<SequenceTargetingReagent> sqtrResponse = new ObjectResponse<>();

		SequenceTargetingReagent sqtr;
		SearchResponse<SequenceTargetingReagent> sqtrSearchResponse = sqtrDAO.findByField("primaryExternalId",
				dto.getPrimaryId());

		if (sqtrSearchResponse == null || sqtrSearchResponse.getSingleResult() == null) {
			sqtrResponse.addErrorMessage("primaryExternalId",
					ValidationConstants.INVALID_MESSAGE + " (" + dto.getPrimaryId() + ")");
			sqtr = new SequenceTargetingReagent();
		} else {
			sqtr = sqtrSearchResponse.getSingleResult();
		}

		VocabularyTerm relation;
		SearchResponse<VocabularyTerm> relationSearchResponse = vocabularyTermService.findByField("name", "targets");
		if (relationSearchResponse == null || relationSearchResponse.getSingleResult() == null) {
			sqtrResponse.addErrorMessage("relation", ValidationConstants.INVALID_MESSAGE + " (" + "targets" + ")");
			relation = new VocabularyTerm();
		} else {
			relation = relationSearchResponse.getSingleResult();
		}

		if (dto.getTargetGeneIds() != null) {
			Set<Long> geneIdsSeen = new HashSet<>();
			for (String geneId : dto.getTargetGeneIds()) {
				Gene gene = geneService.findByIdentifierString(geneId);

				if (gene == null) {
					sqtrResponse.addErrorMessage("targetGeneIds",
							ValidationConstants.INVALID_MESSAGE + " (" + geneId + ")");
				} else if (geneIdsSeen.add(gene.getId())) {
					strGeneAssociations.add(findOrCreateAssociation(sqtr, relation, gene));
				}

			}
		}

		if (sqtrResponse.hasErrors()) {
			throw new ObjectValidationException(dto, sqtrResponse.errorMessagesString());
		}

		return strGeneAssociations;
	}

	// SCRUM-6685: the association an earlier load created for this STR, relation and gene, so that
	// reloading a file does not add a further copy of it (ZFIN's STRs had one copy per load run).
	private SequenceTargetingReagentGeneAssociation findOrCreateAssociation(SequenceTargetingReagent sqtr, VocabularyTerm relation, Gene gene) {
		if (sqtr.getId() != null && relation.getId() != null) {
			Map<String, Object> params = new HashMap<>();
			params.put("sequenceTargetingReagentAssociationSubject.id", sqtr.getId());
			params.put("relation.id", relation.getId());
			params.put("sequenceTargetingReagentGeneAssociationObject.id", gene.getId());
			SequenceTargetingReagentGeneAssociation existing = sqtrGeneAssociationDAO.findByParams(params).getSingleResult();
			if (existing != null) {
				return existing;
			}
		}

		SequenceTargetingReagentGeneAssociation association = new SequenceTargetingReagentGeneAssociation();
		association.setSequenceTargetingReagentAssociationSubject(sqtr);
		association.setRelation(relation);
		association.setSequenceTargetingReagentGeneAssociationObject(gene);
		return association;
	}
}
