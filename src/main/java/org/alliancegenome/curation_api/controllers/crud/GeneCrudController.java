package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneCrudController extends SubmittedObjectCrudController<GeneService, Gene, GeneDTO, GeneDAO> implements GeneCrudInterface {

	@Inject
	GeneService geneService;

	@Inject
	GeneExecutor geneExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneService);
	}

	@Override
	public APIResponse updateGenes(String dataProvider, List<GeneDTO> geneData) {
		return geneExecutor.runLoadApi(geneService, dataProvider, geneData);
	}

	public ObjectResponse<Gene> updateDetail(Gene entity) {
		return geneService.updateDetail(entity);
	}

	public ObjectListResponse<CrossReference> getCrossReferences(Long id) {
		return geneService.getCrossReferences(id);
	}

	public ObjectListResponse<CrossReference> updateCrossReferences(Long id, List<CrossReference> crossReferences) {
		return geneService.updateCrossReferences(id, crossReferences);
	}

	public ObjectListResponse<GeneSynonymSlotAnnotation> getSynonyms(Long id) {
		return geneService.getSynonyms(id);
	}

	public ObjectListResponse<GeneSynonymSlotAnnotation> updateSynonyms(Long id, List<GeneSynonymSlotAnnotation> synonyms) {
		return geneService.updateSynonyms(id, synonyms);
	}

	public ObjectListResponse<GeneSecondaryIdSlotAnnotation> getSecondaryIds(Long id) {
		return geneService.getSecondaryIds(id);
	}

	public ObjectListResponse<GeneSecondaryIdSlotAnnotation> updateSecondaryIds(Long id, List<GeneSecondaryIdSlotAnnotation> secondaryIds) {
		return geneService.updateSecondaryIds(id, secondaryIds);
	}

	public ObjectListResponse<Note> getRelatedNotes(Long id) {
		return geneService.getRelatedNotes(id);
	}

	public ObjectListResponse<Note> updateRelatedNotes(Long id, List<Note> relatedNotes) {
		return geneService.updateRelatedNotes(id, relatedNotes);
	}

}
