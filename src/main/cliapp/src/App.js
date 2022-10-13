import React from 'react';
import { useHistory } from 'react-router-dom';
import { Security } from '@okta/okta-react';
import { OktaAuth } from '@okta/okta-auth-js';
import { oktaAuthConfig } from './oktaAuthConfig';

import { Login } from './Login';

import routes from './routes';
import './App.scss';

const App = () => {

	const oktaAuth = new OktaAuth(oktaAuthConfig);

	const history = useHistory();

	const customAuthHandler = () => {
		history.push('/login');
	};

	const restoreOriginalUri = async (_oktaAuth, originalUri) => {
		// console.log(originalUri);
		// console.log(window.location);
		//history.replace(toRelativeUrl(originalUri, window.location.origin));
	};

	return (
		<Security oktaAuth={oktaAuth} onAuthRequired={customAuthHandler} restoreOriginalUri={restoreOriginalUri}>
			<Login>
				{routes}
			</Login>
		</Security>
	);
};

export default App;
