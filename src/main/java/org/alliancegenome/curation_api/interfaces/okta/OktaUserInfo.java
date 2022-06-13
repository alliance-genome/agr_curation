package org.alliancegenome.curation_api.interfaces.okta;

import lombok.*;

@Data @ToString
public class OktaUserInfo {
	private Boolean active;
	private String token_type;
	private String scope;
	private String client_id;
	private String username;
	private Long exp;
	private Long iat;
	private String sub;
	private String aud;
	private String iss;
	private String jti;
	private String uid;
}
