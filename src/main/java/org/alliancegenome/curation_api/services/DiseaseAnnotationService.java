package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
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
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService<E extends DiseaseAnnotation, D extends DiseaseAnnotationDTO> extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject DoTermDAO doTermDAO;
	@Inject EcoTermDAO ecoTermDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject GeneDAO geneDAO;
	@Inject NoteDAO noteDAO;
	@Inject ExperimentalConditionService experimentalConditionService;
	@Inject NoteService noteService;
	@Inject BiologicalEntityDAO biologicalEntityDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject PersonService personService;
	@Inject ReferenceService referenceService;
	@Inject AuditedObjectService<E, D> auditedObjectService;
	@Inject ConditionRelationService conditionRelationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	// The following methods are for bulk validation

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

	public ObjectResponse<E> validateAnnotationDTO(E annotation, D dto) {
		ObjectResponse<E> daResponse = auditedObjectService.validateAuditedObjectDTO(annotation, dto);
		annotation = daResponse.getEntity();
		
		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			annotation.setModEntityId(dto.getModEntityId());
		} else {
			annotation.setModEntityId(null);
		}

		if (StringUtils.isBlank(dto.getObject())) {
			daResponse.addErrorMessage("object", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			DOTerm disease = doTermDAO.find(dto.getObject());
			if (disease == null) 
				daResponse.addErrorMessage("object", ValidationConstants.INVALID_MESSAGE);
			annotation.setObject(disease);
		}

		if (StringUtils.isBlank(dto.getSingleReference())) {
			daResponse.addErrorMessage("singleReference", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			Reference reference = referenceDAO.find(dto.getSingleReference());
			if (reference == null || reference.getObsolete()) {
				reference = referenceService.retrieveFromLiteratureService(dto.getSingleReference());
				if (reference == null)
					daResponse.addErrorMessage("singleReference", ValidationConstants.INVALID_MESSAGE);
			}
			annotation.setSingleReference(reference);
		}
		
		if (CollectionUtils.isEmpty(dto.getEvidenceCodes())) {
			daResponse.addErrorMessage("evidenceCodes", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<ECOTerm> ecoTerms = new ArrayList<>();
			for (String ecoCurie : dto.getEvidenceCodes()) {
				ECOTerm ecoTerm = ecoTermDAO.find(ecoCurie);
				if (ecoTerm == null) {
					daResponse.addErrorMessage("evidenceCodes", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				ecoTerms.add(ecoTerm);
			}
			annotation.setEvidenceCodes(ecoTerms);
		}

		if (dto.getNegated() != null) {
			annotation.setNegated(dto.getNegated());
		} else {
			annotation.setNegated(false);
		}

		if (CollectionUtils.isNotEmpty(dto.getWith())) {
			List<Gene> withGenes = new ArrayList<>();
			for (String withCurie : dto.getWith()) {
				if (!withCurie.startsWith("HGNC:")) {
					daResponse.addErrorMessage("with", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				Gene withGene = geneDAO.getByIdOrCurie(withCurie);
				if (withGene == null) {
					daResponse.addErrorMessage("with", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				withGenes.add(withGene);
			}
			annotation.setWith(withGenes);
		} else {
			annotation.setWith(null);
		}

		if (StringUtils.isBlank(dto.getDataProvider()))
			daResponse.addErrorMessage("dataProvider", ValidationConstants.REQUIRED_MESSAGE);
		annotation.setDataProvider(dto.getDataProvider());
		
		if (StringUtils.isNotBlank(dto.getSecondaryDataProvider()))
			annotation.setSecondaryDataProvider(dto.getSecondaryDataProvider());

		if (CollectionUtils.isNotEmpty(dto.getDiseaseQualifiers())) {
			List<VocabularyTerm> diseaseQualifiers = new ArrayList<>();
			for (String qualifier : dto.getDiseaseQualifiers()) {
				VocabularyTerm diseaseQualifier = vocabularyTermDAO.getTermInVocabulary(qualifier, VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
				if (diseaseQualifier == null) {
					daResponse.addErrorMessage("diseaseQualifiers", ValidationConstants.INVALID_MESSAGE);
				}
				diseaseQualifiers.add(diseaseQualifier);
			}
			annotation.setDiseaseQualifiers(diseaseQualifiers);
		}

		if (StringUtils.isNotBlank(dto.getDiseaseGeneticModifier()) || StringUtils.isNotBlank(dto.getDiseaseGeneticModifierRelation())) {
			if (StringUtils.isBlank(dto.getDiseaseGeneticModifier())) {
				daResponse.addErrorMessage("diseaseGeneticModifierRelation", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier");
			} else if (StringUtils.isBlank(dto.getDiseaseGeneticModifierRelation())) {
				daResponse.addErrorMessage("diseaseGeneticModifier", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			} else {
				VocabularyTerm diseaseGeneticModifierRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseGeneticModifierRelation(), VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
				if (diseaseGeneticModifierRelation == null)
					daResponse.addErrorMessage("diseaseGeneticModifierRelation", ValidationConstants.INVALID_MESSAGE);
				BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifier());
				if (diseaseGeneticModifier == null)
					daResponse.addErrorMessage("diseaseGeneticModifier", ValidationConstants.INVALID_MESSAGE);
				annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
				annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
			}
		}
			
		if (StringUtils.isNotBlank(dto.getAnnotationType())) {
			VocabularyTerm annotationType = vocabularyTermDAO.getTermInVocabulary(dto.getAnnotationType(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
			if (annotationType == null)
				daResponse.addErrorMessage("annotationType", ValidationConstants.INVALID_MESSAGE);
			annotation.setAnnotationType(annotationType);
		}
		
		if (StringUtils.isNotBlank(dto.getGeneticSex())) {
			VocabularyTerm geneticSex = vocabularyTermDAO.getTermInVocabulary(dto.getGeneticSex(), VocabularyConstants.GENETIC_SEX_VOCABULARY);
			if (geneticSex == null)
				daResponse.addErrorMessage("geneticSex", ValidationConstants.INVALID_MESSAGE);
			annotation.setGeneticSex(geneticSex);
		}
		
		if (CollectionUtils.isNotEmpty(dto.getRelatedNotes())) {
			List<Note> notes = new ArrayList<>();
			for (NoteDTO noteDTO : dto.getRelatedNotes()) {
				ObjectResponse<Note> noteResponse = noteService.validateNoteDTO(noteDTO, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
				if (noteResponse.hasErrors()) {
					daResponse.addErrorMessage("relatedNotes", noteResponse.errorMessagesString());
					break;
				}
				if (CollectionUtils.isNotEmpty(noteDTO.getReferences())) {
					for (String noteRef : noteDTO.getReferences()) {
						if (!noteRef.equals(dto.getSingleReference())) {
							daResponse.addErrorMessage("relatedNotes - reference", ValidationConstants.INVALID_MESSAGE);
						}
					}
				}
				notes.add(noteDAO.persist(noteResponse.getEntity()));
			}
			annotation.setRelatedNotes(notes);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelations())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelations()) {
				if (conditionRelationDTO.getHandle() != null) {
					if (!conditionRelationDTO.getSingleReference().equals(dto.getSingleReference())) {
						daResponse.addErrorMessage("conditionRelations - handle", ValidationConstants.INVALID_MESSAGE);
						break;
					}
				}
				ObjectResponse<ConditionRelation> crResponse = conditionRelationService.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					daResponse.addErrorMessage("conditionRelations", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		}

		daResponse.setEntity(annotation);
		return daResponse;
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
