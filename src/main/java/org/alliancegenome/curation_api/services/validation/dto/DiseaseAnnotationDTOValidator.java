package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
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
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class DiseaseAnnotationDTOValidator extends BaseDTOValidator {

	@Inject ReferenceDAO referenceDAO;
	@Inject DoTermDAO doTermDAO;
	@Inject EcoTermDAO ecoTermDAO;
	@Inject ReferenceService referenceService;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject GeneDAO geneDAO;
	@Inject BiologicalEntityDAO biologicalEntityDAO;
	@Inject NoteDAO noteDAO;
	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject ConditionRelationDTOValidator conditionRelationDtoValidator;
	@Inject NoteDTOValidator noteDtoValidator;
	
	public <E extends DiseaseAnnotation, D extends DiseaseAnnotationDTO> ObjectResponse<E> validateAnnotationDTO(E annotation, D dto) {
		ObjectResponse<E> daResponse = validateAuditedObjectDTO(annotation, dto);
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
			Reference reference = referenceService.retrieveFromDbOrLiteratureService(dto.getSingleReference());
			if (reference == null)
				daResponse.addErrorMessage("singleReference", ValidationConstants.INVALID_MESSAGE);
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
		
		if (StringUtils.isNotBlank(dto.getSecondaryDataProvider())) {
			annotation.setSecondaryDataProvider(dto.getSecondaryDataProvider());
		} else {
			annotation.setSecondaryDataProvider(null);
		}
		
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
		} else {
			annotation.setDiseaseQualifiers(null);
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
		} else {
			annotation.setDiseaseGeneticModifier(null);
			annotation.setDiseaseGeneticModifierRelation(null);
		}
			
		VocabularyTerm annotationType = null;
		if (StringUtils.isNotBlank(dto.getAnnotationType())) {
			annotationType = vocabularyTermDAO.getTermInVocabulary(dto.getAnnotationType(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
			if (annotationType == null)
				daResponse.addErrorMessage("annotationType", ValidationConstants.INVALID_MESSAGE);
			}
		annotation.setAnnotationType(annotationType);
		
		VocabularyTerm geneticSex = null;
		if (StringUtils.isNotBlank(dto.getGeneticSex())) {
			geneticSex = vocabularyTermDAO.getTermInVocabulary(dto.getGeneticSex(), VocabularyConstants.GENETIC_SEX_VOCABULARY);
			if (geneticSex == null)
				daResponse.addErrorMessage("geneticSex", ValidationConstants.INVALID_MESSAGE);
		}	
		annotation.setGeneticSex(geneticSex);
		
		if (CollectionUtils.isNotEmpty(annotation.getRelatedNotes())) {
			annotation.getRelatedNotes().forEach(note -> {diseaseAnnotationDAO.deleteAttachedNote(note.getId());});
		}	
		if (CollectionUtils.isNotEmpty(dto.getRelatedNotes())) {
			List<Note> notes = new ArrayList<>();
			for (NoteDTO noteDTO : dto.getRelatedNotes()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
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
		} else {
			annotation.setRelatedNotes(null);
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
				ObjectResponse<ConditionRelation> crResponse = conditionRelationDtoValidator.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					daResponse.addErrorMessage("conditionRelations", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		} else {
			annotation.setConditionRelations(null);
		}

		daResponse.setEntity(annotation);
		return daResponse;
	}
}
