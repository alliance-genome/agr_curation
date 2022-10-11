import React from 'react';
import OktaSignInWidget from './OktaSignInWidget';
import { useOktaAuth } from '@okta/okta-react';
import { oktaSignInConfig } from './oktaAuthConfig';

export const Login = ({ children }) => {
	const { oktaAuth, authState } = useOktaAuth();


	const onSuccess = (tokens) => {
		oktaAuth.handleLoginRedirect(tokens);
	};

	const onError = (err) => {
		console.log('error logging in', err);
	};

	return authState?.isAuthenticated ? children : <OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError}/>;
};
