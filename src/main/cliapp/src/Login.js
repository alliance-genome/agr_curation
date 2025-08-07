import React from 'react';
import OktaSignInWidget from './OktaSignInWidget';
import { useOktaAuth } from '@okta/okta-react';
import { oktaSignInConfig } from './oktaAuthConfig';
import { useCookies } from 'react-cookie';

export const Login = ({ children }) => {
	const { oktaAuth, authState } = useOktaAuth();
	const [, setCookie] = useCookies(['okta-token-cookie']);

	const onSuccess = (tokens) => {
		//set cookie max age to 12 hours
		setCookie('okta-token-cookie', tokens.accessToken.accessToken, { maxAge: 60 * 60 * 12 });
		oktaAuth.handleLoginRedirect(tokens);
	};

	const onError = (err) => {
		console.log('error logging in', err);
	};

	return authState?.isAuthenticated ? (
		children
	) : (
		<OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError} />
	);
};
