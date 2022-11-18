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

		if (StringUtils.isBlank(dto.getDoTermCurie())) {
			daResponse.addErrorMessage("do_term_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			DOTerm disease = doTermDAO.find(dto.getDoTermCurie());
			if (disease == null) 
				daResponse.addErrorMessage("do_term_curie", ValidationConstants.INVALID_MESSAGE);
			annotation.setObject(disease);
		}
		
		if (CollectionUtils.isEmpty(dto.getEvidenceCodeCuries())) {
			daResponse.addErrorMessage("evidence_code_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<ECOTerm> ecoTerms = new ArrayList<>();
			for (String ecoCurie : dto.getEvidenceCodeCuries()) {
				ECOTerm ecoTerm = ecoTermDAO.find(ecoCurie);
				if (ecoTerm == null) {
					daResponse.addErrorMessage("evidence_code_curies", ValidationConstants.INVALID_MESSAGE);
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

		if (CollectionUtils.isNotEmpty(dto.getWithGeneCuries())) {
			List<Gene> withGenes = new ArrayList<>();
			for (String withCurie : dto.getWithGeneCuries()) {
				if (!withCurie.startsWith("HGNC:")) {
					daResponse.addErrorMessage("with_gene_curies", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				Gene withGene = geneDAO.getByIdOrCurie(withCurie);
				if (withGene == null) {
					daResponse.addErrorMessage("with_gene_curies", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				withGenes.add(withGene);
			}
			annotation.setWith(withGenes);
		} else {
			annotation.setWith(null);
		}

		if (StringUtils.isBlank(dto.getDataProvider()))
			daResponse.addErrorMessage("data_provider", ValidationConstants.REQUIRED_MESSAGE);
		annotation.setDataProvider(dto.getDataProvider());
		
		if (StringUtils.isNotBlank(dto.getSecondaryDataProvider())) {
			annotation.setSecondaryDataProvider(dto.getSecondaryDataProvider());
		} else {
			annotation.setSecondaryDataProvider(null);
		}
		
		if (CollectionUtils.isNotEmpty(dto.getDiseaseQualifierNames())) {
			List<VocabularyTerm> diseaseQualifiers = new ArrayList<>();
			for (String qualifier : dto.getDiseaseQualifierNames()) {
				VocabularyTerm diseaseQualifier = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, qualifier);
				if (diseaseQualifier == null) {
					daResponse.addErrorMessage("disease_qualifier_names", ValidationConstants.INVALID_MESSAGE);
				}
				diseaseQualifiers.add(diseaseQualifier);
			}
			annotation.setDiseaseQualifiers(diseaseQualifiers);
		} else {
			annotation.setDiseaseQualifiers(null);
		}

		if (StringUtils.isNotBlank(dto.getDiseaseGeneticModifierCurie()) || StringUtils.isNotBlank(dto.getDiseaseGeneticModifierRelationName())) {
			if (StringUtils.isBlank(dto.getDiseaseGeneticModifierCurie())) {
				daResponse.addErrorMessage("disease_genetic_modifier_relation_name", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_curie");
			} else if (StringUtils.isBlank(dto.getDiseaseGeneticModifierRelationName())) {
				daResponse.addErrorMessage("disease_genetic_modifier_curie", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_relation_name");
			} else {
				VocabularyTerm diseaseGeneticModifierRelation = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, dto.getDiseaseGeneticModifierRelationName());
				if (diseaseGeneticModifierRelation == null)
					daResponse.addErrorMessage("disease_genetic_modifier_relation_name", ValidationConstants.INVALID_MESSAGE);
				BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifierCurie());
				if (diseaseGeneticModifier == null)
					daResponse.addErrorMessage("disease_genetic_modifier_curie", ValidationConstants.INVALID_MESSAGE);
				annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
				annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
			}
		} else {
			annotation.setDiseaseGeneticModifier(null);
			annotation.setDiseaseGeneticModifierRelation(null);
		}
			
		VocabularyTerm annotationType = null;
		if (StringUtils.isNotBlank(dto.getAnnotationTypeName())) {
			annotationType = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, dto.getAnnotationTypeName());
			if (annotationType == null)
				daResponse.addErrorMessage("annotation_type_name", ValidationConstants.INVALID_MESSAGE);
			}
		annotation.setAnnotationType(annotationType);
		
		VocabularyTerm geneticSex = null;
		if (StringUtils.isNotBlank(dto.getGeneticSexName())) {
			geneticSex = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY, dto.getGeneticSexName());
			if (geneticSex == null)
				daResponse.addErrorMessage("genetic_sex_name", ValidationConstants.INVALID_MESSAGE);
		}	
		annotation.setGeneticSex(geneticSex);
		
		if (CollectionUtils.isNotEmpty(annotation.getRelatedNotes())) {
			annotation.getRelatedNotes().forEach(note -> {diseaseAnnotationDAO.deleteAttachedNote(note.getId());});
		}	
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			List<Note> notes = new ArrayList<>();
			for (NoteDTO noteDTO : dto.getNoteDtos()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
				if (noteResponse.hasErrors()) {
					daResponse.addErrorMessage("note_dtos", noteResponse.errorMessagesString());
					break;
				}
				if (CollectionUtils.isNotEmpty(noteDTO.getEvidenceCuries())) {
					for (String noteRef : noteDTO.getEvidenceCuries()) {
						if (!noteRef.equals(dto.getReferenceCurie())) {
							daResponse.addErrorMessage("relatedNotes - evidence_curies", ValidationConstants.INVALID_MESSAGE);
						}
					}
				}
				notes.add(noteDAO.persist(noteResponse.getEntity()));
			}
			annotation.setRelatedNotes(notes);
		} else {
			annotation.setRelatedNotes(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelationDtos())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelationDtos()) {
				if (conditionRelationDTO.getHandle() != null) {
					if (!conditionRelationDTO.getReferenceCurie().equals(dto.getReferenceCurie())) {
						daResponse.addErrorMessage("condition_relation_dtos - handle", ValidationConstants.INVALID_MESSAGE);
						break;
					}
				}
				ObjectResponse<ConditionRelation> crResponse = conditionRelationDtoValidator.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					daResponse.addErrorMessage("condition_relation_dtos", crResponse.errorMessagesString());
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
	
	public <E extends DiseaseAnnotation, D extends DiseaseAnnotationDTO> ObjectResponse<E> validateReference (E annotation, D dto) {
		ObjectResponse<E> daResponse = new ObjectResponse<E>();
		
		Reference reference = null;
		if (StringUtils.isBlank(dto.getReferenceCurie())) {
			daResponse.addErrorMessage("reference_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			reference = referenceService.retrieveFromDbOrLiteratureService(dto.getReferenceCurie());
			if (reference == null)
				daResponse.addErrorMessage("reference_curie", ValidationConstants.INVALID_MESSAGE);
		}	
		annotation.setSingleReference(reference);
		
		daResponse.setEntity(annotation);
		return daResponse;
	}
}
