package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;

@RequestScoped
public class ZecoTermService extends BaseOntologyTermService<ZecoTerm, ZecoTermDAO> {

	@Inject ZecoTermDAO zecoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(zecoTermDAO);
	}

}
