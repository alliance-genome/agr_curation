package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.document.es.ExpressionDetailDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneExpressionAnnotationFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Getter;

@RequestScoped
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface<GeneExpressionAnnotation, ConsolidatedGeneExpressionFmsDTO> {

	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;
	@Inject GeneExpressionExperimentService geneExpressionExperimentService;
	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;
	@Inject
	org.alliancegenome.curation_api.model.document.builders.ExpressionDetailDocumentBuilder expressionDetailDocumentBuilder;

	@Getter
	private Map<String, Set<String>> experiments;
	@Getter
	private Map<String, Set<CrossReferenceFmsDTO>> crossReferences;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneExpressionAnnotationDAO);
		experiments = new HashMap<>();
		crossReferences = new HashMap<>();
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.EA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> annotationIds = geneExpressionAnnotationDAO.findIdsByParams(params);
		return annotationIds;
	}

	@Transactional
	@Override
	public GeneExpressionAnnotation upsert(ConsolidatedGeneExpressionFmsDTO consolidatedGeneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ValidationException {
		GeneExpressionAnnotation geneExpressionAnnotation = geneExpressionAnnotationFmsDTOValidator.validateAnnotation(consolidatedGeneExpressionFmsDTO, dataProvider, experiments, crossReferences);
		return geneExpressionAnnotationDAO.persist(geneExpressionAnnotation);
	}

	public SearchResponse<ExpressionDetailDocument> getAnnotationsForIndexing(Integer page, Integer limit) {
		Pagination pagination = new Pagination(page, limit);
		HashMap<String, Object> params = new HashMap<>();
		params.put("internal", false);
		params.put("obsolete", false);

		Map<String, GeneExpressionExperiment> experiments = new HashMap<>();
		geneExpressionExperimentService.findByParams(pagination, params)
			.getResults()
			.forEach(exp -> {
				experiments.put(exp.getUniqueId(), exp);
			});

		List<ExpressionDetailDocument> expressionDetailDocumentList = new ArrayList<>();
		for (GeneExpressionAnnotation geneExpressionAnnotation : findByParams(pagination, params).getResults()) {
			expressionDetailDocumentList.add(expressionDetailDocumentBuilder.build(geneExpressionAnnotation, experiments));
		}
		SearchResponse<ExpressionDetailDocument> response = new SearchResponse<>();
		response.setResults(expressionDetailDocumentList);
		return response;
	}
}
