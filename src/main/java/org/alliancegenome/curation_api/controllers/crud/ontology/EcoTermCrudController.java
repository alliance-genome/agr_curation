package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.EcoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class EcoTermCrudController extends BaseOntologyTermController<EcoTermService, ECOTerm, EcoTermDAO> implements EcoTermCrudInterface {

	@Inject
	EcoTermService ecoTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(ecoTermService, ECOTerm.class);
	}

	@Override
	public String updateTerms(boolean async, String fullText) {
		String status = super.updateTerms(async, fullText);
		if (status.equals("OK")) {
			ecoTermService.updateAbbreviations();
		}
		return status;
	}

	public void updateAbbreviations() {
		ecoTermService.updateAbbreviations();
	}
}
