import { Amplify } from 'aws-amplify';
import cognitoConfig from './cognitoAuthConfig';
import { CookiesProvider } from 'react-cookie';

import { Login } from './Login';

import AppRoutes from './routes';
import './App.scss';
import './button-style-overrides.css';

Amplify.configure(cognitoConfig);

const App = () => {
	return (
		<CookiesProvider defaultSetOptions={{ path: '/' }}>
			<Login>
				<AppRoutes />
			</Login>
		</CookiesProvider>
	);
};

export default App;
