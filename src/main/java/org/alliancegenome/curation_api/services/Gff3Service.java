package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.Gff3Constants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3Service  {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	
	@Transactional
	public GenomeAssembly loadGenomeAssembly(List<String> gffHeaderData, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				String assemblyName = header.split(" ")[1];
				Map<String, Object> params = new HashMap<>();
				params.put("modEntityId", assemblyName);
				params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
				params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
				
				SearchResponse<GenomeAssembly> resp = genomeAssemblyDAO.findByParams(params);
				if (resp != null && resp.getSingleResult() != null) {
					return resp.getSingleResult();
				}
				
				GenomeAssembly assembly = new GenomeAssembly();
				assembly.setModEntityId(assemblyName);
				assembly.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
				assembly.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
				
				return genomeAssemblyDAO.persist(assembly);
			}
		}
		throw new ObjectValidationException(gffHeaderData, "#!assembly - " + ValidationConstants.REQUIRED_MESSAGE);
	}
	
	public Map<String, List<Long>> loadEntities(BulkLoadFileHistory history, Gff3DTO gffEntry, Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		Map<String, String> attributes = getAttributes(gffEntry);
		if (StringUtils.equals(gffEntry.getType(), "exon")) {
			
		} else if (StringUtils.equals(gffEntry.getType(), "CDS")) {
			
		} else if (Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
			if (StringUtils.equals(gffEntry.getType(), "lnc_RNA")) {
				gffEntry.setType("lncRNA");
			}
		}
		return idsAdded;
	}
	
	public Map<String, List<Long>> loadAssociations(BulkLoadFileHistory history, Gff3DTO gffEntry, Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider, GenomeAssembly assembly) throws ObjectUpdateException {
		// TODO: implement association loading
		return idsAdded;
	}
	
	private Map<String, String> getAttributes (Gff3DTO gffEntry) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(gffEntry.getAttributes())) {
			for (String keyValue : gffEntry.getAttributes()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}
		return attributes;
	}
}
