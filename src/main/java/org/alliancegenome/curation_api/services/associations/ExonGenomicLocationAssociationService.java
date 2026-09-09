package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.ExonGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.associations.ExonGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExonGenomicLocationAssociationService extends BaseEntityCrudService<ExonGenomicLocationAssociation, ExonGenomicLocationAssociationDAO> {

	@Inject ExonGenomicLocationAssociationDAO exonGenomicLocationAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(exonGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsBySpecies(Species species) {
		String taxon = needsTaxonFilter(species) ? species.getTaxon().getCurie() : null;
		return exonGenomicLocationAssociationDAO.findIdsByDataProvider(species.getDataProvider().getAbbreviation(), taxon);
	}

	private boolean needsTaxonFilter(Species species) {
		return StringUtils.equals(species.getDataProvider().getAbbreviation(), "RGD")
			|| StringUtils.equals(species.getDataProvider().getAbbreviation(), "XB");
	}

	public ObjectResponse<ExonGenomicLocationAssociation> getLocationAssociation(Long exonId, Long assemblyComponentId) {
		ExonGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("exonAssociationSubject.id", exonId);
		params.put("exonGenomicLocationAssociationObject.id", assemblyComponentId);

		SearchResponse<ExonGenomicLocationAssociation> resp = exonGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<ExonGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(ExonGenomicLocationAssociation association) {
		Exon exon = association.getExonAssociationSubject();
		
		List<ExonGenomicLocationAssociation> currentSubjectAssociations = exon.getExonGenomicLocationAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(ExonGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
