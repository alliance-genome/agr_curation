import React, { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import OktaSignInWidget from './OktaSignInWidget';
import { useOktaAuth } from '@okta/okta-react';
import { oktaSignInConfig } from './oktaAuthConfig';
import { LoggedInPersonService } from './service/LoggedInPersonService';

export const Login = ({ children }) => {
	const { oktaAuth, authState } = useOktaAuth();

	let [loggedInPersonService, setLoggedInPersonService] = useState();

	const onSuccess = (tokens) => {
		oktaAuth.handleLoginRedirect(tokens);
	};

	const onError = (err) => {
		console.log('error logging in', err);
	};

	useQuery(['userInfo'],
		() => loggedInPersonService.getUserInfo(), {
			onSuccess: (data) => {
				console.log("User Info");
				console.log(data);
				//setApiVersion(data);
			},
			onError: (error) => {
				console.log(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
			enabled: !!(authState?.isAuthenticated && loggedInPersonService),
		}
	);

	useEffect(() => {
		if(authState?.isAuthenticated) {
			console.log("Setting setLoggedInPersonService");
			setLoggedInPersonService(new LoggedInPersonService())
		}
	}, [authState]);

	//console.log(oktaSignInConfig);

	//return children;
	//return <OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError}/>;

	return authState?.isAuthenticated ? children : <OktaSignInWidget config={oktaSignInConfig} onSuccess={onSuccess} onError={onError}/>;
};
