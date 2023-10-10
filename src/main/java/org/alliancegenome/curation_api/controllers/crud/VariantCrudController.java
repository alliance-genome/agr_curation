package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.interfaces.crud.VariantCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.VariantExecutor;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.VariantService;

@RequestScoped
public class VariantCrudController extends BaseDTOCrudController<VariantService, Variant, VariantDTO, VariantDAO> implements VariantCrudInterface {

	@Inject
	VariantService variantService;

	@Inject
	VariantExecutor variantExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(variantService);
	}

	@Override
	public APIResponse updateVariants(String dataProvider, List<VariantDTO> variantData) {
		return variantExecutor.runLoad(dataProvider, variantData);
	}

}
