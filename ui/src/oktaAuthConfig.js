const oktaDomain = "dev-30456587.okta.com"
const clientId = '0oa1bn1wjdZiJWaJe5d7';
const issuer = 'https://' + oktaDomain + '/oauth2/default';
const baseUrl = 'https://' + oktaDomain;
const googleId = "0oa1crmy0xAPqqTa35d7";
const microsoftId = "0oa1lfrktejLMnemp5d7";

const oktaAuthConfig = {
	// Note: If your app is configured to use the Implicit flow
	// instead of the Authorization Code with Proof of Code Key Exchange (PKCE)
	// you will need to add `pkce: false`
	issuer: issuer,
	clientId: clientId,
	redirectUri: window.location.origin + '/login/callback',
	scopes: ['openid', 'profile', 'email'],
	pkce: true,
};


const oktaSignInConfig = {
	baseUrl: baseUrl,
	clientId: clientId,
	redirectUri: window.location.origin + '/login/callback',
	scopes: ['openid', 'profile', 'email'],
	authParams: {
		pkce: true

	},
	// Additional documentation on config options can be found at https://github.com/okta/okta-signin-widget#basic-config-options
	idps: [
		{ type: 'google', id: googleId },
		{ type: 'microsoft', id: microsoftId }
	],
	idpDisplay: "SCONDARY"

};

export { oktaAuthConfig, oktaSignInConfig };
