import { useNavigate } from 'react-router-dom';
import { Security } from '@okta/okta-react';
import { OktaAuth } from '@okta/okta-auth-js';
import { oktaAuthConfig } from './oktaAuthConfig';
import { CookiesProvider } from 'react-cookie';

import { Login } from './Login';

import AppRoutes from './routes';
import './App.scss';
import './button-style-overrides.css';

const App = () => {
	const oktaAuth = new OktaAuth(oktaAuthConfig);

	const navigate = useNavigate();

	const customAuthHandler = () => {
		navigate('/login');
	};

	const restoreOriginalUri = async (_oktaAuth, originalUri) => {
		// console.log(originalUri);
		// console.log(window.location);
		//history.replace(toRelativeUrl(originalUri, window.location.origin));
	};

	return (
		<Security oktaAuth={oktaAuth} onAuthRequired={customAuthHandler} restoreOriginalUri={restoreOriginalUri}>
			<CookiesProvider defaultSetOptions={{ path: '/' }}>
				<Login>
					<AppRoutes />
				</Login>
			</CookiesProvider>
		</Security>
	);
};

export default App;
