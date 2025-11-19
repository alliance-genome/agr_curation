package org.alliancegenome.curation_api.controllers.document;

import org.alliancegenome.curation_api.interfaces.document.GeneExpressionRibbonDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneExpressionRibbonDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionRibbonSummaryDocument;
import jakarta.inject.Inject;

public class GeneExpressionRibbonDocumentController implements GeneExpressionRibbonDocumentInterface{

	@Inject GeneExpressionRibbonDocumentBuilder builder;

	@Override
	public GeneExpressionRibbonSummaryDocument findDocument() {
		
		return builder.build();
		
	}
}
