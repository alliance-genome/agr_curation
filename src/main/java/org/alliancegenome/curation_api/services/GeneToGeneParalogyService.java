package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.ParalogyFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneParalogyService extends BaseEntityCrudService<GeneToGeneParalogy, GeneToGeneParalogyDAO> implements BaseUpsertServiceInterface<GeneToGeneParalogy, ParalogyFmsDTO> {

	@Inject
	GeneToGeneParalogyDAO geneToGeneParalogyDAO;
	@Inject
	ParalogyFmsDTOValidator paralogyFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneParalogyDAO);
	}

	public GeneToGeneParalogy upsert(ParalogyFmsDTO paralogyData, BackendBulkDataProvider backendBulkDataProvider)
			throws ObjectUpdateException {
		return paralogyFmsDtoValidator.validateParalogyFmsDTO(paralogyData);
	}

}
