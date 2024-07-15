package org.alliancegenome.curation_api.controllers.crud.associations.alleleAssociations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.alleleAssociations.AlleleGeneAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.alleleAssociations.AlleleGeneAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGeneAssociationCrudController extends
	BaseEntityCrudController<AlleleGeneAssociationService, AlleleGeneAssociation, AlleleGeneAssociationDAO> implements AlleleGeneAssociationCrudInterface {

	@Inject
	AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject
	AlleleGeneAssociationExecutor alleleGeneAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleGeneAssociationService);
	}

	@Override
	public ObjectResponse<AlleleGeneAssociation> update(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleGeneAssociation> create(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.upsert(entity);
	}

	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateAlleleGeneAssociations(String dataProvider,
			List<AlleleGeneAssociationDTO> associations) {
		return alleleGeneAssociationExecutor.runLoadApi(alleleGeneAssociationService, dataProvider, associations);
	}

	public ObjectResponse<AlleleGeneAssociation> getAssociation(Long alleleId, String relationName,
			Long geneId) {
		return alleleGeneAssociationService.getAssociation(alleleId, relationName, geneId);
	}
}
