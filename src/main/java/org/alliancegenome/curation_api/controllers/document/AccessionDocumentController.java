package org.alliancegenome.curation_api.controllers.document;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.document.AccessionDocumentInterface;
import org.alliancegenome.curation_api.model.document.es.AccessionSummaryDocument;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class AccessionDocumentController implements AccessionDocumentInterface {

	@Inject GeneDAO geneDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject VariantDAO variantDAO;
	@Inject DoTermDAO doTermDAO;
	
	@Override
	public AccessionSummaryDocument getAccessionSummary() {
		AccessionSummaryDocument doc = new AccessionSummaryDocument();
		doc.setIdsByType(new HashMap<>());

		Log.info("Pulling Disease Id List");
		List<String> diseaseList = doTermDAO.getDoTermCuries();
		doc.getIdsByType().put("disease", diseaseList);
		
		Log.info("Pulling Gene Id List");
		List<String> geneList = geneDAO.getAllGenePrimaryExternalIds();
		doc.getIdsByType().put("gene", geneList);
		
		Log.info("Pulling Allele Id List");
		List<String> alleleList = alleleDAO.getAllAllelePrimaryExternalIds();
		doc.getIdsByType().put("allele", alleleList);
		
		Log.info("Pulling Variant Id List");
		List<String> variantList = variantDAO.getAllVariantPrimaryExternalIds();
		doc.getIdsByType().put("variant", variantList);
		
		Log.info("Finished Pulling all lists");

		return doc;
	}

}
