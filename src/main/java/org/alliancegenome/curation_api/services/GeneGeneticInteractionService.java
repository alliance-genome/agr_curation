package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneGeneticInteractionFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneGeneticInteractionService extends BaseEntityCrudService<GeneGeneticInteraction, GeneGeneticInteractionDAO> implements BaseUpsertServiceInterface<GeneGeneticInteraction, PsiMiTabDTO> {

	@Inject GeneGeneticInteractionDAO geneGeneticInteractionDAO;
	@Inject GeneGeneticInteractionFmsDTOValidator geneGeneticInteractionValidator;
	@Inject CurieMintService curieMintService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneGeneticInteractionDAO);
	}

	@Override
	public ObjectResponse<GeneGeneticInteraction> getByIdentifier(String identifier) {
		GeneGeneticInteraction interaction = findByAlternativeFields(List.of("interactionId", "uniqueId"), identifier);
		return new ObjectResponse<GeneGeneticInteraction>(interaction);
	}

	public void preloadInteractionIds() {
		geneGeneticInteractionValidator.setExistingInteractionMap(geneGeneticInteractionDAO.findInteractionIdMap());
	}

	public List<Long> getAllIds() {
		return geneGeneticInteractionDAO.getAllIds();
	}

	public List<GeneGeneticInteraction> findByIds(List<Long> ids) {
		return geneGeneticInteractionDAO.findByIds(ids);
	}

	@Override
	@Transactional
	public ObjectResponse<GeneGeneticInteraction> upsert(PsiMiTabDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return geneGeneticInteractionValidator.validateGeneGeneticInteractionFmsDTO(dto);
	}

	// SCRUM-6463 — mint on the curator create paths. Both are exposed as REST endpoints (POST / and
	// POST /multiple), so both need it. The bulk upsert path mints in the FMS DTO validator instead,
	// which is where the persist lives for that route.
	@Override
	@Transactional
	public ObjectResponse<GeneGeneticInteraction> create(GeneGeneticInteraction uiEntity) {
		curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.GENETIC_INTERACTION);
		return super.create(uiEntity);
	}

	@Override
	@Transactional
	public ObjectListResponse<GeneGeneticInteraction> create(List<GeneGeneticInteraction> uiEntities) {
		uiEntities.forEach(uiEntity -> curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.GENETIC_INTERACTION));
		return super.create(uiEntities);
	}
}
