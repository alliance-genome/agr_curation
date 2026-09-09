package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneMolecularInteractionFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneMolecularInteractionService extends BaseEntityCrudService<GeneMolecularInteraction, GeneMolecularInteractionDAO> implements BaseUpsertServiceInterface<GeneMolecularInteraction, PsiMiTabDTO> {

	@Inject GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject GeneMolecularInteractionFmsDTOValidator geneMolInteractionValidator;
	@Inject CurieMintService curieMintService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneMolecularInteractionDAO);
	}

	@Override
	public ObjectResponse<GeneMolecularInteraction> getByIdentifier(String identifier) {
		return new ObjectResponse<>(findByAlternativeFields(List.of("interactionId", "uniqueId"), identifier));
	}

	public void preloadInteractionIds() {
		geneMolInteractionValidator.setExistingInteractionMap(geneMolecularInteractionDAO.findInteractionIdMap());
	}

	public List<Long> getAllIds() {
		return geneMolecularInteractionDAO.getAllIds();
	}

	public List<GeneMolecularInteraction> findByIds(List<Long> ids) {
		return geneMolecularInteractionDAO.findByIds(ids);
	}

	@Override
	@Transactional
	public ObjectResponse<GeneMolecularInteraction> upsert(PsiMiTabDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return geneMolInteractionValidator.validateGeneMolecularInteractionFmsDTO(dto);
	}

	// SCRUM-6463 — mint on the curator create paths. Both are exposed as REST endpoints (POST / and
	// POST /multiple), so both need it. The bulk upsert path mints in the FMS DTO validator instead,
	// which is where the persist lives for that route.
	@Override
	@Transactional
	public ObjectResponse<GeneMolecularInteraction> create(GeneMolecularInteraction uiEntity) {
		curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.MOLECULAR_INTERACTION);
		return super.create(uiEntity);
	}

	@Override
	@Transactional
	public ObjectListResponse<GeneMolecularInteraction> create(List<GeneMolecularInteraction> uiEntities) {
		uiEntities.forEach(uiEntity -> curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.MOLECULAR_INTERACTION));
		return super.create(uiEntities);
	}
}
