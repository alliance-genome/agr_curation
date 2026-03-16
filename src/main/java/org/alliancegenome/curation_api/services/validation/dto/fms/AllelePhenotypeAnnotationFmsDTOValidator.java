package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AllelePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.services.helpers.GenePhenotypeAnnotationXrefHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AllelePhenotypeAnnotationFmsDTOValidator extends PhenotypeAnnotationFmsDTOValidator {

	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject GenomicEntityService genomicEntityService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject GenePhenotypeAnnotationXrefHelper xrefHelper;

	private HashMap<String, Long> genomicEntityIdCache = new HashMap<>();

	public AllelePhenotypeAnnotation validatePrimaryAnnotation(Allele subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		ObjectResponse<AllelePhenotypeAnnotation> apaResponse = new ObjectResponse<AllelePhenotypeAnnotation>();
		AllelePhenotypeAnnotation annotation = new AllelePhenotypeAnnotation();

		ObjectResponse<InformationContentEntity> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());

		InformationContentEntity reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();

		String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subject.getIdentifier(), refString);
		SearchResponse<AllelePhenotypeAnnotation> annotationSearch = allelePhenotypeAnnotationDAO.findByField("uniqueId", uniqueId);
		if (annotationSearch != null && annotationSearch.getSingleResult() != null) {
			annotation = annotationSearch.getSingleResult();
		}

		annotation.setUniqueId(uniqueId);
		annotation.setEvidenceItem(reference);
		annotation.setPhenotypeAnnotationSubject(subject);

		// Reset implied/asserted fields as secondary annotations loaded separately
		annotation.setAssertedGenes(null);
		annotation.setInferredGene(null);

		ObjectResponse<AllelePhenotypeAnnotation> paResponse = validatePhenotypeAnnotation(annotation, dto, dataProvider);
		apaResponse.addErrorMessages(paResponse.getErrorMessages());
		annotation = paResponse.getEntity();

		if (apaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		}

		return annotation;

	}

	public List<AllelePhenotypeAnnotation> validateInferredOrAssertedEntities(Allele primaryAnnotationSubject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider, Set<Long> idsAdded) throws ValidationException {
		// Fast skip: check if the inferred gene is already set using pre-loaded maps only
		if (existingUniqueIds != null && inferredGeneIds != null && dataProvider.hasInferredGenePhenotypeAnnotations && StringUtils.isNotBlank(dto.getObjectId())) {
			ObjectResponse<InformationContentEntity> quickRefResponse = validateReference(dto);
			if (!quickRefResponse.hasErrors()) {
				String quickRefString = quickRefResponse.getEntity() != null ? quickRefResponse.getEntity().getCurie() : null;
				String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, primaryAnnotationSubject.getIdentifier(), quickRefString);
				if (existingUniqueIds.containsKey(uniqueId)) {
					Long existingInferredGeneId = inferredGeneIds.get(uniqueId);
					Long objectEntityId = genomicEntityIdCache.computeIfAbsent(dto.getObjectId(), id -> {
						GenomicEntity entity = genomicEntityService.findByIdentifierString(id);
						return entity != null ? entity.getId() : null;
					});
					if (existingInferredGeneId != null && objectEntityId != null && existingInferredGeneId.equals(objectEntityId)) {
						idsAdded.add(existingUniqueIds.get(uniqueId));
						return new ArrayList<>();
					}
				}
			}
		}

		ObjectResponse<AllelePhenotypeAnnotation> apaResponse = new ObjectResponse<AllelePhenotypeAnnotation>();

		ObjectResponse<InformationContentEntity> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());

		InformationContentEntity reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();

		List<AllelePhenotypeAnnotation> primaryAnnotations = findPrimaryAnnotations(allelePhenotypeAnnotationDAO, dto, primaryAnnotationSubject.getIdentifier(), refString);
		
		if (CollectionUtils.isEmpty(primaryAnnotations)) {

			PhenotypeFmsDTO inferredPrimaryDTO = createPrimaryAnnotationDTO(dto, primaryAnnotationSubject.getIdentifier());
			try {
				Long createdPrimaryAnnotationId = phenotypeAnnotationService.upsertPrimaryAnnotation(inferredPrimaryDTO, dataProvider);
				idsAdded.add(createdPrimaryAnnotationId);
				AllelePhenotypeAnnotation primaryAnnotation = allelePhenotypeAnnotationDAO.find(createdPrimaryAnnotationId);
				primaryAnnotations = List.of(primaryAnnotation);
			} catch (ObjectUpdateException e) {
				throw new ObjectValidationException(dto, "Could not construct primary annotation for " + inferredPrimaryDTO.getObjectId() + ": " + e.getData().getMessage());
			} catch (Exception e) {
				throw new ObjectValidationException(dto, e.getMessage());
			}
		}

		if (StringUtils.isBlank(dto.getObjectId())) {
			apaResponse.addErrorMessage("objectId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			GenomicEntity inferredOrAssertedEntity = genomicEntityService.findByIdentifierString(dto.getObjectId());
			if (inferredOrAssertedEntity == null) {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			} else if (inferredOrAssertedEntity instanceof Gene) {
				boolean alreadySet = true;
				for (AllelePhenotypeAnnotation primaryAnnotation : primaryAnnotations) {
					if (dataProvider.hasInferredGenePhenotypeAnnotations) {
						if (primaryAnnotation.getInferredGene() == null || !primaryAnnotation.getInferredGene().getId().equals(inferredOrAssertedEntity.getId())) {
							alreadySet = false;
							break;
						}
					} else if (dataProvider.hasAssertedGenePhenotypeAnnotations) {
						if (primaryAnnotation.getAssertedGenes() == null || primaryAnnotation.getAssertedGenes().stream().noneMatch(g -> g.getId().equals(inferredOrAssertedEntity.getId()))) {
							alreadySet = false;
							break;
						}
					}
				}
				if (alreadySet) {
					return new ArrayList<>();
				}
				Gene inferredOrAssertedGene = xrefHelper.addGenePhenotypeCrossReference(dataProvider, (Gene) inferredOrAssertedEntity);
				for (AllelePhenotypeAnnotation primaryAnnotation : primaryAnnotations) {
					if (dataProvider.hasInferredGenePhenotypeAnnotations) {
						primaryAnnotation.setInferredGene(inferredOrAssertedGene);
					} else if (dataProvider.hasAssertedGenePhenotypeAnnotations) {
						List<Gene> assertedGenes = primaryAnnotation.getAssertedGenes();
						if (assertedGenes == null) {
							assertedGenes = new ArrayList<>();
						}
						assertedGenes.add(inferredOrAssertedGene);
						primaryAnnotation.setAssertedGenes(assertedGenes);
					} else {
						apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
					}
				}
			} else {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			}
		}

		if (apaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		}

		return primaryAnnotations;
	}

	private PhenotypeFmsDTO createPrimaryAnnotationDTO(PhenotypeFmsDTO dto, String primarySubjectId) {
		PhenotypeFmsDTO primaryAnnotationDTO = new PhenotypeFmsDTO();

		primaryAnnotationDTO.setObjectId(primarySubjectId);
		primaryAnnotationDTO.setPhenotypeStatement(dto.getPhenotypeStatement());
		primaryAnnotationDTO.setPhenotypeTermIdentifiers(dto.getPhenotypeTermIdentifiers());
		primaryAnnotationDTO.setEvidence(dto.getEvidence());
		primaryAnnotationDTO.setDateAssigned(dto.getDateAssigned());
		primaryAnnotationDTO.setConditionRelations(dto.getConditionRelations());

		return primaryAnnotationDTO;
	}
}
