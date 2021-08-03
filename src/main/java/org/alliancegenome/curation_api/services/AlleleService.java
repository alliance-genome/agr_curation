package org.alliancegenome.curation_api.services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseService<Allele, AlleleDAO> {
	
	@Inject
	private AlleleDAO alleleDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}
	
	public List<Allele> getAllAlleles() {
		return getAll();
	}




}
