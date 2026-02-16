package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.interfaces.document.GeneExpressionDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneExpressionDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.inject.Inject;

public class GeneExpressionDocumentController implements GeneExpressionDocumentInterface {

	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject GeneExpressionExperimentDAO geneExpressionExperimentDAO;

	@Override
	public SearchResponse<GeneExpressionDocument> getConsolidateDocumentsForGenes(List<String> geneIds) {
		HashMap<String, Object> params = new HashMap<>();
		
		List<GeneExpressionAnnotation> expressionList = geneExpressionAnnotationService.getByGeneIds(geneIds);
		
		GeneExpressionDocumentBuilder geneExpressionDocumentBuilder = new GeneExpressionDocumentBuilder();
		
		ArrayList<GeneExpressionDocument> geneExpressionDocumentList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(expressionList)) {
			for (GeneExpressionAnnotation expressionAnnotation : expressionList) {
				GeneExpressionDocument doc = geneExpressionDocumentBuilder.buildDocument(expressionAnnotation);
				geneExpressionDocumentList.add(doc);
			}
		}

		List<GeneExpressionDocument> consolidatedList = geneExpressionDocumentBuilder.consolidateExpressionDocuments(geneExpressionDocumentList);

		return new SearchResponse<GeneExpressionDocument>(consolidatedList);
	}

	@Override
	public SearchResponse<String> getGeneIds() {
		return new SearchResponse<>(geneExpressionAnnotationService.getGeneExpressionAnnotationList());
	}
	
}
