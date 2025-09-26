package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.interfaces.document.GeneExpressionDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneExpressionDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.apache.commons.collections4.CollectionUtils;

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
		if (CollectionUtils.isNotEmpty(resp.getResults())) {
			GeneExpressionDocumentBuilder geneExpressionDocumentBuilder = new GeneExpressionDocumentBuilder();
			
			Map<String, GeneExpressionExperiment> experimentsCache = geneExpressionDocumentBuilder.preloadExperiments(resp.getResults(), geneExpressionExperimentDAO);
			
			for (GeneExpressionAnnotation expressionAnnotation : resp.getResults()) {
				GeneExpressionDocument doc = geneExpressionDocumentBuilder.buildDocument(expressionAnnotation, experimentsCache);
				list.add(doc);
			}
		}

		SearchResponse<GeneExpressionDocument> ret = new SearchResponse<GeneExpressionDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
	
}
