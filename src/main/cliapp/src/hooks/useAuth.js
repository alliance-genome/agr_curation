import { useState, useEffect } from 'react';
import { fetchAuthSession } from 'aws-amplify/auth';

// TODO: Consider consolidating auth state into a React context provider
// to avoid duplicating auth check logic across components (see SiteLayout.js, RequiredAuth.js)

/**
 * Custom hook for authentication state
 * Returns authState object compatible with existing code
 */
export const useAuth = () => {
	const [authState, setAuthState] = useState({ isAuthenticated: false });

	useEffect(() => {
		const checkAuth = async () => {
			try {
				const session = await fetchAuthSession();
				if (session.tokens?.accessToken) {
					setAuthState({ isAuthenticated: true });
				} else {
					setAuthState({ isAuthenticated: false });
				}
			} catch (error) {
				console.error('Auth check failed:', error);
				setAuthState({ isAuthenticated: false });
			}
		};
		checkAuth();
	}, []);

	return { authState };
};
