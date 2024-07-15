package org.alliancegenome.curation_api.model.mati;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Identifier {
	private Long counter;
	@JsonProperty("subdomain_code")
	private String subdomainCode;
	@JsonProperty("subdomain_name")
	private String subdomainName;
	private String curie;
}
