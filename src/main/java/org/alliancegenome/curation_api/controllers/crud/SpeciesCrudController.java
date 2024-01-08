package org.alliancegenome.curation_api.controllers.crud;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.interfaces.crud.SpeciesCrudInterface;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.services.SpeciesService;

@RequestScoped
public class SpeciesCrudController extends BaseEntityCrudController<SpeciesService, Species, SpeciesDAO> implements SpeciesCrudInterface {

	@Inject
	SpeciesService speciesService;

	@Override
	@PostConstruct
	protected void init() {
		setService(speciesService);
	}
}
