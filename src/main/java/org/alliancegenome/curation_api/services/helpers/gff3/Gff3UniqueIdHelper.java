package org.alliancegenome.curation_api.services.helpers.gff3;

import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Gff3UniqueIdHelper {

	public static String getExonOrCodingSequenceUniqueId(Gff3DTO dto, Map<String, String> attributes, BackendBulkDataProvider dataProvider) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		
		if (attributes.containsKey("ID")) {
			uniqueId.add(removeWormBasePrefix(attributes.get("ID"), dataProvider));
		} else if (attributes.containsKey("Name")) {
			uniqueId.add(attributes.get("Name"));
		}
		
		if (attributes.containsKey("Parent")) {
			uniqueId.add(removeWormBasePrefix(attributes.get("Parent"), dataProvider));
		}
		
		uniqueId.add(dto.getSeqId());
		uniqueId.add(dto.getStart());
		uniqueId.add(dto.getEnd());
		uniqueId.add(dto.getStrand());
		
		return uniqueId.getUniqueId();
	}

	private static String removeWormBasePrefix(String id, BackendBulkDataProvider dataProvider) {
		if (StringUtils.equals("WB", dataProvider.sourceOrganization)) {
			String[] idParts = id.split(":");
			if (idParts.length > 1) {
				id = idParts[1];
			}
		}
		return id;
	}

}
