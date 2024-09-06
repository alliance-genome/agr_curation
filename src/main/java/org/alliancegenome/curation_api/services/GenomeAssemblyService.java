package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenomeAssemblyService extends BaseEntityCrudService<GenomeAssembly, GenomeAssemblyDAO> {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomeAssemblyDAO);
	}
	
	public GenomeAssembly getOrCreate(String assemblyName, BackendBulkDataProvider dataProvider) {

		if (StringUtils.isNotBlank(assemblyName)) {
			Map<String, Object> params = new HashMap<>();
			params.put("modEntityId", assemblyName);
			params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
	
			SearchResponse<GenomeAssembly> resp = genomeAssemblyDAO.findByParams(params);
			if (resp == null || resp.getSingleResult() == null) {
				GenomeAssembly assembly = new GenomeAssembly();
				assembly.setModEntityId(assemblyName);
				assembly.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.sourceOrganization));
				assembly.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
	
				return genomeAssemblyDAO.persist(assembly);
			} else {
				return resp.getSingleResult();
			}
		} else {
			return null;
		}
	}
	
}
