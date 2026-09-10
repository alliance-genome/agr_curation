package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;


@RequestScoped
public class TranscriptGenomicLocationAssociationService extends BaseEntityCrudService<TranscriptGenomicLocationAssociation, TranscriptGenomicLocationAssociationDAO> {

	@Inject TranscriptGenomicLocationAssociationDAO transcriptGenomicLocationAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(Species species) {
		String taxon = needsTaxonFilter(species) ? species.getTaxon().getCurie() : null;
		return transcriptGenomicLocationAssociationDAO.findIdsByDataProvider(species.getDataProvider().getAbbreviation(), taxon);
	}

	private boolean needsTaxonFilter(Species species) {
		return StringUtils.equals(species.getDataProvider().getAbbreviation(), "RGD")
			|| StringUtils.equals(species.getDataProvider().getAbbreviation(), "XB");
	}

	public ObjectResponse<TranscriptGenomicLocationAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptGenomicLocationAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptGenomicLocationAssociation> resp = transcriptGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(TranscriptGenomicLocationAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptGenomicLocationAssociation> currentSubjectAssociations = transcript.getTranscriptGenomicLocationAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
