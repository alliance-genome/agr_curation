package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.PhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class PhenotypeAnnotationService extends BaseAnnotationCrudService<PhenotypeAnnotation, PhenotypeAnnotationDAO> {

	@Inject
	PhenotypeAnnotationDAO phenotypeAnnotationDAO;
	@Inject
	PersonService personService;
	@Inject
	PersonDAO personDAO;
	@Inject
	GenomicEntityService genomicEntityService;
	@Inject
	ReferenceService referenceService;
	@Inject
	AGMPhenotypeAnnotationService agmPhenotypeAnnotationService;

	HashMap<String, List<PhenotypeFmsDTO>> unprocessedAnnotationsMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(phenotypeAnnotationDAO);
	}
	
	public ObjectResponse<PhenotypeAnnotation> get(String identifier) {
		SearchResponse<PhenotypeAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<PhenotypeAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<PhenotypeAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<PhenotypeAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<PhenotypeAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<PhenotypeAnnotation>();
	}
	
	@Override
	@Transactional
	public ObjectResponse<PhenotypeAnnotation> delete(Long id) {
		deprecateOrDeleteAnnotationAndNotes(id, true, "Phenotype annotation DELETE API call", false);
		ObjectResponse<PhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Transactional
	public PhenotypeAnnotation deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecateAnnotation) {
		PhenotypeAnnotation annotation = phenotypeAnnotationDAO.find(id);

		if (annotation == null) {
			String errorMessage = "Could not find Phenotype Annotation with id: " + id;
			if (throwApiError) {
				ObjectResponse<PhenotypeAnnotation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}

		if (deprecateAnnotation) {
			if (!annotation.getObsolete()) {
				annotation.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					annotation.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					annotation.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				annotation.setDateUpdated(OffsetDateTime.now());
				return phenotypeAnnotationDAO.persist(annotation);
			} else {
				return annotation;
			}
		} else {
			phenotypeAnnotationDAO.remove(id);
		}

		return null;
	}

	public List<Long> getAllReferencedConditionRelationIds() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		
		List<Long> paIds = phenotypeAnnotationDAO.findAllIds().getResults();
		pdh.startProcess("Checking PAs for referenced Conditions ", paIds.size());
		
		List<Long> conditionRelationIds = new ArrayList<>();
		paIds.forEach(paId -> {
			PhenotypeAnnotation annotation = phenotypeAnnotationDAO.find(paId);
			if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
				List<Long> crIds = annotation.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
				conditionRelationIds.addAll(crIds);
			}
			pdh.progressProcess();
		});
		pdh.finishProcess();
		
		return conditionRelationIds;
	}
	
	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return getAnnotationIdsByDataProvider(phenotypeAnnotationDAO, dataProvider);
	}
	
	protected <D extends BaseSQLDAO<?>> List<Long> getAnnotationIdsByDataProvider(D dao, BackendBulkDataProvider dataProvider) {
		//TODO: need some rules for XenBase submissions here
		
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		
		if(StringUtils.equals(dataProvider.sourceOrganization, "RGD"))
			params.put(EntityFieldConstants.DA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		
		List<Long> annotationIds = phenotypeAnnotationDAO.findFilteredIds(params);
		annotationIds.removeIf(Objects::isNull);
		
		if (StringUtils.equals(dataProvider.toString(), "HUMAN")) {
			Map<String, Object> newParams = new HashMap<>();
			newParams.put(EntityFieldConstants.SECONDARY_DATA_PROVIDER, dataProvider.sourceOrganization);
			newParams.put(EntityFieldConstants.DA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
			List<Long> additionalIds = phenotypeAnnotationDAO.findFilteredIds(newParams);
			annotationIds.addAll(additionalIds);
		}
		
		return annotationIds;
	}
	
	public Long upsertPrimaryAnnotation(PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		if (StringUtils.isBlank(dto.getObjectId()))
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.REQUIRED_MESSAGE);
		GenomicEntity phenotypeAnnotationSubject = genomicEntityService.findByIdentifierString(dto.getObjectId());
		if (phenotypeAnnotationSubject == null)
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
		
		
		if (phenotypeAnnotationSubject instanceof AffectedGenomicModel) {
			AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationService.upsertPrimaryAnnotation((AffectedGenomicModel) phenotypeAnnotationSubject, dto, dataProvider);
			return annotation.getId();
		} else if (phenotypeAnnotationSubject instanceof Allele) {
			// TODO: point to AllelePhenotypeAnnotationService method
		} else if (phenotypeAnnotationSubject instanceof Gene) {
			// TODO: point to GenePhenotypeAnnotationService method
		} else {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
		}
		
		return null;
	}

	public void addInferredOrAssertedEntities(PhenotypeFmsDTO dto, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		for (String primaryGeneticEntityCurie : dto.getPrimaryGeneticEntityIds()) {
			GenomicEntity primaryAnnotationSubject = genomicEntityService.findByIdentifierString(dto.getObjectId());
			if (primaryAnnotationSubject == null)
				throw new ObjectValidationException(dto, "primaryGeneticEntityIds - " + ValidationConstants.INVALID_MESSAGE + " (" + primaryGeneticEntityCurie + ")");
		
			if (primaryAnnotationSubject instanceof AffectedGenomicModel) {
				agmPhenotypeAnnotationService.addInferredOrAssertedEntities((AffectedGenomicModel) primaryAnnotationSubject, dto, idsAdded, dataProvider);
			} else if (primaryAnnotationSubject instanceof Allele) {
				// TODO: point to AllelePhenotypeAnnotationService method
			} else if (primaryAnnotationSubject instanceof Gene) {
				// TODO: point to GenePhenotypeAnnotationService method
			} else {
				throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			}
		}
	} 
	
}
