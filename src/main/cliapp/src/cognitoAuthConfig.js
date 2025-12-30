export const COGNITO_CLIENT_ID = '28hmqobfq1jnp4abm1c5l0bb5j';
export const COGNITO_DOMAIN = 'auth.alliancegenome.org';

const cognitoConfig = {
	Auth: {
		Cognito: {
			userPoolId: 'us-east-1_d3eK6SYpI',
			userPoolClientId: COGNITO_CLIENT_ID,
			loginWith: {
				oauth: {
					domain: COGNITO_DOMAIN,
					scopes: ['openid', 'profile', 'email'],
					redirectSignIn: [window.location.origin + '/'],
					redirectSignOut: [window.location.origin + '/'],
					responseType: 'code', // Authorization code flow with PKCE
				},
			},
		},
	},
};

export default cognitoConfig;
