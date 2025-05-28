package org.alliancegenome.curation_api.controllers.document;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.document.GeneExpressionDocumentInterface;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

public class ExpressionDocumentController implements GeneExpressionDocumentInterface {

	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;

	@Override
	public Response getAnnotationsForIndexing(Integer page, Integer limit, HashMap<String, Object> params) {
		return geneExpressionAnnotationService.getAnnotationsForIndexing(page, limit, params);
	}
}
