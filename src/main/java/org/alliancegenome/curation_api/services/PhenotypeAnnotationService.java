package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.CrossReferenceConstants;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.AllelePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.GenePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.PhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.fms.AGMPhenotypeAnnotationFmsDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.fms.AllelePhenotypeAnnotationFmsDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.fms.GenePhenotypeAnnotationFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class PhenotypeAnnotationService extends BaseAnnotationCrudService<PhenotypeAnnotation, PhenotypeAnnotationDAO> {

	@Inject PhenotypeAnnotationDAO phenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;
	@Inject GenomicEntityService genomicEntityService;
	@Inject ReferenceService referenceService;
	@Inject InformationContentEntityService iceService;
	@Inject AGMPhenotypeAnnotationService agmPhenotypeAnnotationService;
	@Inject GenePhenotypeAnnotationService genePhenotypeAnnotationService;
	@Inject AllelePhenotypeAnnotationService allelePhenotypeAnnotationService;
	@Inject GenePhenotypeAnnotationFmsDTOValidator genePhenotypeAnnotationFmsDtoValidator;
	@Inject AllelePhenotypeAnnotationFmsDTOValidator allelePhenotypeAnnotationFmsDtoValidator;
	@Inject AGMPhenotypeAnnotationFmsDTOValidator agmPhenotypeAnnotationFmsDtoValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;

	HashMap<String, List<PhenotypeFmsDTO>> unprocessedAnnotationsMap = new HashMap<>();
	private HashMap<String, String> genomicEntityIdentifierCache = new HashMap<>();
	private Map<String, Long> existingUniqueIds;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(phenotypeAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<PhenotypeAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "Phenotype annotation DELETE API call", false);
		ObjectResponse<PhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAllReferencedConditionRelationIds() {
		return getAllReferencedConditionRelationIds(phenotypeAnnotationDAO);
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		List<Long> existingPhenotypeAnnotationIds = new ArrayList<>();
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(agmPhenotypeAnnotationDAO, dataProvider));
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(genePhenotypeAnnotationDAO, dataProvider));
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(allelePhenotypeAnnotationDAO, dataProvider));
		return existingPhenotypeAnnotationIds;
	}

	protected <D extends BaseSQLDAO<?>> List<Long> getAnnotationIdsByDataProvider(D dao, BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);

		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.PA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}

		List<Long> annotationIds = dao.findIdsByParams(params);
		return annotationIds;
	}

	public void preloadUniqueIds(BackendBulkDataProvider dataProvider) {
		existingUniqueIds = phenotypeAnnotationDAO.findUniqueIdsByDataProvider(dataProvider.sourceOrganization);
		Map<String, Long> inferredGeneIds = phenotypeAnnotationDAO.findInferredGeneIdsByDataProvider(dataProvider.sourceOrganization);
		Map<String, Long> inferredAlleleIds = phenotypeAnnotationDAO.findInferredAlleleIdsByDataProvider(dataProvider.sourceOrganization);
		genePhenotypeAnnotationFmsDtoValidator.setExistingUniqueIds(existingUniqueIds);
		allelePhenotypeAnnotationFmsDtoValidator.setExistingUniqueIds(existingUniqueIds);
		allelePhenotypeAnnotationFmsDtoValidator.setInferredGeneIds(inferredGeneIds);
		agmPhenotypeAnnotationFmsDtoValidator.setExistingUniqueIds(existingUniqueIds);
		agmPhenotypeAnnotationFmsDtoValidator.setInferredGeneIds(inferredGeneIds);
		agmPhenotypeAnnotationFmsDtoValidator.setInferredAlleleIds(inferredAlleleIds);

		// Shared across the three validators on purpose: each one that writes a missing cross reference
		// records it here so the others do not write it again.
		Set<Long> geneIdsWithPhenotypeXref = crossReferenceDAO.findGenomicEntityIdsByPageAreas(CrossReferenceConstants.ALLIANCE_DERIVED_PHENOTYPE_PAGE_AREAS);
		genePhenotypeAnnotationFmsDtoValidator.setGeneIdsWithPhenotypeXref(geneIdsWithPhenotypeXref);
		allelePhenotypeAnnotationFmsDtoValidator.setGeneIdsWithPhenotypeXref(geneIdsWithPhenotypeXref);
		agmPhenotypeAnnotationFmsDtoValidator.setGeneIdsWithPhenotypeXref(geneIdsWithPhenotypeXref);
	}

	private String resolveIdentifier(String objectId) {
		return genomicEntityIdentifierCache.computeIfAbsent(objectId, id -> {
			GenomicEntity entity = genomicEntityService.findByIdentifierString(id);
			return entity != null ? entity.getIdentifier() : null;
		});
	}

	@Transactional
	public Long upsertPrimaryAnnotation(PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		if (StringUtils.isBlank(dto.getObjectId())) {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.REQUIRED_MESSAGE);
		}

		String subjectIdentifier = resolveIdentifier(dto.getObjectId());
		if (subjectIdentifier == null) {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
		}

		// Skip unchanged records by checking pre-loaded uniqueId map
		if (existingUniqueIds != null && dto.getEvidence() != null && StringUtils.isNotBlank(dto.getEvidence().getPublicationId())) {
			String refCurie = dto.getEvidence().getPublicationId();
			if (refCurie.startsWith("OMIM:")) {
				refCurie = refCurie.substring(1);
			}
			InformationContentEntity refEntity = iceService.retrieveFromDbOrLiteratureService(refCurie);
			String refString = refEntity != null ? refEntity.getCurie() : null;
			String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subjectIdentifier, refString);
			if (existingUniqueIds.containsKey(uniqueId)) {
				return existingUniqueIds.get(uniqueId);
			}
		}

		// Not skipped -- do a fresh entity lookup within this transaction
		GenomicEntity phenotypeAnnotationSubject = genomicEntityService.findByIdentifierString(dto.getObjectId());

		Long annotationId;
		if (phenotypeAnnotationSubject instanceof AffectedGenomicModel) {
			AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationService.upsertPrimaryAnnotation((AffectedGenomicModel) phenotypeAnnotationSubject, dto, dataProvider);
			annotationId = annotation.getId();
		} else if (phenotypeAnnotationSubject instanceof Allele) {
			AllelePhenotypeAnnotation annotation = allelePhenotypeAnnotationService.upsertPrimaryAnnotation((Allele) phenotypeAnnotationSubject, dto, dataProvider);
			annotationId = annotation.getId();
		} else if (phenotypeAnnotationSubject instanceof Gene) {
			GenePhenotypeAnnotation annotation = genePhenotypeAnnotationService.upsertPrimaryAnnotation((Gene) phenotypeAnnotationSubject, dto, dataProvider);
			annotationId = annotation.getId();
		} else {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_TYPE_MESSAGE + " (" + dto.getObjectId() + ")");
		}

		// Update in-memory map so subsequent duplicates within the same load are caught
		if (existingUniqueIds != null && annotationId != null) {
			String uniqueId = computeUniqueId(dto, subjectIdentifier);
			if (uniqueId != null) {
				existingUniqueIds.put(uniqueId, annotationId);
			}
		}

		return annotationId;
	}

	private String computeUniqueId(PhenotypeFmsDTO dto, String subjectIdentifier) {
		if (dto.getEvidence() == null || StringUtils.isBlank(dto.getEvidence().getPublicationId())) {
			return null;
		}
		String refCurie = dto.getEvidence().getPublicationId();
		if (refCurie.startsWith("OMIM:")) {
			refCurie = refCurie.substring(1);
		}
		InformationContentEntity refEntity = iceService.retrieveFromDbOrLiteratureService(refCurie);
		String refString = refEntity != null ? refEntity.getCurie() : null;
		return AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subjectIdentifier, refString);
	}

	public void addInferredOrAssertedEntities(PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider, Set<Long> idsAdded) throws ValidationException {
		for (String primaryGeneticEntityCurie : dto.getPrimaryGeneticEntityIds()) {
			GenomicEntity primaryAnnotationSubject = genomicEntityService.findByIdentifierString(primaryGeneticEntityCurie);
			if (primaryAnnotationSubject == null) {
				throw new ObjectValidationException(dto, "primaryGeneticEntityIds - " + ValidationConstants.INVALID_MESSAGE + " (" + primaryGeneticEntityCurie + ")");
			}

			if (primaryAnnotationSubject instanceof AffectedGenomicModel) {
				agmPhenotypeAnnotationService.addInferredOrAssertedEntities((AffectedGenomicModel) primaryAnnotationSubject, dto, dataProvider, idsAdded);
			} else if (primaryAnnotationSubject instanceof Allele) {
				allelePhenotypeAnnotationService.addInferredOrAssertedEntities((Allele) primaryAnnotationSubject, dto, dataProvider, idsAdded);
			} else {
				throw new ObjectValidationException(dto, "primaryGeneticEntityIds - " + ValidationConstants.INVALID_TYPE_MESSAGE + " (" + primaryGeneticEntityCurie + ")");
			}
		}
	}

}
