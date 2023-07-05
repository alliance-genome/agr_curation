package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class OrthologyFmsDTOValidator extends BaseDTOValidator {

	@Inject
	GeneToGeneOrthologyGeneratedDAO generatedOrthologyDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	@Transactional
	public GeneToGeneOrthologyGenerated validateOrthologyFmsDTO(OrthologyFmsDTO dto, Map<String, Map<String, VocabularyTerm>> validTerms) throws ObjectValidationException {

		ObjectResponse<GeneToGeneOrthologyGenerated> orthologyResponse = new ObjectResponse<GeneToGeneOrthologyGenerated>();

		String subjectGeneCurie = null;
		String objectGeneCurie = null;
		
		GeneToGeneOrthologyGenerated orthoPair = null;

		if (StringUtils.isBlank(dto.getGene1())) {
			orthologyResponse.addErrorMessage("gene1", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectGeneCurie = convertToAgrCurie(dto.getGene1(), dto.getGene1Species());
		}
		if (StringUtils.isBlank(dto.getGene2())) {
			orthologyResponse.addErrorMessage("gene2", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectGeneCurie = convertToAgrCurie(dto.getGene2(), dto.getGene2Species());
		}
		
		if (subjectGeneCurie != null && objectGeneCurie != null) {
			Map<String, Object> params = new HashMap<>();
			params.put("subjectGene.curie", subjectGeneCurie);
			params.put("objectGene.curie", objectGeneCurie);
			SearchResponse<GeneToGeneOrthologyGenerated> searchResponse = generatedOrthologyDAO.findByParams(null, params);
			if (searchResponse != null && searchResponse.getSingleResult() != null)
				orthoPair = searchResponse.getSingleResult();
		}

		if (orthoPair == null)
			orthoPair = new GeneToGeneOrthologyGenerated();

		Gene subjectGene = null;
		if (StringUtils.isNotBlank(dto.getGene1())) {
			subjectGene = geneDAO.find(subjectGeneCurie);
			if (subjectGene == null) {
				orthologyResponse.addErrorMessage("gene1", ValidationConstants.INVALID_MESSAGE + " (" + subjectGeneCurie + ")");
			} else {
				if (dto.getGene1Species() == null) {
					orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.REQUIRED_MESSAGE);
				} else {
					ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get("NCBITaxon:" + dto.getGene1Species());
					NCBITaxonTerm subjectTaxon = taxonResponse.getEntity();
					if (subjectTaxon == null) {
						orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene1Species() + ")");
					} else if (!sameGenus(subjectTaxon, subjectGene.getTaxon())) {
						orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene1Species() + ") for gene " + subjectGene.getCurie());
					}
				}
			}
		}
		orthoPair.setSubjectGene(subjectGene);
		
		Gene objectGene = null;
		if (StringUtils.isNotBlank(dto.getGene2())) {
			objectGene = geneDAO.find(objectGeneCurie);
			if (objectGene == null) {
				orthologyResponse.addErrorMessage("gene2", ValidationConstants.INVALID_MESSAGE + " (" + objectGeneCurie + ")");
			} else {
				if (dto.getGene2Species() == null) {
					orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.REQUIRED_MESSAGE);
				} else {
					ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get("NCBITaxon:" + dto.getGene2Species());
					NCBITaxonTerm objectTaxon = taxonResponse.getEntity();
					if (objectTaxon == null) {
						orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene2Species() + ")");
					} else if (!sameGenus(objectTaxon, objectGene.getTaxon())) {
						orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene2Species() + ") for gene " + objectGene.getCurie());
					}
				}
			}
		}
		orthoPair.setObjectGene(objectGene);

		VocabularyTerm isBestScore = null;
		if (StringUtils.isBlank(dto.getIsBestScore())) {
			orthologyResponse.addErrorMessage("isBestScore", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			isBestScore = validTerms.get(VocabularyConstants.ORTHOLOGY_BEST_SCORE_VOCABULARY).get(dto.getIsBestScore());
			if (isBestScore == null)
				orthologyResponse.addErrorMessage("isBestScore", ValidationConstants.INVALID_MESSAGE + " (" + dto.getIsBestScore() + ")");
		}
		orthoPair.setIsBestScore(isBestScore);

		VocabularyTerm isBestScoreReverse = null;
		if (StringUtils.isBlank(dto.getIsBestRevScore())) {
			orthologyResponse.addErrorMessage("isBestRevScore", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			isBestScoreReverse = validTerms.get(VocabularyConstants.ORTHOLOGY_BEST_REVERSE_SCORE_VOCABULARY_TERM_SET).get(dto.getIsBestRevScore());
			if (isBestScoreReverse == null)
				orthologyResponse.addErrorMessage("isBestRevScore", ValidationConstants.INVALID_MESSAGE + " (" + dto.getIsBestRevScore() + ")");
		}
		orthoPair.setIsBestScoreReverse(isBestScoreReverse);

		List<VocabularyTerm> predictionMethodsMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsMatched())) {
			predictionMethodsMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsMatched()) {
				VocabularyTerm method = validTerms.get(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY).get(methodName);
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsMatched.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsMatched(predictionMethodsMatched);

		List<VocabularyTerm> predictionMethodsNotMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotMatched())) {
			predictionMethodsNotMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotMatched()) {
				VocabularyTerm method = validTerms.get(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY).get(methodName);
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsNotMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotMatched.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsNotMatched(predictionMethodsNotMatched);

		List<VocabularyTerm> predictionMethodsNotCalled = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotCalled())) {
			predictionMethodsNotCalled = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotCalled()) {
				VocabularyTerm method = validTerms.get(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY).get(methodName);
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsNotCalled", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotCalled.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsNotCalled(predictionMethodsNotCalled);

		VocabularyTerm confidence = null;
		if (StringUtils.isBlank(dto.getConfidence())) {
			orthologyResponse.addErrorMessage("confidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			confidence = validTerms.get(VocabularyConstants.ORTHOLOGY_CONFIDENCE_VOCABULARY).get(dto.getConfidence());
			if (confidence == null)
				orthologyResponse.addErrorMessage("confidence", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConfidence() + ")");
		}
		orthoPair.setConfidence(confidence);

		orthoPair.setModerateFilter(dto.getModerateFilter());
		
		orthoPair.setStrictFilter(dto.getStrictFilter());

		if (orthologyResponse.hasErrors())
			throw new ObjectValidationException(dto, orthologyResponse.errorMessagesString());
		
		return generatedOrthologyDAO.persist(orthoPair);

	}

	private boolean sameGenus(NCBITaxonTerm subjectTaxon, NCBITaxonTerm objectTaxon) {
		String subjectGenus = subjectTaxon.getName().substring(0, subjectTaxon.getName().indexOf(" "));
		String objectGenus = objectTaxon.getName().substring(0, objectTaxon.getName().indexOf(" "));
		if (subjectGenus.equals(objectGenus))
			return true;
		return false;
	}

	private String convertToAgrCurie(String curie, Integer taxonId) {
		curie = curie.replaceFirst("^DRSC:", "");
		if (curie.indexOf(":") == -1) {
			String prefix = BackendBulkDataProvider.getCuriePrefixFromTaxonCurie("NCBITaxon:" + taxonId);
			if (prefix != null)
				curie = prefix + curie;
		}
			
		return curie;
	}

}
