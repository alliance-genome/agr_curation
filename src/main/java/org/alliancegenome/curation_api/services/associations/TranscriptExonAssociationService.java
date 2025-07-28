package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptExonAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TranscriptExonAssociationService extends BaseEntityCrudService<TranscriptExonAssociation, TranscriptExonAssociationDAO> {

	@Inject TranscriptExonAssociationDAO transcriptExonAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptExonAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = transcriptExonAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<TranscriptExonAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptExonAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptExonAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptExonAssociation> resp = transcriptExonAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptExonAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubjectAndObject(TranscriptExonAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptExonAssociation> currentSubjectAssociations = transcript.getTranscriptExonAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptExonAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
		
		Exon exon = association.getTranscriptExonAssociationObject();
		
		List<TranscriptExonAssociation> currentObjectAssociations = exon.getTranscriptExonAssociations();
		if (currentObjectAssociations == null) {
			currentObjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentObjectAssociationIds = currentObjectAssociations.stream()
				.map(TranscriptExonAssociation::getId).collect(Collectors.toList());
		
		if (!currentObjectAssociationIds.contains(association.getId())) {
			currentObjectAssociations.add(association);
		}
	}
}
