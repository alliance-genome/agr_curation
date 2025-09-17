package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneExpressionAnnotationFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Getter;

@RequestScoped
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface<GeneExpressionAnnotation, ConsolidatedGeneExpressionFmsDTO> {

	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneExpressionExperimentDAO geneExpressionExperimentDAO;
	@Inject GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;
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
	
	@Override
	@Transactional
	public GeneExpressionAnnotation deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		GeneExpressionAnnotation annotation = geneExpressionAnnotationDAO.find(id);
		if (annotation != null) {
			GeneExpressionExperiment experiment = annotation.getExpressionExperiment();
			if (experiment != null) {
				experiment.getExpressionAnnotations().remove(annotation);
				geneExpressionExperimentDAO.merge(experiment);
			}
			geneExpressionAnnotationDAO.remove(id);
		} else {
			String errorMessage = "Could not find GeneExpressionAnnotation with id: " + id;
			if (throwApiError) {
				ObjectResponse<Gene> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}
}
