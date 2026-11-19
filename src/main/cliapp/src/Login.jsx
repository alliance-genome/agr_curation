import React, { useEffect, useState, useCallback } from 'react';
import { signInWithRedirect, getCurrentUser, fetchAuthSession } from 'aws-amplify/auth';
import { Hub } from 'aws-amplify/utils';
import { useCookies } from 'react-cookie';
import { Button } from 'primereact/button';

const CALLBACK_RETRIED_KEY = 'cognito-callback-retried';

let pendingCallbackFailure = false;
let callbackFailureHandler = null;

/**
 * Registered at module load, ahead of App's Amplify.configure(). Passes a sign-in redirect failure on
 * an OAuth callback URL to the mounted Login, or holds it until Login mounts.
 */
Hub.listen('auth', ({ payload }) => {
	if (payload.event !== 'signInWithRedirect_failure' || !window.location.search.includes('code=')) {
		return;
	}
	if (callbackFailureHandler) {
		callbackFailureHandler();
	} else {
		pendingCallbackFailure = true;
	}
});

/**
 * Removes the OAuth `code` and `state` query parameters from the current URL without reloading.
 */
const stripCallbackParams = () => {
	const url = new URL(window.location.href);
	url.searchParams.delete('code');
	url.searchParams.delete('state');
	window.history.replaceState(window.history.state, '', url.pathname + url.search + url.hash);
};

export const Login = ({ children }) => {
	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);
	const [loggingOut, setLoggingOut] = useState(false);
	const [callbackStatus, setCallbackStatus] = useState(null);
	const [, setCookie] = useCookies(['cognito-token-cookie']);

	const checkUser = useCallback(async () => {
		try {
			const currentUser = await getCurrentUser();
			const session = await fetchAuthSession();

			if (currentUser && session.tokens?.accessToken) {
				sessionStorage.removeItem(CALLBACK_RETRIED_KEY);
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

	/**
	 * Starts a new hosted-UI login. If Amplify refuses to start one, shows the retry prompt and
	 * re-checks the session so existing tokens still sign the user in.
	 */
	const restartLogin = useCallback(() => {
		signInWithRedirect().catch(() => {
			setCallbackStatus('failed');
			checkUser();
		});
	}, [checkUser]);

	/**
	 * Clears the callback parameters from the URL, then restarts login once per tab; after that
	 * attempt, shows the retry prompt instead.
	 */
	const recoverFromFailedCallback = useCallback(() => {
		stripCallbackParams();
		if (sessionStorage.getItem(CALLBACK_RETRIED_KEY)) {
			setCallbackStatus('failed');
			setLoading(false);
			return;
		}
		sessionStorage.setItem(CALLBACK_RETRIED_KEY, 'true');
		setCallbackStatus('redirecting');
		restartLogin();
	}, [restartLogin]);

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
			sessionStorage.removeItem(CALLBACK_RETRIED_KEY);
			checkUser();
		}

		return () => hubListener();
	}, [checkUser]);

	useEffect(() => {
		callbackFailureHandler = recoverFromFailedCallback;
		if (pendingCallbackFailure) {
			pendingCallbackFailure = false;
			recoverFromFailedCallback();
		}
		return () => {
			callbackFailureHandler = null;
		};
	}, [recoverFromFailedCallback]);

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

		if (!loading && !user && !isCallback && !callbackStatus) {
			signInWithRedirect();
		}
	}, [loading, user, callbackStatus]);

	if (loading) {
		return <div>Loading authentication...</div>;
	}

	if (callbackStatus === 'failed' && !user) {
		return (
			<div>
				<p>Login didn't complete.</p>
				<Button label="Try again" onClick={restartLogin} />
			</div>
		);
	}

	if (loggingOut) {
		return <div>Logging out...</div>;
	}

	if (!user) {
		return <div>Redirecting to login...</div>;
	}

	return children;
};
