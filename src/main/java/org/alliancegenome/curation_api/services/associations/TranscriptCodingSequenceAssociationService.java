package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptCodingSequenceAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptCodingSequenceAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TranscriptCodingSequenceAssociationService extends BaseEntityCrudService<TranscriptCodingSequenceAssociation, TranscriptCodingSequenceAssociationDAO> {

	@Inject TranscriptCodingSequenceAssociationDAO transcriptCodingSequenceAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptCodingSequenceAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		String taxon = needsTaxonFilter(dataProvider) ? dataProvider.canonicalTaxonCurie : null;
		return transcriptCodingSequenceAssociationDAO.findIdsByDataProvider(dataProvider.sourceOrganization, taxon);
	}

	private boolean needsTaxonFilter(BackendBulkDataProvider dataProvider) {
		return StringUtils.equals(dataProvider.sourceOrganization, "RGD")
			|| StringUtils.equals(dataProvider.sourceOrganization, "XB");
	}

	public ObjectResponse<TranscriptCodingSequenceAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptCodingSequenceAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptCodingSequenceAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptCodingSequenceAssociation> resp = transcriptCodingSequenceAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptCodingSequenceAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubjectAndObject(TranscriptCodingSequenceAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptCodingSequenceAssociation> currentSubjectAssociations = transcript.getTranscriptCodingSequenceAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptCodingSequenceAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
		
		CodingSequence codingSequence = association.getTranscriptCodingSequenceAssociationObject();
		
		List<TranscriptCodingSequenceAssociation> currentObjectAssociations = codingSequence.getTranscriptCodingSequenceAssociations();
		if (currentObjectAssociations == null) {
			currentObjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentObjectAssociationIds = currentObjectAssociations.stream()
				.map(TranscriptCodingSequenceAssociation::getId).collect(Collectors.toList());
		
		if (!currentObjectAssociationIds.contains(association.getId())) {
			currentObjectAssociations.add(association);
		}
	}
}
