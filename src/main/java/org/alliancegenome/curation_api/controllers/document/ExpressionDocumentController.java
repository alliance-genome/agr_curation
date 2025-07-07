package org.alliancegenome.curation_api.controllers.document;

import org.alliancegenome.curation_api.interfaces.document.GeneExpressionDocumentInterface;
import org.alliancegenome.curation_api.model.document.es.ExpressionDetailDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;

import jakarta.inject.Inject;

public class ExpressionDocumentController implements GeneExpressionDocumentInterface {

	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;

	@Override
	public SearchResponse<ExpressionDetailDocument> getAnnotationsForIndexing(Integer page, Integer limit) {
		return geneExpressionAnnotationService.getAnnotationsForIndexing(page, limit);
	}
}
