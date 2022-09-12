package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	DoTermDAO doTermDAO;
	@Inject
	EcoTermDAO ecoTermDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	ExperimentalConditionService experimentalConditionService;
	@Inject
	NoteService noteService;
	@Inject
	BiologicalEntityDAO biologicalEntityDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	PersonService personService;
	@Inject
	ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	// The following methods are for bulk validation


	@Transactional
	public DiseaseAnnotation upsert(DiseaseAnnotation annotation, DiseaseAnnotationDTO annotationDTO) throws ObjectUpdateException {
		List<ConditionRelation> conditionRelations = new ArrayList<>();
		List<ConditionRelation> conditionRelationsToPersist = new ArrayList<>();

		// create Experimental Conditions
		if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
			for (ConditionRelationDTO conditionRelationDTO : annotationDTO.getConditionRelations()) {
				ConditionRelation relation = new ConditionRelation();

				if (relation.getInternal() != null)
					relation.setInternal(conditionRelationDTO.getInternal());
				if (relation.getObsolete() != null)
					relation.setObsolete(conditionRelationDTO.getObsolete());

				if (StringUtils.isNotBlank(conditionRelationDTO.getCreatedBy())) {
					Person createdBy = personService.fetchByUniqueIdOrCreate(conditionRelationDTO.getCreatedBy());
					relation.setCreatedBy(createdBy);
				}
				if (StringUtils.isNotBlank(conditionRelationDTO.getUpdatedBy())) {
					Person updatedBy = personService.fetchByUniqueIdOrCreate(conditionRelationDTO.getUpdatedBy());
					relation.setUpdatedBy(updatedBy);
				}

				if (StringUtils.isNotBlank(conditionRelationDTO.getDateUpdated())) {
					OffsetDateTime dateLastModified;
					try {
						dateLastModified = OffsetDateTime.parse(conditionRelationDTO.getDateUpdated());
					} catch (DateTimeParseException e) {
						throw new ObjectValidationException(conditionRelationDTO, "Could not parse date_updated - skipping");
					}
					relation.setDateUpdated(dateLastModified);
				}

				if (StringUtils.isNotBlank(conditionRelationDTO.getDateCreated())) {
					OffsetDateTime creationDate;
					try {
						creationDate = OffsetDateTime.parse(conditionRelationDTO.getDateCreated());
					} catch (DateTimeParseException e) {
						throw new ObjectValidationException(conditionRelationDTO, "Could not parse date_created in - skipping");
					}
					relation.setDateCreated(creationDate);
				}

				String conditionRelationType = conditionRelationDTO.getConditionRelationType();
				if (StringUtils.isBlank(conditionRelationType)) {
					throw new ObjectValidationException(annotationDTO, "Annotation " + annotation.getUniqueId() + " has condition without relation type - skipping");
				}
				VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(conditionRelationType, VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
				if (conditionRelationTypeTerm == null) {
					throw new ObjectValidationException(annotationDTO, "Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
				} else {
					relation.setConditionRelationType(conditionRelationTypeTerm);
				}

				if (CollectionUtils.isEmpty(conditionRelationDTO.getConditions())) {
					throw new ObjectValidationException(annotationDTO, "Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
				}
				for (ExperimentalConditionDTO experimentalConditionDTO : conditionRelationDTO.getConditions()) {
					ExperimentalCondition experimentalCondition = experimentalConditionService.validateExperimentalConditionDTO(experimentalConditionDTO);
					if (experimentalCondition == null) return null;

					relation.addExperimentCondition(experimentalCondition);
				}
				if (StringUtils.isNotBlank(conditionRelationDTO.getHandle())) {
					relation.setHandle(conditionRelationDTO.getHandle());
					// reference of annotation equals the reference of the experiment
					relation.setSingleReference(annotation.getSingleReference());
				}

				relation.setUniqueId(DiseaseAnnotationCurie.getConditionRelationUnique(relation));
				// reuse existing condition relation
				SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", relation.getUniqueId());
				if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
					conditionRelationsToPersist.add(relation);
				} else {
					relation = searchResponseRel.getSingleResult();
				}
				conditionRelations.add(relation);
			}
			annotation.setConditionRelations(conditionRelations);
		}

		conditionRelationsToPersist.forEach(relation -> conditionRelationDAO.persist(relation));
		diseaseAnnotationDAO.persist(annotation);
		return annotation;

	}

	public void removeNonUpdatedAnnotations(String taxonId, List<String> annotationIdsBefore, List<String> annotationIdsAfter) {
		log.debug("runLoad: After: " + taxonId + " " + annotationIdsAfter.size());

		List<String> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonId + " " + distinctAfter.size());

		List<String> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonId + " " + idsToRemove.size());

		for (String id : idsToRemove) {
			SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", id);
			if (da != null && da.getTotalResults() == 1) {
				List<Long> noteIdsToDelete = da.getResults().get(0).getRelatedNotes().stream().map(Note::getId).collect(Collectors.toList());
				delete(da.getResults().get(0).getId());
				for (Long noteId : noteIdsToDelete) {
					noteService.delete(noteId);
				}
			} else {
				log.error("Failed getting annotation: " + id);
			}
		}
	}

	public DiseaseAnnotation validateAnnotationDTO(DiseaseAnnotation annotation, DiseaseAnnotationDTO dto) throws ObjectValidationException {
		if (StringUtils.isBlank(dto.getObject()) || StringUtils.isBlank(dto.getDiseaseRelation()) || StringUtils.isBlank(dto.getDataProvider()) ||
			StringUtils.isBlank(dto.getSingleReference()) || CollectionUtils.isEmpty(dto.getEvidenceCodes()) ||
			dto.getInternal() == null) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing required fields - skipping");
		}

		if (StringUtils.isNotBlank(dto.getCreatedBy())) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			annotation.setCreatedBy(createdBy);
		}
		if (StringUtils.isNotBlank(dto.getUpdatedBy())) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			annotation.setUpdatedBy(updatedBy);
		}

		annotation.setInternal(dto.getInternal());
		
		Boolean obsolete = false;
		if (dto.getObsolete() != null)
			obsolete = dto.getObsolete();
		annotation.setObsolete(obsolete);

		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in " + annotation.getUniqueId() + " - skipping");
			}
			annotation.setDateUpdated(dateLastModified);
		}

		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created in " + annotation.getUniqueId() + " - skipping");
			}
			annotation.setDateCreated(creationDate);
		}

		annotation.setDataProvider(dto.getDataProvider());

		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			annotation.setModEntityId(dto.getModEntityId());
		} else {
			annotation.setModEntityId(null);
		}

		DOTerm disease = doTermDAO.find(dto.getObject());
		if (disease == null) {
			throw new ObjectValidationException(dto, "Annotation " + annotation.getUniqueId() + " has DOTerm " + disease + " not found in database - skipping");
		}
		annotation.setObject(disease);

		String publicationId = dto.getSingleReference();
		Reference reference = referenceDAO.find(publicationId);
		if (reference == null || reference.getObsolete()) {
			reference = referenceService.retrieveFromLiteratureService(publicationId);
			if (reference == null) {
				throw new ObjectValidationException(dto, "Invalid publication ID in " + annotation.getUniqueId() + " - skipping annotation");
			}

		}
		annotation.setSingleReference(reference);

		List<ECOTerm> ecoTerms = new ArrayList<>();
		for (String ecoCurie : dto.getEvidenceCodes()) {
			ECOTerm ecoTerm = ecoTermDAO.find(ecoCurie);
			if (ecoTerm == null) {
				throw new ObjectValidationException(dto, "Invalid evidence code in " + annotation.getUniqueId() + " - skipping annotation");
			}
			ecoTerms.add(ecoTerm);
		}
		annotation.setEvidenceCodes(ecoTerms);

		if (dto.getNegated() != null) {
			annotation.setNegated(dto.getNegated());
		} else {
			annotation.setNegated(false);
		}

		if (CollectionUtils.isNotEmpty(dto.getWith())) {
			List<Gene> withGenes = new ArrayList<>();
			for (String withCurie : dto.getWith()) {
				if (!withCurie.startsWith("HGNC:")) {
					throw new ObjectValidationException(dto, "Non-HGNC gene (" + withCurie + ") found in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
				}
				Gene withGene = geneDAO.getByIdOrCurie(withCurie);
				if (withGene == null) {
					throw new ObjectValidationException(dto, "Invalid gene (" + withCurie + ") in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
				}
				withGenes.add(withGene);
			}
			annotation.setWith(withGenes);
		} else {
			annotation.setWith(null);
		}

		String secondaryDataProvider = null;
		if (StringUtils.isNotBlank(dto.getSecondaryDataProvider())) {
			secondaryDataProvider = dto.getSecondaryDataProvider();
		}
		annotation.setSecondaryDataProvider(secondaryDataProvider);

		if (CollectionUtils.isNotEmpty(dto.getDiseaseQualifiers())) {
			List<VocabularyTerm> diseaseQualifiers = new ArrayList<>();
			for (String qualifier : dto.getDiseaseQualifiers()) {
				VocabularyTerm diseaseQualifier = vocabularyTermDAO.getTermInVocabulary(qualifier, VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
				if (diseaseQualifier == null) {
					throw new ObjectValidationException(dto, "Invalid disease qualifier (" + qualifier + ") for " + annotation.getUniqueId() + " - skipping annotation");
				}
				diseaseQualifiers.add(diseaseQualifier);
			}
			annotation.setDiseaseQualifiers(diseaseQualifiers);
		} else {
			annotation.setDiseaseQualifiers(null);
		}

		BiologicalEntity diseaseGeneticModifier = null;
		VocabularyTerm diseaseGeneticModifierRelation = null;
		if (StringUtils.isNotBlank(dto.getDiseaseGeneticModifier()) || StringUtils.isNotBlank(dto.getDiseaseGeneticModifierRelation())) {
			if (StringUtils.isBlank(dto.getDiseaseGeneticModifier()) || StringUtils.isBlank(dto.getDiseaseGeneticModifierRelation())) {
				throw new ObjectValidationException(dto, "Genetic modifier specified without genetic modifier relation (or vice versa) for " + annotation.getUniqueId() + " - skipping annotation");
			}

			diseaseGeneticModifierRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseGeneticModifierRelation(), VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
			if (diseaseGeneticModifierRelation == null) {
				throw new ObjectValidationException(dto, "Invalid disease genetic modifier relation (" + dto.getDiseaseGeneticModifierRelation() + ") for " + annotation.getUniqueId() + " - skipping annotation");
			}
			
			diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifier());
			if (diseaseGeneticModifier == null) {
				throw new ObjectValidationException(dto, "Invalid biological entity (" + dto.getDiseaseGeneticModifier() + ") in 'disease_genetic_modifier' field in " + annotation.getUniqueId() + " - skipping annotation");
			}
		}
		annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
		annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);

		VocabularyTerm annotationType = null;
		if (StringUtils.isNotBlank(dto.getAnnotationType())) {
			annotationType = vocabularyTermDAO.getTermInVocabulary(dto.getAnnotationType(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
			if (annotationType == null) {
				throw new ObjectValidationException(dto, "Invalid annotation type (" + dto.getAnnotationType() + ") in " + annotation.getUniqueId() + " - skipping annotation");
			}
		}
		annotation.setAnnotationType(annotationType);
		
		VocabularyTerm geneticSex = null;
		if (StringUtils.isNotBlank(dto.getGeneticSex())) {
			geneticSex = vocabularyTermDAO.getTermInVocabulary(dto.getGeneticSex(), VocabularyConstants.GENETIC_SEX_VOCABULARY);
			if (geneticSex == null) {
				throw new ObjectValidationException(dto, "Invalid genetic sex (" + dto.getGeneticSex() + ") in " + annotation.getUniqueId() + " - skipping annotation");
			}
		}
		annotation.setGeneticSex(geneticSex);
		
		if (CollectionUtils.isNotEmpty(dto.getRelatedNotes())) {
			List<Note> notesToPersist = new ArrayList<>();
			for (NoteDTO noteDTO : dto.getRelatedNotes()) {
				Note relatedNote = noteService.validateNoteDTO(noteDTO, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
				if (relatedNote == null)
					throw new ObjectValidationException(dto, "Invalid note attached to disease annotation " + annotation.getUniqueId() + " - skipping annotation");
				notesToPersist.add(relatedNote);
			}
			notesToPersist.forEach(note -> noteDAO.persist(note));
			annotation.setRelatedNotes(notesToPersist);
		} else {
			annotation.setRelatedNotes(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelations())) {
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelations()) {
				if (conditionRelationDTO.getHandle() != null) {
					if (!conditionRelationDTO.getSingleReference().equals(dto.getSingleReference()))
						throw new ObjectValidationException(dto, "Invalid Paper Handle: reference of annotation needs to be the same as the conditionRelationDto reference " + annotation.getUniqueId());
				}
			}
		} else {
			annotation.setConditionRelations(null);
		}

		return annotation;
	}

	@Transactional
	public ObjectResponse<DiseaseAnnotation> deleteNotes(Long id) {
		SearchResponse<DiseaseAnnotation> response = dao.searchByField(new Pagination(), "id", Long.toString(id));

		DiseaseAnnotation singleResult = response.getSingleResult();
		if (singleResult == null) {
			ObjectResponse<DiseaseAnnotation> oResponse = new ObjectResponse<>();
			oResponse.addErrorMessage("id", "Could not find Disease Annotation with id: " + id);
			throw new ApiErrorException(oResponse);
		}
		// remove notes
		if (CollectionUtils.isNotEmpty(singleResult.getRelatedNotes())) {
			singleResult.getRelatedNotes().forEach(note -> noteService.delete(note.getId()));
		}
		singleResult.setRelatedNotes(null);
		if (singleResult.getSingleReference() != null) {
			singleResult.setSingleReference(null);
		}

		return new ObjectResponse<>(singleResult);
	}

}
