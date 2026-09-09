package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationFmsDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.HTPExpressionDatasetSampleAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class HTPExpressionDatasetSampleAnnotationService extends BaseEntityCrudService<HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationDAO> implements BaseUpsertServiceInterface<HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationFmsDTO> {
	
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;
	@Inject HTPExpressionDatasetSampleAnnotationFmsDTOValidator htpExpressionDatasetSampleAnnotationFmsDtoValidator;
	@Inject CurieMintService curieMintService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(htpExpressionDatasetSampleAnnotationDAO);
	}
	@Override
	@Transactional
	public ObjectResponse<HTPExpressionDatasetSampleAnnotation> upsert(HTPExpressionDatasetSampleAnnotationFmsDTO htpExpressionDatasetSampleAnnotationData, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return htpExpressionDatasetSampleAnnotationFmsDtoValidator.validateHTPExpressionDatasetSampleAnnotationFmsDTO(htpExpressionDatasetSampleAnnotationData, backendBulkDataProvider);
	}

	public List<Long> getAnnotationIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = htpExpressionDatasetSampleAnnotationDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	// SCRUM-6463 — mint on the curator create paths. Both are exposed as REST endpoints (POST / and
	// POST /multiple), so both need it. The bulk upsert path mints in the FMS DTO validator instead,
	// which is where the persist lives for that route.
	@Override
	@Transactional
	public ObjectResponse<HTPExpressionDatasetSampleAnnotation> create(HTPExpressionDatasetSampleAnnotation uiEntity) {
		curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.HTP_EXPRESSION_SAMPLE);
		return super.create(uiEntity);
	}

	@Override
	@Transactional
	public ObjectListResponse<HTPExpressionDatasetSampleAnnotation> create(List<HTPExpressionDatasetSampleAnnotation> uiEntities) {
		uiEntities.forEach(uiEntity -> curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.HTP_EXPRESSION_SAMPLE));
		return super.create(uiEntities);
	}
}
