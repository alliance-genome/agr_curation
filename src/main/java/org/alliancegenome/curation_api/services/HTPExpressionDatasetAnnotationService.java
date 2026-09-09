package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.HTPExpressionDatasetAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class HTPExpressionDatasetAnnotationService extends BaseEntityCrudService<HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationDAO> implements BaseUpsertServiceInterface<HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationFmsDTO> {
	
	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	@Inject HTPExpressionDatasetAnnotationFmsDTOValidator htpExpressionDatasetAnnotationFmsDtoValidator;
	@Inject CurieMintService curieMintService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(htpExpressionDatasetAnnotationDAO);
	}

	@Override
	public ObjectResponse<HTPExpressionDatasetAnnotation> upsert(HTPExpressionDatasetAnnotationFmsDTO htpExpressionDatasetAnnotationData, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return htpExpressionDatasetAnnotationFmsDtoValidator.validateHTPExpressionDatasetAnnotationFmsDTO(htpExpressionDatasetAnnotationData, backendBulkDataProvider);
	}

	public List<Long> getAnnotationIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = htpExpressionDatasetAnnotationDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	public List<Long> getAllHTPDatasetSearchResultIds() {
		return htpExpressionDatasetAnnotationDAO.getAllHTPDatasetSearchResultIds();
	}

	public List<HTPExpressionDatasetAnnotation> findByIds(List<Long> ids) {
		return htpExpressionDatasetAnnotationDAO.findByIds(ids);
	}

	// SCRUM-6463 — mint on the curator create paths. Both are exposed as REST endpoints (POST / and
	// POST /multiple), so both need it. The bulk upsert path mints in the FMS DTO validator instead,
	// which is where the persist lives for that route.
	@Override
	@Transactional
	public ObjectResponse<HTPExpressionDatasetAnnotation> create(HTPExpressionDatasetAnnotation uiEntity) {
		curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.HTP_EXPRESSION_DATASET);
		return super.create(uiEntity);
	}

	@Override
	@Transactional
	public ObjectListResponse<HTPExpressionDatasetAnnotation> create(List<HTPExpressionDatasetAnnotation> uiEntities) {
		uiEntities.forEach(uiEntity -> curieMintService.mintCurieIfAbsent(uiEntity, MatiSubdomain.HTP_EXPRESSION_DATASET));
		return super.create(uiEntities);
	}
}