import React from 'react';
import { useHistory } from 'react-router-dom';
import { Security } from '@okta/okta-react';
import { OktaAuth } from '@okta/okta-auth-js';
import { oktaAuthConfig } from './oktaAuthConfig';

import { UserProvider } from './store/UserProvider'
import { SiteLayout } from './containers/layout'

import routes from './routes';


import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'react-transition-group';
import 'prismjs/themes/prism-coy.css';
import './layout/flags/flags.css';
import './layout/layout.scss';
import './App.scss';

const App = () => {

  const oktaAuth = new OktaAuth(oktaAuthConfig);

  const history = useHistory();

  const customAuthHandler = () => {
    history.push('/login');
  };

  const restoreOriginalUri = async (_oktaAuth, originalUri) => {
    console.log(originalUri);
    console.log(window.location);
    //history.replace(toRelativeUrl(originalUri, window.location.origin));
  };


  return (
    <Security oktaAuth={oktaAuth} onAuthRequired={customAuthHandler} restoreOriginalUri={restoreOriginalUri}>
      <UserProvider>
        <SiteLayout>
          {routes}
        </SiteLayout>
      </UserProvider>
    </Security>
  );
};

export default App;
