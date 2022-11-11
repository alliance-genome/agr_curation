package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class BiologicalEntityService extends BaseEntityCrudService<BiologicalEntity, BiologicalEntityDAO> {

	@Inject BiologicalEntityDAO biologicalEntityDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}
}
