package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.response.ObjectResponse;
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
	@Inject OrganizationService organizationService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomeAssemblyDAO);
	}
	
	public GenomeAssembly getOrCreate(String assemblyName, Species species) {

		if (StringUtils.isNotBlank(assemblyName)) {
			Map<String, Object> params = new HashMap<>();
			params.put("primaryExternalId", assemblyName);
			params.put(EntityFieldConstants.DATA_PROVIDER, species.getDataProvider().getAbbreviation());
			params.put(EntityFieldConstants.TAXON, species.getTaxon().getCurie());
	
			SearchResponse<GenomeAssembly> resp = genomeAssemblyDAO.findByParams(params);
			if (resp == null || resp.getSingleResult() == null) {
				GenomeAssembly assembly = new GenomeAssembly();
				assembly.setPrimaryExternalId(assemblyName);
				assembly.setDataProvider(organizationService.getByAbbr(species.getDataProvider().getAbbreviation()).getEntity());
				assembly.setTaxon(ncbiTaxonTermService.getByCurie(species.getTaxon().getCurie()).getEntity());
	
				return genomeAssemblyDAO.persist(assembly);
			} else {
				return resp.getSingleResult();
			}
		} else {
			return null;
		}
	}

	public ObjectResponse<GenomeAssembly> deleteByIdentifier(String identifierString) {
		GenomeAssembly assembly = findByAlternativeFields(List.of("primaryExternalId", "modInternalId"), identifierString);
		if (assembly != null) {
			genomeAssemblyDAO.remove(assembly.getId());
		}
		ObjectResponse<GenomeAssembly> ret = new ObjectResponse<>(assembly);
		return ret;
	}
	
}
