package org.alliancegenome.curation_api.services.orthology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.OrthologyFmsDTOValidator;
import org.apache.commons.lang3.tuple.Pair;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneToGeneOrthologyGeneratedService extends BaseEntityCrudService<GeneToGeneOrthologyGenerated, GeneToGeneOrthologyGeneratedDAO> {

	@Inject
	GeneToGeneOrthologyGeneratedDAO geneToGeneOrthologyGeneratedDAO;
	@Inject
	OrthologyFmsDTOValidator orthologyFmsDtoValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneOrthologyGeneratedDAO);
	}

	public GeneToGeneOrthologyGenerated upsert(OrthologyFmsDTO orthoPair, Map<String, Map<String, VocabularyTerm>> validTerms) throws ObjectUpdateException {
		return orthologyFmsDtoValidator.validateOrthologyFmsDTO(orthoPair, validTerms);		
	}

	public List<Object[]> getAllOrthologyPairsBySubjectGeneDataProvider(String dataProvider) {
		return geneToGeneOrthologyGeneratedDAO.findAllOrthologyPairsBySubjectGeneDataProvider(dataProvider);
	}

	public void removeNonUpdated(Pair<String, String> pairToRemove) {
		Map<String, Object> params = new HashMap<>();
		params.put("subjectGene.curie", pairToRemove.getLeft());
		params.put("objectGene.curie", pairToRemove.getRight());
		SearchResponse<GeneToGeneOrthologyGenerated> response = geneToGeneOrthologyGeneratedDAO.findByParams(null, params);
		if (response != null && response.getSingleResult() != null) {
			geneToGeneOrthologyGeneratedDAO.remove(response.getSingleResult().getId());
		} else {
			log.error("Failed getting orthology pair: " + pairToRemove.getLeft() + "|" + pairToRemove.getRight());
		}
	}

}
