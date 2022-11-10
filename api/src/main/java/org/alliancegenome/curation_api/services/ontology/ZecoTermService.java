package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class ZecoTermService extends BaseOntologyTermService<ZECOTerm, ZecoTermDAO> {

	@Inject ZecoTermDAO zecoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(zecoTermDAO);
	}

}
