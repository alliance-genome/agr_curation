package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleExecutor;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleCrudController extends SubmittedObjectCrudController<AlleleService, Allele, AlleleDTO, AlleleDAO> implements AlleleCrudInterface {

	@Inject
	AlleleService alleleService;

	@Inject
	AlleleExecutor alleleExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleService);
	}

	@Override
	public APIResponse updateAlleles(String dataProvider, List<AlleleDTO> alleleData) {
		return alleleExecutor.runLoadApi(alleleService, dataProvider, alleleData);
	}

	public ObjectResponse<Allele> updateDetail(Allele entity) {
		return alleleService.updateDetail(entity);
	}

	public ObjectListResponse<CrossReference> getCrossReferences(Long id) {
		return alleleService.getCrossReferences(id);
	}

	public ObjectListResponse<CrossReference> updateCrossReferences(Long id, List<CrossReference> crossReferences) {
		return alleleService.updateCrossReferences(id, crossReferences);
	}

	public ObjectListResponse<Reference> getReferences(Long id) {
		return alleleService.getReferences(id);
	}

	public ObjectListResponse<Reference> updateReferences(Long id, List<Reference> references) {
		return alleleService.updateReferences(id, references);
	}

	public ObjectListResponse<AlleleGeneAssociation> getGeneAssociations(Long id) {
		return alleleService.getGeneAssociations(id);
	}

	public ObjectListResponse<AlleleGeneAssociation> updateGeneAssociations(Long id, List<AlleleGeneAssociation> geneAssociations) {
		return alleleService.updateGeneAssociations(id, geneAssociations);
	}

	public ObjectListResponse<AlleleSynonymSlotAnnotation> getSynonyms(Long id) {
		return alleleService.getSynonyms(id);
	}

	public ObjectListResponse<AlleleSynonymSlotAnnotation> updateSynonyms(Long id, List<AlleleSynonymSlotAnnotation> synonyms) {
		return alleleService.updateSynonyms(id, synonyms);
	}

	public ObjectListResponse<AlleleSecondaryIdSlotAnnotation> getSecondaryIds(Long id) {
		return alleleService.getSecondaryIds(id);
	}

	public ObjectListResponse<AlleleSecondaryIdSlotAnnotation> updateSecondaryIds(Long id, List<AlleleSecondaryIdSlotAnnotation> secondaryIds) {
		return alleleService.updateSecondaryIds(id, secondaryIds);
	}

	public ObjectListResponse<AlleleNomenclatureEventSlotAnnotation> getNomenclatureEvents(Long id) {
		return alleleService.getNomenclatureEvents(id);
	}

	public ObjectListResponse<AlleleNomenclatureEventSlotAnnotation> updateNomenclatureEvents(Long id, List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents) {
		return alleleService.updateNomenclatureEvents(id, nomenclatureEvents);
	}

	public ObjectListResponse<AlleleMutationTypeSlotAnnotation> getMutationTypes(Long id) {
		return alleleService.getMutationTypes(id);
	}

	public ObjectListResponse<AlleleMutationTypeSlotAnnotation> updateMutationTypes(Long id, List<AlleleMutationTypeSlotAnnotation> mutationTypes) {
		return alleleService.updateMutationTypes(id, mutationTypes);
	}

	public ObjectListResponse<AlleleFunctionalImpactSlotAnnotation> getFunctionalImpacts(Long id) {
		return alleleService.getFunctionalImpacts(id);
	}

	public ObjectListResponse<AlleleFunctionalImpactSlotAnnotation> updateFunctionalImpacts(Long id, List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts) {
		return alleleService.updateFunctionalImpacts(id, functionalImpacts);
	}

	public ObjectListResponse<AlleleInheritanceModeSlotAnnotation> getInheritanceModes(Long id) {
		return alleleService.getInheritanceModes(id);
	}

	public ObjectListResponse<AlleleInheritanceModeSlotAnnotation> updateInheritanceModes(Long id, List<AlleleInheritanceModeSlotAnnotation> inheritanceModes) {
		return alleleService.updateInheritanceModes(id, inheritanceModes);
	}

	public ObjectListResponse<Note> getRelatedNotes(Long id) {
		return alleleService.getRelatedNotes(id);
	}

	public ObjectListResponse<Note> updateRelatedNotes(Long id, List<Note> relatedNotes) {
		return alleleService.updateRelatedNotes(id, relatedNotes);
	}

}
