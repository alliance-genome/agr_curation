package org.alliancegenome.curation_api.services.helpers.annotations;

import jakarta.enterprise.context.RequestScoped;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.lang3.ObjectUtils;

@RequestScoped
public class GeneExpressionAnnotationUniqueIdHelper {

	//	UniqueID =
	//	assayId | evidenceCurie | geneId | stageTermId | stageName | whereExpressedStatement | anatomicalStructureTermId | cellularComponentTermId
	public String generateUniqueId(ConsolidatedGeneExpressionFmsDTO consolidatedGeneExpressionFmsDTO, String referenceCurie) {
		UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();
		uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getAssay());
		uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getGeneId());
		uniqueIdGeneratorHelper.add(referenceCurie);
		if (consolidatedGeneExpressionFmsDTO.getWhenExpressed() != null) {
			uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getWhenExpressed().getStageTermId());
			uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getWhenExpressed().getStageName());
		}
		if (consolidatedGeneExpressionFmsDTO.getWhereExpressed() != null) {
			uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement());
			uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId());
			uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId());
		}
		return uniqueIdGeneratorHelper.getUniqueId();
	}

	// used to consolidate annotation DTOs before validating
	public String generateHash(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();

		uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getAssay());
		uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getGeneId());
		if (ObjectUtils.isNotEmpty(geneExpressionFmsDTO.getEvidence())) {
			if (ObjectUtils.isNotEmpty(geneExpressionFmsDTO.getEvidence().getPublicationId())) {
				uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getEvidence().getPublicationId());
			}
		}
		if (ObjectUtils.isNotEmpty(geneExpressionFmsDTO.getWhenExpressed())) {
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhenExpressed().getStageTermId());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhenExpressed().getStageName());
		}
		if (ObjectUtils.isNotEmpty(geneExpressionFmsDTO.getWhereExpressed())) {
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId());
		}
		return uniqueIdGeneratorHelper.getUniqueId();
	}

	//	UniqueID =	geneId | evidenceReferenceCurie | assayId
	public String generateExperimentId(ConsolidatedGeneExpressionFmsDTO consolidatedGeneExpressionFmsDTO, String referenceCurie) {
		UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();
		uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getGeneId());
		uniqueIdGeneratorHelper.add(referenceCurie);
		uniqueIdGeneratorHelper.add(consolidatedGeneExpressionFmsDTO.getAssay());
		return uniqueIdGeneratorHelper.getUniqueId();
	}
}
