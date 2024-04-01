package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneGeneticInteractionFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneGeneticInteractionService extends BaseEntityCrudService<GeneGeneticInteraction, GeneGeneticInteractionDAO> {

	@Inject
	GeneGeneticInteractionDAO geneGeneticInteractionDAO;
	@Inject
	GeneGeneticInteractionFmsDTOValidator geneGeneticInteractionValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneGeneticInteractionDAO);
	}
	
	public ObjectResponse<GeneGeneticInteraction> get(String identifier) {
		List<String> identifierFields = List.of("interactionId", "uniqueId");
		GeneGeneticInteraction interaction = findByAlternativeFields(identifierFields, identifier);
		return new ObjectResponse<GeneGeneticInteraction>(interaction);
	}

	@Transactional
	public GeneGeneticInteraction upsert(PsiMiTabDTO dto) throws ObjectUpdateException {
		GeneGeneticInteraction interaction = geneGeneticInteractionValidator.validateGeneGeneticInteractionFmsDTO(dto);
		return geneGeneticInteractionDAO.persist(interaction);
	}

}
