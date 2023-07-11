package org.alliancegenome.curation_api.services.orthology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.OrthologyFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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

	public GeneToGeneOrthologyGenerated upsert(OrthologyFmsDTO orthoPair) throws ObjectUpdateException {
		return orthologyFmsDtoValidator.validateOrthologyFmsDTO(orthoPair);		
	}

	public List<Object[]> getAllOrthologyPairsBySubjectGeneDataProvider(String dataProvider) {
		if (StringUtils.equals(dataProvider,"XBXL")) {
			return geneToGeneOrthologyGeneratedDAO.findAllOrthologyPairsBySubjectGeneDataProviderAndTaxon("XB", "NCBITaxon:8355");
		}
		if (StringUtils.equals(dataProvider,"XBXT")) {
			return geneToGeneOrthologyGeneratedDAO.findAllOrthologyPairsBySubjectGeneDataProviderAndTaxon("XB", "NCBITaxon:8364");
		}
		return geneToGeneOrthologyGeneratedDAO.findAllOrthologyPairsBySubjectGeneDataProvider(dataProvider);
	}

	public void removeNonUpdated(Pair<String, String> pairToRemove) {
		Map<String, Object> params = new HashMap<>();
		params.put("subjectGene.curie", pairToRemove.getLeft());
		params.put("objectGene.curie", pairToRemove.getRight());
		SearchResponse<GeneToGeneOrthologyGenerated> response = geneToGeneOrthologyGeneratedDAO.findByParams(params);
		if (response != null && response.getSingleResult() != null) {
			geneToGeneOrthologyGeneratedDAO.remove(response.getSingleResult().getId());
		} else {
			log.error("Failed getting orthology pair: " + pairToRemove.getLeft() + "|" + pairToRemove.getRight());
		}
	}

}
