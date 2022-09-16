package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.NoteService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


public class DiseaseAnnotationValidator extends AuditedObjectValidator<DiseaseAnnotation>{

	@Inject
	EcoTermDAO ecoTermDAO;
	@Inject
	DoTermDAO doTermDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	BiologicalEntityDAO biologicalEntityDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceService referenceService;
	@Inject
	ReferenceValidator referenceValidator;
	@Inject
	NoteValidator noteValidator;
	@Inject
	NoteService noteService;
	@Inject
	NoteDAO noteDAO;
	@Inject
	ConditionRelationValidator conditionRelationValidator;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;

	public DOTerm validateObject(DiseaseAnnotation	uiEntity, DiseaseAnnotation	 dbEntity) {
		String field = "object";
		if (ObjectUtils.isEmpty(uiEntity.getObject()) || StringUtils.isEmpty(uiEntity.getObject().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		DOTerm diseaseTerm = doTermDAO.find(uiEntity.getObject().getCurie());
		if (diseaseTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (diseaseTerm.getObsolete() && (dbEntity.getObject() == null || !diseaseTerm.getCurie().equals(dbEntity.getObject().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return diseaseTerm;
	}


	public List<ECOTerm> validateEvidenceCodes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "evidenceCodes";
		if (CollectionUtils.isEmpty(uiEntity.getEvidenceCodes())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<ECOTerm> validEvidenceCodes = new ArrayList<>();
		for (ECOTerm ec : uiEntity.getEvidenceCodes()) {
			ECOTerm evidenceCode = ecoTermDAO.find(ec.getCurie());
			if (evidenceCode == null ) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			else if (evidenceCode.getObsolete() &&
					(CollectionUtils.isEmpty(dbEntity.getEvidenceCodes()) || !dbEntity.getEvidenceCodes().contains(evidenceCode))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}

			validEvidenceCodes.add(evidenceCode);

		}
		return validEvidenceCodes;
	}


	public List<Gene> validateWith(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getWith()))
			return null;

		List<Gene> validWithGenes = new ArrayList<Gene>();
		List<String> previousCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getWith()))
			previousCuries = dbEntity.getWith().stream().map(Gene::getCurie).collect(Collectors.toList());
		for (Gene wg : uiEntity.getWith()) {
			Gene withGene = geneDAO.find(wg.getCurie());
			if (withGene == null || !withGene.getCurie().startsWith("HGNC:")) {
				addMessageResponse("with", ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (withGene.getObsolete() && !previousCuries.contains(withGene.getCurie())) {
				addMessageResponse("with", ValidationConstants.OBSOLETE_MESSAGE);
			}
			else {
				validWithGenes.add(withGene);
			}
		}

		return validWithGenes;
	}

	public String validateDataProvider(DiseaseAnnotation uiEntity) {
		// TODO: re-enable error response once field can be added in UI
		String dataProvider = uiEntity.getDataProvider();
		if (dataProvider == null) {
			// addMessageResponse("dataProvider", requiredMessage);
			return null;
		}

		return dataProvider;
	}

	public BiologicalEntity validateDiseaseGeneticModifier(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (uiEntity.getDiseaseGeneticModifier() == null) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			addMessageResponse("diseaseGeneticModifier", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			return null;
		}

		BiologicalEntity modifier = biologicalEntityDAO.find(uiEntity.getDiseaseGeneticModifier().getCurie());
		if (modifier == null) {
			addMessageResponse("diseaseGeneticModifier", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (modifier.getObsolete() && (dbEntity.getDiseaseGeneticModifier() == null || !modifier.getCurie().equals(dbEntity.getDiseaseGeneticModifier().getCurie()))) {
			addMessageResponse("diseaseGeneticModifier", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return modifier;
	}

	public VocabularyTerm validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierRelation";
		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifier() == null) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier");
			return null;
		}

		VocabularyTerm dgmRelation = vocabularyTermDAO.getTermInVocabulary(uiEntity.getDiseaseGeneticModifierRelation().getName(), VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);

		if(dgmRelation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dgmRelation.getObsolete() && (dbEntity.getDiseaseGeneticModifierRelation() == null || !dgmRelation.getName().equals(dbEntity.getDiseaseGeneticModifierRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dgmRelation;
	}

	public VocabularyTerm validateGeneticSex(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "geneticSex";
		if (uiEntity.getGeneticSex() == null) {
			return null;
		}

		VocabularyTerm geneticSex = vocabularyTermDAO.getTermInVocabulary(uiEntity.getGeneticSex().getName(), VocabularyConstants.GENETIC_SEX_VOCABULARY);

		if(geneticSex == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (geneticSex.getObsolete() && (dbEntity.getGeneticSex() == null || !geneticSex.getName().equals(dbEntity.getGeneticSex().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return geneticSex;
	}

	public VocabularyTerm validateAnnotationType(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "annotationType";
		if (uiEntity.getAnnotationType() == null) {
			return null;
		}

		VocabularyTerm annotationType = vocabularyTermDAO.getTermInVocabulary(uiEntity.getAnnotationType().getName(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);

		if(annotationType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (annotationType.getObsolete() && (dbEntity.getAnnotationType() == null || !annotationType.getName().equals(dbEntity.getAnnotationType().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return annotationType;
	}

	public List<Note> validateRelatedNotes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "relatedNotes";
		
		List<Note> validatedNotes = new ArrayList<Note>();
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (Note note : uiEntity.getRelatedNotes()) {
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
				if (noteResponse.getEntity() == null) {
					Map<String, String> errors = noteResponse.getErrorMessages();
					for (String noteField : errors.keySet()) {
						addMessageResponse(field, noteField + " - " + errors.get(noteField));
					}
					return null;
				}
				note = noteResponse.getEntity();
				
				// If present, note reference should match DA reference
				if (CollectionUtils.isNotEmpty(note.getReferences())) {
					for (Reference noteRef : note.getReferences()) {
						if (!noteRef.getCurie().equals(dbEntity.getSingleReference().getCurie())) {
							addMessageResponse(field, "references - " + ValidationConstants.INVALID_MESSAGE);
							return null;
						}
					}
				}
				
				validatedNotes.add(note);
			}
		}

		List<Long> previousNoteIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes()))
			previousNoteIds = dbEntity.getRelatedNotes().stream().map(Note::getId).collect(Collectors.toList());
		List<Long> validatedNoteIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(validatedNotes))
			validatedNoteIds = validatedNotes.stream().map(Note::getId).collect(Collectors.toList());
		for (Note validatedNote: validatedNotes) {
			if (!previousNoteIds.contains(validatedNote.getId())) {
				noteDAO.persist(validatedNote);
			}
		}
		List<Long> idsToRemove = ListUtils.subtract(previousNoteIds, validatedNoteIds);
		for (Long id : idsToRemove) {
			diseaseAnnotationDAO.deleteAttachedNote(id);
		}

		if (CollectionUtils.isEmpty(validatedNotes))
			return null;

		return validatedNotes;
	}

	public List<ConditionRelation> validateConditionRelations(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getConditionRelations()))
			return null;

		List<ConditionRelation> validatedConditionRelations = new ArrayList<ConditionRelation>();
		List<Long> previousConditionRelationIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getConditionRelations()))
			previousConditionRelationIds = dbEntity.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());

		for (ConditionRelation conditionRelation : uiEntity.getConditionRelations()) {
			if (uiEntity.getSingleReference() != null && !StringUtils.isBlank(uiEntity.getSingleReference().getCurie()) &&
					conditionRelation.getSingleReference() != null && !StringUtils.isBlank(conditionRelation.getSingleReference().getCurie()) &&
					!conditionRelation.getSingleReference().getCurie().equals(uiEntity.getSingleReference().getCurie())) {
				addMessageResponse("conditionRelations", "singleReference - " + ValidationConstants.INVALID_MESSAGE);
			}

			if (conditionRelation.getObsolete() && !previousConditionRelationIds.contains(conditionRelation.getId())) {
				addMessageResponse("conditionRelations", ValidationConstants.OBSOLETE_MESSAGE);
			}

			ObjectResponse<ConditionRelation> crResponse = conditionRelationValidator.validateConditionRelation(conditionRelation);
			conditionRelation = crResponse.getEntity();
			if (conditionRelation == null) {
				Map<String, String> errors = crResponse.getErrorMessages();
				for (String field : errors.keySet()) {
					addMessageResponse("conditionRelations", field + " - " + errors.get(field));
				}
				return null;
			}

			// reuse existing condition relation
			SearchResponse<ConditionRelation> crSearchResponse = conditionRelationDAO.findByField("uniqueId", conditionRelation.getUniqueId());
			if (crSearchResponse != null && crSearchResponse.getSingleResult() != null) {
				conditionRelation.setId(crSearchResponse.getSingleResult().getId());
				conditionRelation = conditionRelationDAO.merge(conditionRelation);
			} else if (conditionRelation.getId() == null) {
				conditionRelation = conditionRelationDAO.persist(crResponse.getEntity());
			}
			validatedConditionRelations.add(crResponse.getEntity());
		}
		return validatedConditionRelations;
	}

	public Reference validateSingleReference(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "singleReference";
		if (ObjectUtils.isEmpty(uiEntity.getSingleReference()) || StringUtils.isBlank(uiEntity.getSingleReference().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity.getSingleReference());
		if (singleRefResponse.getEntity() == null) {
			Map<String, String> errors = singleRefResponse.getErrorMessages();
			for (String refField : errors.keySet()) {
				addMessageResponse(field, refField + " - " + errors.get(refField));
			}
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (dbEntity.getSingleReference() == null || !singleRefResponse.getEntity().getCurie().equals(dbEntity.getSingleReference().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	public String validateUniqueId(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String uniqueId;
		if (!StringUtils.isBlank(uiEntity.getModEntityId())) {
			uniqueId = uiEntity.getModEntityId();
		} else if (!StringUtils.isBlank(dbEntity.getModEntityId())) {
			uniqueId = dbEntity.getModEntityId();
		} else if (StringUtils.isBlank(uiEntity.getSubjectCurie()) || StringUtils.isBlank(uiEntity.getSubjectTaxonCurie()) || uiEntity.getObject() == null || uiEntity.getSingleReference() == null) {
					 return null;
		} else {
			uniqueId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(uiEntity.getSubjectTaxonCurie()).getCurieID(uiEntity);
		}

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<DiseaseAnnotation> response = diseaseAnnotationDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null)
			newEntity = true;
		dbEntity = (DiseaseAnnotation) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		if (uiEntity.getModEntityId() != null)
			dbEntity.setModEntityId(uiEntity.getModEntityId());

		DOTerm term = validateObject(uiEntity, dbEntity);
		dbEntity.setObject(term);

		List<ECOTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
		dbEntity.setEvidenceCodes(terms);

		Reference singleReference = validateSingleReference(uiEntity, dbEntity);
		dbEntity.setSingleReference(singleReference);

		List<Gene> genes = validateWith(uiEntity, dbEntity);
		dbEntity.setWith(genes);

		if(uiEntity.getNegated() != null) {
			dbEntity.setNegated(uiEntity.getNegated());
		}else{
			dbEntity.setNegated(false);
		}

		VocabularyTerm annotationType = validateAnnotationType(uiEntity, dbEntity);
		dbEntity.setAnnotationType(annotationType);

		VocabularyTerm geneticSex = validateGeneticSex(uiEntity, dbEntity);
		dbEntity.setGeneticSex(geneticSex);

		String dataProvider = validateDataProvider(uiEntity);
		dbEntity.setDataProvider(dataProvider);

		dbEntity.setSecondaryDataProvider(handleStringField(uiEntity.getSecondaryDataProvider()));

		BiologicalEntity diseaseGeneticModifier = validateDiseaseGeneticModifier(uiEntity, dbEntity);
		VocabularyTerm dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity, dbEntity);
		dbEntity.setDiseaseGeneticModifier(diseaseGeneticModifier);
		dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);

		List<ConditionRelation> conditionRelations = validateConditionRelations(uiEntity, dbEntity);
		dbEntity.setConditionRelations(conditionRelations);

		if (CollectionUtils.isNotEmpty(uiEntity.getDiseaseQualifiers())) {
			dbEntity.setDiseaseQualifiers(uiEntity.getDiseaseQualifiers());
		} else {
			dbEntity.setDiseaseQualifiers(null);
		}

		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		dbEntity.setRelatedNotes(relatedNotes);

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		return dbEntity;
	}
}
