package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.VariantValidator;
import org.alliancegenome.curation_api.services.validation.dto.VariantDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantService extends SubmittedObjectCrudService<Variant, VariantDTO, VariantDAO> {

	@Inject VariantDAO variantDAO;
	@Inject NoteService noteService;
	@Inject VariantValidator variantValidator;
	@Inject VariantDTOValidator variantDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(variantDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Variant> update(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantUpdate(uiEntity);
		loadGenomicLocationLists(dbEntity);
		return new ObjectResponse<Variant>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Variant> create(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantCreate(uiEntity);
		loadGenomicLocationLists(dbEntity);
		return new ObjectResponse<Variant>(dbEntity);
	}

	private void loadGenomicLocationLists(Variant variant) {
		if (variant.getCuratedVariantGenomicLocations() == null) {
			return;
		}
		for (CuratedVariantGenomicLocationAssociation location : variant.getCuratedVariantGenomicLocations()) {
			loadEvidence(location.getEvidence());
			AssemblyComponent chromosome = location.getVariantGenomicLocationAssociationObject();
			if (chromosome != null) {
				loadGenomicEntityLists(chromosome);
				GenomeAssembly assembly = chromosome.getGenomeAssembly();
				if (assembly != null) {
					loadNotes(assembly.getRelatedNotes());
					loadGenomicEntityLists(assembly.getSpecimenGenomicModel());
				}
			}
			if (location.getPredictedVariantConsequences() != null) {
				for (PredictedVariantConsequence consequence : location.getPredictedVariantConsequences()) {
					if (consequence.getVepConsequences() != null) {
						consequence.getVepConsequences().size();
					}
					loadGenomicEntityLists(consequence.getVariantTranscript());
				}
			}
		}
	}

	private void loadGenomicEntityLists(GenomicEntity entity) {
		if (entity == null) {
			return;
		}
		loadNotes(entity.getRelatedNotes());
		if (entity.getCrossReferences() != null) {
			entity.getCrossReferences().size();
		}
	}

	private void loadNotes(List<Note> notes) {
		if (notes == null) {
			return;
		}
		for (Note note : notes) {
			if (note.getReferences() != null) {
				note.getReferences().forEach(this::loadCrossReferences);
			}
		}
	}

	private void loadEvidence(List<InformationContentEntity> evidence) {
		if (evidence == null) {
			return;
		}
		for (InformationContentEntity entity : evidence) {
			if (entity instanceof Reference reference) {
				loadCrossReferences(reference);
			}
		}
	}

	private void loadCrossReferences(Reference reference) {
		if (reference.getCrossReferences() != null) {
			reference.getCrossReferences().size();
		}
	}

	@Override
	public ObjectResponse<Variant> upsert(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return variantDtoValidator.validateVariantDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public Variant deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Variant variant = variantDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (variant != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(variant.getAlleleVariantAssociations())) {
				deprecationReasons.add("Variant has allele association(s)");
			}
			if (CollectionUtils.isNotEmpty(variant.getCuratedVariantGenomicLocations())) {
				deprecationReasons.add("Variant has curated genomic location(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!variant.getObsolete()) {
					variant.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					variant.setDateUpdated(OffsetDateTime.now());
					variant.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(variant.getIdentifier(), requestSource, deprecationReasons);
					if (variant.getRelatedNotes() == null) {
						variant.setRelatedNotes(new ArrayList<>());
					}
					variant.getRelatedNotes().add(deprecationNote);
					
					return variantDAO.persist(variant);
				} else {
					return variant;
				}
			} else {
				variantDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Variant with id: " + id;
			if (throwApiError) {
				ObjectResponse<Variant> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = variantDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	public ObjectListResponse<String> findAllVariantNames() {
		return variantDAO.getAllVariantNames();
	}

}
