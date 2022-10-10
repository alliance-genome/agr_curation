import React, { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
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



	//console.log(oktaSignInConfig);

	//return children;
	//return <OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError}/>;

	return authState?.isAuthenticated ? children : <OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError}/>;
};
