package org.alliancegenome.curation_api.model.okta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OktaToken {
	private String token_type;
	private Integer expires_in;
	private String access_token;
}
