package org.alliancegenome.curation_api.crud.controllers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.rest.interfaces.GeneRESTInterface;
import org.alliancegenome.curation_api.services.GeneService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneController extends BaseController<GeneService, Gene, GeneDAO> implements GeneRESTInterface {

	@Inject GeneService geneService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(geneService);
	}

	public List<Gene> getAllGenes() {
		return getAll();
	}

	@Override
	public Gene getByCurie(String id) {
		return geneService.getByIdOrCurie(id);
	}

}
