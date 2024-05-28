package org.alliancegenome.curation_api.controllers.crud;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneExpressionAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.ExpressionExecutor;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;

import java.util.List;

@RequestScoped
public class GeneExpressionAnnotationCrudController extends BaseEntityCrudController<GeneExpressionAnnotationService, GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements GeneExpressionAnnotationCrudInterface {

	@Inject
	GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject
	ExpressionExecutor expressionExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneExpressionAnnotationService);
	}

	public ObjectResponse<GeneExpressionAnnotation> getByIdentifier(String identifierString) {
		return geneExpressionAnnotationService.getByIdentifier(identifierString);
	}

	public APIResponse updateExpressionAnnotations(String dataProvider, List<GeneExpressionFmsDTO> annotations) {
		return expressionExecutor.runLoadApi(geneExpressionAnnotationService, dataProvider, annotations);
	}
}

