package org.alliancegenome.curation_api.services.orthology;

import java.time.OffsetDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class GeneToGeneOrthologyService extends BaseEntityCrudService<GeneToGeneOrthology, GeneToGeneOrthologyDAO> {

	@Inject
	GeneToGeneOrthologyDAO geneToGeneOrthologyDAO;
	@Inject
	PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneOrthologyDAO);
	}

	@Transactional
	public GeneToGeneOrthology deprecateOrthologyPair(Long orthId, String loadDescription) {
		GeneToGeneOrthology orthoPair = geneToGeneOrthologyDAO.find(orthId);
		
		if (orthoPair == null)
			return null;
		
		if (!orthoPair.getObsolete()) {
			orthoPair.setObsolete(true);
			orthoPair.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
			orthoPair.setDateUpdated(OffsetDateTime.now());
			return geneToGeneOrthologyDAO.persist(orthoPair);
		}
		
		return null;
	}

}
