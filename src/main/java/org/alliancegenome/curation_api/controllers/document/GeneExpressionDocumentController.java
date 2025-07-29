package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.interfaces.document.GeneExpressionDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneExpressionDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import jakarta.inject.Inject;

public class GeneExpressionDocumentController implements GeneExpressionDocumentInterface {

	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject GeneExpressionExperimentDAO geneExpressionExperimentDAO;

	@Override
	public SearchResponse<GeneExpressionDocument> findDocument(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);

		SearchResponse<GeneExpressionAnnotation> resp = geneExpressionAnnotationService.findByParams(pagination, params);
		ArrayList<GeneExpressionDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			GeneExpressionDocumentBuilder geneExpressionDocumentBuilder = new GeneExpressionDocumentBuilder();
			for (GeneExpressionAnnotation expressionAnnotation : resp.getResults()) {
				GeneExpressionDocument doc = geneExpressionDocumentBuilder.buildDocument(expressionAnnotation, geneExpressionExperimentDAO);
				list.add(doc);
			}
		}

		SearchResponse<GeneExpressionDocument> ret = new SearchResponse<GeneExpressionDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
	
}
