import React, { useState, useEffect, useRef } from 'react';
import { Route, useHistory, Switch } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import { Security, SecureRoute, LoginCallback, useOktaAuth } from '@okta/okta-react';
import { OktaAuth, toRelativeUrl } from '@okta/okta-auth-js';
import { oktaAuthConfig, oktaSignInConfig } from './oktaAuthConfig';

import classNames from 'classnames';


import { Login } from './Login';

import routes from './routes';




import PrimeReact from 'primereact/api';

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

    //const { oktaAuth, authState } = useOktaAuth();

    const history = useHistory();

    const customAuthHandler = () => {
      history.push('/login');
    };

    const restoreOriginalUri = async (_oktaAuth, originalUri) => {
      console.log(originalUri);
      console.log(window.location);
      //    history.replace(toRelativeUrl(originalUri, window.location.origin));
    };

    return (
      <>
        { console.log(oktaAuth.isAuthenticated()) }
        <Security oktaAuth={oktaAuth} onAuthRequired={customAuthHandler} restoreOriginalUri={restoreOriginalUri}> 
            { routes }
            <Route path='/login' render={() => <Login config={oktaSignInConfig} />} />
        </Security>
      </>

    );

};

export default App;
