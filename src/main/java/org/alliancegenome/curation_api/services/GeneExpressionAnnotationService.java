package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.config.RestDefaultObjectMapper;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneExpressionAnnotationFmsDTOValidator;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@RequestScoped
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface<GeneExpressionAnnotation, ConsolidatedGeneExpressionFmsDTO> {

	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;
	@Inject GeneExpressionExperimentService geneExpressionExperimentService;
	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;

	private ObjectMapper mapper;

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
		mapper = new RestDefaultObjectMapper().getMapper();

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

	public Response getAnnotationsForIndexing(Integer page, Integer limit, HashMap<String, Object> params) {
		Pagination pagination = new Pagination(page, limit);
		HashMap<String, Object> allparams = new HashMap<>();
		allparams.put("internal", false);
		allparams.put("obsolete", false);
		if (params != null) {
			allparams.putAll(params);
		}
		Map<String, GeneExpressionExperiment> experiments = new HashMap<>();
		geneExpressionExperimentService.findByParams(pagination, allparams)
			.getResults().forEach(exp -> {
				experiments.put(exp.getUniqueId(), exp);
			});

		List<GeneExpressionAnnotation> annotations = new ArrayList<>();
		for (GeneExpressionAnnotation geneExpressionAnnotation : findByParams(pagination, allparams).getResults()) {
			if (geneExpressionAnnotation.getDataProvider().getAbbreviation().equals("MGI") || geneExpressionAnnotation.getDataProvider().getAbbreviation().equals("WB")) {
				ConsolidatedGeneExpressionFmsDTO geneExpressionFmsDTO = new ConsolidatedGeneExpressionFmsDTO();
				geneExpressionFmsDTO.setGeneId(geneExpressionAnnotation.getExpressionAnnotationSubject().getPrimaryExternalId());
				geneExpressionFmsDTO.setAssay(geneExpressionAnnotation.getExpressionAssayUsed().getCurie());
				String experimentId = geneExpressionAnnotationUniqueIdHelper.generateExperimentId(geneExpressionFmsDTO, geneExpressionAnnotation.getEvidenceItem().getCurie());
				if (experiments.get(experimentId) != null && experiments.get(experimentId).getCrossReferences() != null) {
					geneExpressionAnnotation.setCrossReferences(experiments.get(experimentId).getCrossReferences());
				}
			}
			annotations.add(geneExpressionAnnotation);
		}
		try {
			SearchResponse<GeneExpressionAnnotation> resp = new SearchResponse<>();
			resp.setResults(annotations);
			String json = mapper.writerWithView(View.ForPublic.class).writeValueAsString(resp);
			return Response.ok(json).build();
		} catch (JsonProcessingException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing JSON").build();
		}
	}
}
