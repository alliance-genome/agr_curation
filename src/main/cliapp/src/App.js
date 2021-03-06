import React from 'react';
import { useHistory } from 'react-router-dom';
import { Security } from '@okta/okta-react';
import { OktaAuth } from '@okta/okta-auth-js';
import { oktaAuthConfig } from './oktaAuthConfig';

import { SiteLayout } from './containers/layout'

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
				<SiteLayout>
					{routes}
				</SiteLayout>
		</Security>
	);
};

export default App;
