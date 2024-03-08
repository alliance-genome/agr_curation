package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneMolecularInteractionService extends GeneInteractionService<GeneMolecularInteraction, GeneMolecularInteractionDAO> {

	@Inject
	GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	
	@Inject
	GeneMolecularInteractionFmsDTOValidator geneMolInteractionValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneMolecularInteractionDAO);
	}

	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		return geneMolInteractionValidator.validateGeneMolecularInteractionFmsDTO(dto);
	}

}
