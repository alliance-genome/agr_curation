package org.alliancegenome.curation_api.services.helpers.gff3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
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

		if (StringUtils.equals(dataProvider.sourceOrganization, "WB")) {
			for (String key : List.of("ID", "Parent")) {
				if (attributes.containsKey(key)) {
					String id = attributes.get(key);
					String[] idParts = id.split(":");
					if (idParts.length > 1) {
						id = idParts[1];
					}
					attributes.put(key, id);
				}
			}
		}
		
		return attributes;
	}

}
