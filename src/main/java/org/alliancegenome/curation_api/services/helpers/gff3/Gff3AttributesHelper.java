package org.alliancegenome.curation_api.services.helpers.gff3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class Gff3AttributesHelper {

	public static Map<String, String> getAttributes(Gff3DTO dto, BackendBulkDataProvider dataProvider) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(dto.getAttributes())) {
			for (String keyValue : dto.getAttributes()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}

		// Ensure identifiers have MOD prefix
		for (String key : List.of("ID", "Parent")) {
			if (attributes.containsKey(key)) {
				String idsString = attributes.get(key);
				if (StringUtils.equals(dataProvider.sourceOrganization, "WB")) {
					// Remove prefixes like Gene: and Transcript: from WB identifiers
					idsString = idsString.replaceAll("Gene:", "");
					idsString = idsString.replaceAll("Transcript:", "");
					idsString = idsString.replaceAll("CDS:", "");
					idsString = idsString.replaceAll("Pseudogene:", "");
				}
				String[] idsList = idsString.split(",");
				List<String> processedIdList = new ArrayList<>();
				for (String id : idsList) {
					String[] idParts = id.split(":");
					if (idParts.length == 1) {
						id = dataProvider.name() + ':' + idParts[0];
					}
					processedIdList.add(id);
				}
				attributes.put(key, String.join(",", processedIdList));
			}
		}
		
		return attributes;
	}

}
