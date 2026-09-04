package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AssemblyComponentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AssemblyComponentService extends BaseEntityCrudService<AssemblyComponent, AssemblyComponentDAO> {

	@Inject AssemblyComponentDAO assemblyComponentDAO;
	@Inject GenomeAssemblyService genomeAssemblyService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject OrganizationService organizationService;
	@Inject ChromosomeAccessionService chromosomeAccessionService;

	Date assemblyComponentRequest;
	HashMap<String, AssemblyComponent> assemblyComponentCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(assemblyComponentDAO);
	}

	@Transactional
	public AssemblyComponent fetchOrCreate(String name, String assemblyId, String taxonCurie, BackendBulkDataProvider dataProvider) {
		AssemblyComponent assemblyComponent = null;
		if (assemblyComponentRequest != null) {
			UniqueIdGeneratorHelper uniqueIdGen = new UniqueIdGeneratorHelper();
			uniqueIdGen.add(name);
			uniqueIdGen.add(assemblyId);
			uniqueIdGen.add(taxonCurie);
			uniqueIdGen.add(dataProvider.sourceOrganization);
			String uniqueId = uniqueIdGen.getUniqueId();
			if (assemblyComponentCacheMap.containsKey(uniqueId)) {
				assemblyComponent = assemblyComponentCacheMap.get(uniqueId);
			} else {
				Log.debug("AssemblyComponent not cached, caching name|assembly: (" + uniqueId + ")");
				assemblyComponent = findAssemblyComponentOrCreateDB(name, assemblyId, taxonCurie, dataProvider);
				assemblyComponentCacheMap.put(uniqueId, assemblyComponent);
			}
		} else {
			assemblyComponent = findAssemblyComponentOrCreateDB(name, assemblyId, taxonCurie, dataProvider);
			assemblyComponentRequest = new Date();
		}
		return assemblyComponent;
	}

	private AssemblyComponent findAssemblyComponentOrCreateDB(String name, String assemblyId, String taxonCurie, BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put(EntityFieldConstants.ASSEMBLY, assemblyId);
		params.put(EntityFieldConstants.TAXON, taxonCurie);
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		SearchResponse<AssemblyComponent> assemblyComponentResponse = assemblyComponentDAO.findByParams(params);
		if (assemblyComponentResponse != null && assemblyComponentResponse.getResults().size() > 0) {
			return assemblyComponentResponse.getSingleResult();
		}

		String primaryExternalId = chromosomeAccessionService.getChromosomeAccession(name, assemblyId);

		// SCRUM-6258: a sequence can keep its accession across assembly versions - the
		// mitochondrion is unchanged between GRCz11 and GRCz12tu (RefSeq:NC_002333.2) and
		// between mRatBN7.2 and GRCr8 (RefSeq:NC_001665.2), so the chromosomeaccession table maps
		// both assemblies of each pair to the same accession. The lookup above is scoped to a
		// single assembly and therefore misses the component stored under the older one, but
		// primaryExternalId is globally unique on BiologicalEntity, so persisting a second
		// component for the same accession violates biologicalentity_primaryexternalid_uk and
		// fails the load. Reuse the existing component and re-point it at the assembly now
		// being loaded.
		if (primaryExternalId != null) {
			SearchResponse<AssemblyComponent> existingByAccession = assemblyComponentDAO.findByField("primaryExternalId", primaryExternalId);
			if (existingByAccession != null && existingByAccession.getResults().size() > 0) {
				AssemblyComponent existingComponent = existingByAccession.getSingleResult();
				Log.info("AssemblyComponent " + primaryExternalId + " (" + name + ") already exists under a different assembly; re-pointing it to " + assemblyId);
				existingComponent.setGenomeAssembly(genomeAssemblyService.getOrCreate(assemblyId, dataProvider));
				return assemblyComponentDAO.merge(existingComponent);
			}
		}

		AssemblyComponent assemblyComponent = new AssemblyComponent();
		assemblyComponent.setName(name);
		GenomeAssembly genomeAssembly = genomeAssemblyService.getOrCreate(assemblyId, dataProvider);
		assemblyComponent.setGenomeAssembly(genomeAssembly);
		assemblyComponent.setTaxon(ncbiTaxonTermService.getByCurie(taxonCurie).getEntity());
		assemblyComponent.setDataProvider(organizationService.getByAbbr(dataProvider.sourceOrganization).getEntity());
		assemblyComponent.setPrimaryExternalId(primaryExternalId);
		return assemblyComponentDAO.persist(assemblyComponent);
	}

	public ObjectResponse<AssemblyComponent> deleteByIdentifier(String identifierString) {
		AssemblyComponent assemblyComponent = findByAlternativeFields(List.of("primaryExternalId", "modInternalId"), identifierString);
		if (assemblyComponent != null) {
			assemblyComponentDAO.remove(assemblyComponent.getId());
		}
		ObjectResponse<AssemblyComponent> ret = new ObjectResponse<>(assemblyComponent);
		return ret;
	}

}
