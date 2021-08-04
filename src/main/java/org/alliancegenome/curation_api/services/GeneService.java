package org.alliancegenome.curation_api.services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.entities.Gene;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseService<Gene, GeneDAO> {

	@Inject GeneDAO geneDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}
	
	public List<Gene> getAllGenes(Pagination pagination) {
		return getAll(pagination);
	}

	@Transactional
	public Gene getByIdOrCurie(String id) {
		Gene gene = geneDAO.getByIdOrCurie(id);
		if(gene != null) {
			gene.getSynonyms().size();
			gene.getSecondaryIdentifiers().size();
		}
		return gene;
	}

}
