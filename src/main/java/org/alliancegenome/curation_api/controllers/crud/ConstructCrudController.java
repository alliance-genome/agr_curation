package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.interfaces.crud.ConstructCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.ConstructExecutor;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ConstructService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructCrudController extends SubmittedObjectCrudController<ConstructService, Construct, ConstructDTO, ConstructDAO> implements ConstructCrudInterface {

	@Inject
	ConstructService constructService;
	@Inject
	ConstructExecutor constructExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(constructService);
	}

	@Override
	public ObjectResponse<Construct> update(Construct entity) {
		ObjectResponse<Construct> response = constructService.update(entity);
		return constructService.getById(response.getEntity().getId());
	}

	public ObjectResponse<Construct> updateDetail(Construct entity) {
		ObjectResponse<Construct> response = constructService.updateDetail(entity);
		return constructService.getById(response.getEntity().getId());
	}

	public ObjectListResponse<ConstructSynonymSlotAnnotation> getSynonyms(Long id) {
		return constructService.getSynonyms(id);
	}

	public ObjectListResponse<ConstructSynonymSlotAnnotation> updateSynonyms(Long id, List<ConstructSynonymSlotAnnotation> synonyms) {
		return constructService.updateSynonyms(id, synonyms);
	}

	public ObjectListResponse<String> getSecondaryIds(Long id) {
		return constructService.getSecondaryIds(id);
	}

	public ObjectListResponse<String> updateSecondaryIds(Long id, List<String> secondaryIds) {
		return constructService.updateSecondaryIds(id, secondaryIds);
	}

	public ObjectListResponse<Note> getRelatedNotes(Long id) {
		return constructService.getRelatedNotes(id);
	}

	public ObjectListResponse<Note> updateRelatedNotes(Long id, List<Note> relatedNotes) {
		return constructService.updateRelatedNotes(id, relatedNotes);
	}

	public ObjectListResponse<Reference> getReferences(Long id) {
		return constructService.getReferences(id);
	}

	public ObjectListResponse<Reference> updateReferences(Long id, List<Reference> references) {
		return constructService.updateReferences(id, references);
	}

	public ObjectListResponse<ConstructCassetteAssociation> getCassetteAssociations(Long id) {
		return constructService.getCassetteAssociations(id);
	}

	public ObjectListResponse<ConstructCassetteAssociation> updateCassetteAssociations(Long id, List<ConstructCassetteAssociation> cassetteAssociations) {
		constructService.updateCassetteAssociations(id, cassetteAssociations);
		return constructService.getCassetteAssociations(id);
	}

	@Override
	public ObjectResponse<Construct> create(Construct entity) {
		ObjectResponse<Construct> response = constructService.create(entity);
		return constructService.getById(response.getEntity().getId());
	}

	@Override
	public APIResponse updateConstructs(String dataProvider, List<ConstructDTO> annotations) {
		return constructExecutor.runLoadApi(constructService, dataProvider, annotations);
	}

	
}
