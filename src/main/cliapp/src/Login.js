import React, { useEffect, useState, useCallback } from 'react';
import { signInWithRedirect, getCurrentUser, fetchAuthSession } from 'aws-amplify/auth';
import { Hub } from 'aws-amplify/utils';
import { useCookies } from 'react-cookie';

export const Login = ({ children }) => {
	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);
	const [loggingOut, setLoggingOut] = useState(false);
	const [, setCookie] = useCookies(['cognito-token-cookie']);

	const checkUser = useCallback(async () => {
		try {
			const currentUser = await getCurrentUser();
			const session = await fetchAuthSession();

			if (currentUser && session.tokens?.accessToken) {
				setUser(currentUser);
				setCookie('cognito-token-cookie', session.tokens.accessToken.toString(), {
					maxAge: 60 * 60 * 12,
					path: '/',
				});

				const cognitoTokenStorage = {
					accessToken: {
						accessToken: session.tokens.accessToken.toString(),
						tokenType: 'Bearer',
						payload: session.tokens.accessToken.payload,
					},
					idToken: session.tokens.idToken
						? {
								idToken: session.tokens.idToken.toString(),
								payload: session.tokens.idToken.payload,
							}
						: null,
				};
				localStorage.setItem('cognito-token-storage', JSON.stringify(cognitoTokenStorage));
			} else {
				setUser(null);
			}
		} catch (error) {
			setUser(null);
		} finally {
			setLoading(false);
		}
	}, [setCookie]);

	useEffect(() => {
		const isCallback = window.location.search.includes('code=');

		const hubListener = Hub.listen('auth', ({ payload }) => {
			switch (payload.event) {
				case 'signedIn':
				case 'tokenRefresh':
					checkUser();
					break;
				case 'signedOut':
					setUser(null);
					localStorage.removeItem('cognito-token-storage');
					break;
				default:
					break;
			}
		});

		if (!isCallback) {
			checkUser();
		}

		return () => hubListener();
	}, [checkUser]);

	useEffect(() => {
		const isCallback = window.location.search.includes('code=');

		const justLoggedOut = sessionStorage.getItem('cognito-just-logged-out');
		if (justLoggedOut) {
			sessionStorage.removeItem('cognito-just-logged-out');
			setLoggingOut(true);
			setTimeout(() => {
				setLoggingOut(false);
				signInWithRedirect();
			}, 1500);
			return;
		}

		if (!loading && !user && !isCallback) {
			signInWithRedirect();
		}
	}, [loading, user]);

	if (loading) {
		return <div>Loading authentication...</div>;
	}

	if (loggingOut) {
		return <div>Logging out...</div>;
	}

	if (!user) {
		return <div>Redirecting to login...</div>;
	}

	return children;
};
