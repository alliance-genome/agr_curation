import { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { useOktaAuth } from '@okta/okta-react';
import { toRelativeUrl } from '@okta/okta-auth-js';

const Loading = () => <div>Loading authentication...</div>;

export const RequiredAuth = () => {
	const { oktaAuth, authState } = useOktaAuth();

	useEffect(() => {
		if (!authState) return;

		if (!authState.isAuthenticated) {
			const originalUri = toRelativeUrl(window.location.href, window.location.origin);
			oktaAuth.setOriginalUri(originalUri);
			oktaAuth.signInWithRedirect();
		}
	}, [oktaAuth, authState]);

	if (!authState || !authState?.isAuthenticated) {
		return <Loading />;
	}

	return <Outlet />;
};
