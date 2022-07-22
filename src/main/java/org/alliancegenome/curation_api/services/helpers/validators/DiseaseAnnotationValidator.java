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
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.NoteService;
import org.alliancegenome.curation_api.services.ReferenceService;
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
		else if (diseaseTerm.getObsolete() && !diseaseTerm.getCurie().equals(dbEntity.getObject().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return diseaseTerm;
	}

	
	public List<EcoTerm> validateEvidenceCodes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "evidence";
		if (CollectionUtils.isEmpty(uiEntity.getEvidenceCodes())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<EcoTerm> validEvidenceCodes = new ArrayList<>(); 
		for (EcoTerm ec : uiEntity.getEvidenceCodes()) {
			EcoTerm evidenceCode = ecoTermDAO.find(ec.getCurie());
			if (evidenceCode == null ) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			else if (evidenceCode.getObsolete() && !dbEntity.getEvidenceCodes().contains(evidenceCode)) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}

			validEvidenceCodes.add(evidenceCode);

		}
		return validEvidenceCodes;
	}
	
	
	public List<Gene> validateWith(DiseaseAnnotation uiEntity) {
		List<Gene> validWithGenes = new ArrayList<Gene>();
		
		if (CollectionUtils.isNotEmpty(uiEntity.getWith())) {
			for (Gene wg : uiEntity.getWith()) {
				Gene withGene = geneDAO.find(wg.getCurie());
				if (withGene == null || !withGene.getCurie().startsWith("HGNC:")) {
					addMessageResponse("with", ValidationConstants.INVALID_MESSAGE);
					return null;
				}
				else {
					validWithGenes.add(withGene);
				}
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
	
	public BiologicalEntity validateDiseaseGeneticModifier(DiseaseAnnotation uiEntity) {
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
		
		return modifier;
	}
	
	public VocabularyTerm validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity) {
		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			return null;
		}
		
		if (uiEntity.getDiseaseGeneticModifier() == null) {
			addMessageResponse("diseaseGeneticModifierRelation", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier");
			return null;
		}
		
		return uiEntity.getDiseaseGeneticModifierRelation();
	}
	
	public List<Note> validateRelatedNotes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		List<Note> validatedNotes = new ArrayList<Note>();
		if (uiEntity.getRelatedNotes() != null) {
			for (Note note : uiEntity.getRelatedNotes()) {
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
				if (noteResponse.getEntity() == null) {
					Map<String, String> errors = noteResponse.getErrorMessages();
					for (String field : errors.keySet()) {
						addMessageResponse("relatedNotes", field + " - " + errors.get(field));
					}
					return null;
				}
				validatedNotes.add(noteResponse.getEntity());
			}
		}
		
		List<Long> previousNoteIds = dbEntity.getRelatedNotes().stream().map(Note::getId).collect(Collectors.toList());
		List<Long> validatedNoteIds = validatedNotes.stream().map(Note::getId).collect(Collectors.toList());
		for (Note validatedNote: validatedNotes) {
			if (!previousNoteIds.contains(validatedNote.getId())) {
				noteDAO.persist(validatedNote);
			}
		}
		List<Long> idsToRemove = ListUtils.subtract(previousNoteIds, validatedNoteIds);
		for (Long id : idsToRemove) {
			noteService.delete(id);
		}
		
		if (CollectionUtils.isEmpty(validatedNotes))
			return null;
		
		return validatedNotes;
	}
	
	public List<ConditionRelation> validateConditionRelations(DiseaseAnnotation uiEntity) {
		List<ConditionRelation> validatedConditionRelations = new ArrayList<ConditionRelation>();
		for (ConditionRelation conditionRelation : uiEntity.getConditionRelations()) {
			if (uiEntity.getSingleReference() != null && !StringUtils.isBlank(uiEntity.getSingleReference().getCurie()) &&
					conditionRelation.getSingleReference() != null && !StringUtils.isBlank(conditionRelation.getSingleReference().getCurie()) &&
					!conditionRelation.getSingleReference().getCurie().equals(uiEntity.getSingleReference().getCurie())) {
				addMessageResponse("conditionRelationHandle", ValidationConstants.INVALID_MESSAGE);
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
				conditionRelation = crSearchResponse.getSingleResult();
			} else if (conditionRelation.getId() == null) {
				conditionRelation = conditionRelationDAO.persist(crResponse.getEntity());
			}
			validatedConditionRelations.add(crResponse.getEntity());
		}
		return validatedConditionRelations;
	}
	
	public Reference validateSingleReference(DiseaseAnnotation uiEntity) {
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
		
		return singleRefResponse.getEntity();
	}
	
	public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		dbEntity = (DiseaseAnnotation) validateAuditedObjectFields(uiEntity, dbEntity);
		
		if (uiEntity.getModEntityId() != null)
			dbEntity.setModEntityId(uiEntity.getModEntityId());

		DOTerm term = validateObject(uiEntity, dbEntity);
		dbEntity.setObject(term);

		List<EcoTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
		dbEntity.setEvidenceCodes(terms);

		if (CollectionUtils.isNotEmpty(uiEntity.getWith())) {
			List<Gene> genes = validateWith(uiEntity);	
			dbEntity.setWith(genes);
		} else {
			dbEntity.setWith(null);
		}

		if(uiEntity.getNegated() != null) {
			dbEntity.setNegated(uiEntity.getNegated());
		}else{
			dbEntity.setNegated(false);
		}
		
		dbEntity.setAnnotationType(uiEntity.getAnnotationType());

		dbEntity.setGeneticSex(uiEntity.getGeneticSex());

		String dataProvider = validateDataProvider(uiEntity);
		dbEntity.setDataProvider(dataProvider);

		dbEntity.setSecondaryDataProvider(handleStringField(uiEntity.getSecondaryDataProvider()));
	
		BiologicalEntity diseaseGeneticModifier = validateDiseaseGeneticModifier(uiEntity);
		VocabularyTerm dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity);
		dbEntity.setDiseaseGeneticModifier(diseaseGeneticModifier);
		dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);
		
		if (CollectionUtils.isNotEmpty(uiEntity.getConditionRelations())) {
			List<ConditionRelation> conditionRelations = validateConditionRelations(uiEntity);
			dbEntity.setConditionRelations(conditionRelations);	
		} else {
			dbEntity.setConditionRelations(null);
		}
		
		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		dbEntity.setRelatedNotes(relatedNotes);
		
		if (CollectionUtils.isNotEmpty(uiEntity.getDiseaseQualifiers())) {
			dbEntity.setDiseaseQualifiers(uiEntity.getDiseaseQualifiers());
		} else {
			dbEntity.setDiseaseQualifiers(null);
		}
		
		Reference singleReference = validateSingleReference(uiEntity);
		dbEntity.setSingleReference(singleReference);
		
		return dbEntity;
	}
}
