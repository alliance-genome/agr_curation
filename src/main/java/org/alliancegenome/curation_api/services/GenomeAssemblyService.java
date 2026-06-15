package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenomeAssemblyService extends BaseEntityCrudService<GenomeAssembly, GenomeAssemblyDAO> {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomeAssemblyDAO);
	}
	
	/**
	 * Looks up an existing GenomeAssembly by its name (primaryExternalId) for the
	 * given data provider's organization and taxon. Returns null if none exists.
	 * Assemblies are not auto-created here: a GFF load that references an unknown
	 * assembly is failed by the caller (see Gff3Executor.validateGffAssembly).
	 */
	public GenomeAssembly findByName(String assemblyName, BackendBulkDataProvider dataProvider) {
		if (StringUtils.isBlank(assemblyName)) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("primaryExternalId", assemblyName);
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);

		SearchResponse<GenomeAssembly> resp = genomeAssemblyDAO.findByParams(params);
		return (resp == null) ? null : resp.getSingleResult();
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
