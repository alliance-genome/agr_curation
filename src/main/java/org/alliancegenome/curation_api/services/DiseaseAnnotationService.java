package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

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

				relation.setInternal(conditionRelationDTO.getInternal());
				relation.setObsolete(conditionRelationDTO.getObsolete());
				
				if (conditionRelationDTO.getCreatedBy() != null) {
					Person createdBy = personService.fetchByUniqueIdOrCreate(conditionRelationDTO.getCreatedBy());
					relation.setCreatedBy(createdBy);
				}
				if (conditionRelationDTO.getModifiedBy() != null) {
					Person modifiedBy = personService.fetchByUniqueIdOrCreate(conditionRelationDTO.getModifiedBy());
					relation.setModifiedBy(modifiedBy);
				}
				
				if (conditionRelationDTO.getDateUpdated() != null) {
					OffsetDateTime dateLastModified;
					try {
						dateLastModified = OffsetDateTime.parse(conditionRelationDTO.getDateUpdated());
					} catch (DateTimeParseException e) {
						throw new ObjectValidationException(conditionRelationDTO, "Could not parse date_updated - skipping");
					}
					relation.setDateUpdated(dateLastModified);
				}

				if (conditionRelationDTO.getDateCreated() != null) {
					OffsetDateTime creationDate;
					try {
						creationDate = OffsetDateTime.parse(conditionRelationDTO.getDateCreated());
					} catch (DateTimeParseException e) {
						throw new ObjectValidationException(conditionRelationDTO, "Could not parse date_created in - skipping");
					}
					relation.setDateCreated(creationDate);
				}
				
				String conditionRelationType = conditionRelationDTO.getConditionRelationType();
				if (conditionRelationType == null) {
					throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " has condition without relation type - skipping");
				}
				VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(conditionRelationType, VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
				if (conditionRelationTypeTerm == null) {
					throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
				} else {
					relation.setConditionRelationType(conditionRelationTypeTerm);
				}

				if (CollectionUtils.isEmpty(conditionRelationDTO.getConditions())) {
					throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
				}
				for (ExperimentalConditionDTO experimentalConditionDTO : conditionRelationDTO.getConditions()) {
					ExperimentalCondition experimentalCondition = experimentalConditionService.validateExperimentalConditionDTO(experimentalConditionDTO);
					if (experimentalCondition == null) return null;

					relation.addExperimentCondition(experimentalCondition);
				}
				if (conditionRelationDTO.getHandle() != null) {
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
		if (StringUtils.isEmpty(dto.getObject()) || StringUtils.isEmpty(dto.getDiseaseRelation()) || StringUtils.isEmpty(dto.getDataProvider()) ||
				StringUtils.isEmpty(dto.getSingleReference()) || CollectionUtils.isEmpty(dto.getEvidenceCodes()) ||
				StringUtils.isEmpty(dto.getCreatedBy()) || StringUtils.isEmpty(dto.getModifiedBy()) || dto.getInternal() == null) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing required fields - skipping");
		}
		
		Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
		annotation.setCreatedBy(createdBy);
		Person modifiedBy = personService.fetchByUniqueIdOrCreate(dto.getModifiedBy());
		annotation.setModifiedBy(modifiedBy);
		
		annotation.setInternal(dto.getInternal());
		annotation.setObsolete(dto.getObsolete());

		if (dto.getDateUpdated() != null) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in " + annotation.getUniqueId() + " - skipping");
			}
			annotation.setDateUpdated(dateLastModified);
		}

		if (dto.getDateCreated() != null) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created in " + annotation.getUniqueId() + " - skipping");
			}
			annotation.setDateCreated(creationDate);
		}
		
		annotation.setDataProvider(dto.getDataProvider());

		if (dto.getModEntityId() != null) {
			annotation.setModEntityId(dto.getModEntityId());
		}

		DOTerm disease = doTermDAO.find(dto.getObject());
		if (disease == null) {
			throw new ObjectValidationException(dto, "Annotation " + annotation.getUniqueId() + " has DOTerm " + disease + " not found in database - skipping");
		}
		annotation.setObject(disease);

		String publicationId = dto.getSingleReference();
		Reference reference = referenceDAO.find(publicationId);
		if (reference == null) {
			reference = referenceService.retrieveFromLiteratureService(publicationId);
			if (reference == null) {
				throw new ObjectValidationException(dto, "Invalid publication ID in " + annotation.getUniqueId() + " - skipping annotation");
			}
		}
		annotation.setSingleReference(reference);

		List<EcoTerm> ecoTerms = new ArrayList<>();
		for (String ecoCurie : dto.getEvidenceCodes()) {
			EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
			if (ecoTerm == null) {
				throw new ObjectValidationException(dto, "Invalid evidence code in " + annotation.getUniqueId() + " - skipping annotation");
			}
			ecoTerms.add(ecoTerm);
		}
		annotation.setEvidenceCodes(ecoTerms);

		if (dto.getNegated() != null)
			annotation.setNegated(dto.getNegated());

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
		}

		if (dto.getSecondaryDataProvider() != null)
			annotation.setSecondaryDataProvider(dto.getSecondaryDataProvider());

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
		}

		if (dto.getDiseaseGeneticModifier() != null || dto.getDiseaseGeneticModifierRelation() != null) {
			if (dto.getDiseaseGeneticModifier() == null || dto.getDiseaseGeneticModifierRelation() == null) {
				throw new ObjectValidationException(dto, "Genetic modifier specified without genetic modifier relation (or vice versa) for " + annotation.getUniqueId() + " - skipping annotation");
			}
			
			VocabularyTerm diseaseGeneticModifierRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseGeneticModifierRelation(), VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
			if (diseaseGeneticModifierRelation == null) {
				throw new ObjectValidationException(dto, "Invalid disease genetic modifier relation (" + dto.getDiseaseGeneticModifierRelation() + ") for " + annotation.getUniqueId() + " - skipping annotation");
			}
			annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);

			BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifier());
			if (diseaseGeneticModifier == null) {
				throw new ObjectValidationException(dto, "Invalid biological entity (" + dto.getDiseaseGeneticModifier() + ") in 'disease_genetic_modifier' field in " + annotation.getUniqueId() + " - skipping annotation");
			}
			annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
		}

		if (dto.getAnnotationType() != null) {
			VocabularyTerm annotationType = vocabularyTermDAO.getTermInVocabulary(dto.getAnnotationType(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
			if (annotationType == null) {
				throw new ObjectValidationException(dto, "Invalid annotation type (" + dto.getAnnotationType() + ") in " + annotation.getUniqueId() + " - skipping annotation");
			}
			annotation.setAnnotationType(annotationType);

		}

		if (dto.getGeneticSex() != null) {
			VocabularyTerm geneticSex = vocabularyTermDAO.getTermInVocabulary(dto.getGeneticSex(), VocabularyConstants.GENETIC_SEX_VOCABULARY);
			if (geneticSex == null) {
				throw new ObjectValidationException(dto, "Invalid genetic sex (" + dto.getGeneticSex() + ") in " + annotation.getUniqueId() + " - skipping annotation");
			}
			annotation.setGeneticSex(geneticSex);
		}

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
		}

		if (dto.getConditionRelations() != null) {
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelations()) {
				if (conditionRelationDTO.getHandle() != null) {
					if (!conditionRelationDTO.getSingleReference().equals(dto.getSingleReference()))
						throw new ObjectValidationException(dto, "Invalid Paper Handle: reference of annotation needs to be the same as the conditionRelationDto reference " + annotation.getUniqueId());
				}
			}
		}	 

		return annotation;
	}

}
