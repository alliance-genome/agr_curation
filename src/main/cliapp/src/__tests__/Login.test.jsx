import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import '../tools/jest/setupTests';

const { hubListeners, mockAuth, mockSetCookie } = vi.hoisted(() => ({
	hubListeners: [],
	mockSetCookie: vi.fn(),
	mockAuth: {
		signInWithRedirect: vi.fn(),
		getCurrentUser: vi.fn(),
		fetchAuthSession: vi.fn(),
	},
}));

vi.mock('aws-amplify/auth', () => mockAuth);

vi.mock('aws-amplify/utils', () => ({
	Hub: {
		listen: (channel, listener) => {
			hubListeners.push(listener);
			return () => {
				const index = hubListeners.indexOf(listener);
				if (index !== -1) hubListeners.splice(index, 1);
			};
		},
	},
}));

vi.mock('react-cookie', () => ({
	useCookies: () => [{}, mockSetCookie],
}));

const CALLBACK_RETRIED_KEY = 'cognito-callback-retried';

const dispatchAuthEvent = (event) => {
	act(() => {
		[...hubListeners].forEach((listener) => listener({ payload: { event } }));
	});
};

const settle = () =>
	act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});

const signedInSession = {
	tokens: {
		accessToken: { toString: () => 'access-token', payload: {} },
	},
};

const mockSignedInUser = () => {
	mockAuth.getCurrentUser.mockResolvedValue({ username: 'curator' });
	mockAuth.fetchAuthSession.mockResolvedValue(signedInSession);
};

const importLogin = async () => (await import('../Login')).Login;

const renderLogin = async () => {
	const Login = await importLogin();
	return render(
		<Login>
			<div>App content</div>
		</Login>
	);
};

describe('Login', () => {
	beforeEach(() => {
		vi.resetModules();
		hubListeners.length = 0;
		vi.clearAllMocks();
		sessionStorage.clear();
		window.history.replaceState(null, '', '/');
		mockAuth.signInWithRedirect.mockResolvedValue(undefined);
		mockAuth.getCurrentUser.mockRejectedValue(new Error('No current user'));
		mockAuth.fetchAuthSession.mockResolvedValue({});
	});

	describe('without an OAuth callback in the URL', () => {
		it('redirects to the hosted login when there is no user', async () => {
			await renderLogin();

			await waitFor(() => expect(mockAuth.signInWithRedirect).toHaveBeenCalledTimes(1));
		});

		it('renders the app when a user is signed in', async () => {
			mockSignedInUser();

			await renderLogin();

			expect(await screen.findByText('App content')).toBeInTheDocument();
			expect(mockAuth.signInWithRedirect).not.toHaveBeenCalled();
		});

		it('clears a retry marker left by an earlier callback', async () => {
			sessionStorage.setItem(CALLBACK_RETRIED_KEY, 'true');

			await renderLogin();

			expect(sessionStorage.getItem(CALLBACK_RETRIED_KEY)).toBeNull();
		});

		it('ignores a redirect failure reported outside a callback', async () => {
			mockSignedInUser();
			await renderLogin();
			await screen.findByText('App content');

			dispatchAuthEvent('signInWithRedirect_failure');

			expect(screen.getByText('App content')).toBeInTheDocument();
			expect(mockAuth.signInWithRedirect).not.toHaveBeenCalled();
			expect(sessionStorage.getItem(CALLBACK_RETRIED_KEY)).toBeNull();
		});
	});

	describe('on an OAuth callback', () => {
		beforeEach(() => {
			window.history.replaceState(null, '', '/?code=abc&state=xyz');
		});

		it('waits for Amplify while the code exchange is pending', async () => {
			await renderLogin();
			await settle();

			expect(screen.getByText('Loading authentication...')).toBeInTheDocument();
			expect(mockAuth.getCurrentUser).not.toHaveBeenCalled();
			expect(mockAuth.signInWithRedirect).not.toHaveBeenCalled();
		});

		it('renders the app and clears the retry marker when sign-in completes', async () => {
			sessionStorage.setItem(CALLBACK_RETRIED_KEY, 'true');
			await renderLogin();
			mockSignedInUser();

			dispatchAuthEvent('signedIn');

			expect(await screen.findByText('App content')).toBeInTheDocument();
			expect(sessionStorage.getItem(CALLBACK_RETRIED_KEY)).toBeNull();
		});

		it('strips the callback parameters and restarts login once when the exchange fails', async () => {
			window.history.replaceState(null, '', '/?code=abc&state=xyz&other=1#section');
			await renderLogin();

			dispatchAuthEvent('signInWithRedirect_failure');
			await settle();

			expect(mockAuth.signInWithRedirect).toHaveBeenCalledTimes(1);
			expect(mockAuth.getCurrentUser).not.toHaveBeenCalled();
			expect(window.location.search).toBe('?other=1');
			expect(window.location.hash).toBe('#section');
			expect(sessionStorage.getItem(CALLBACK_RETRIED_KEY)).toBe('true');
			expect(screen.getByText('Loading authentication...')).toBeInTheDocument();
		});

		it('shows the retry prompt when Amplify refuses to restart login', async () => {
			mockAuth.signInWithRedirect.mockRejectedValue(new Error('Unable to start login'));
			await renderLogin();

			dispatchAuthEvent('signInWithRedirect_failure');

			expect(await screen.findByText("Login didn't complete.")).toBeInTheDocument();
		});

		it('renders the app when restarting login is refused because tokens already exist', async () => {
			mockAuth.signInWithRedirect.mockRejectedValue(new Error('There is already a signed in user.'));
			await renderLogin();
			mockSignedInUser();

			dispatchAuthEvent('signInWithRedirect_failure');

			expect(await screen.findByText('App content')).toBeInTheDocument();
		});

		it('handles a failure Amplify reports before Login mounts', async () => {
			const Login = await importLogin();
			dispatchAuthEvent('signInWithRedirect_failure');
			expect(mockAuth.signInWithRedirect).not.toHaveBeenCalled();

			render(
				<Login>
					<div>App content</div>
				</Login>
			);
			await settle();

			expect(mockAuth.signInWithRedirect).toHaveBeenCalledTimes(1);
			expect(mockAuth.getCurrentUser).not.toHaveBeenCalled();
			expect(window.location.search).toBe('');
		});

		it('shows a retry prompt instead of redirecting again after a second failure', async () => {
			sessionStorage.setItem(CALLBACK_RETRIED_KEY, 'true');
			await renderLogin();

			dispatchAuthEvent('signInWithRedirect_failure');
			await settle();

			expect(screen.getByText("Login didn't complete.")).toBeInTheDocument();
			expect(window.location.search).toBe('');
			expect(mockAuth.signInWithRedirect).not.toHaveBeenCalled();

			fireEvent.click(screen.getByRole('button', { name: 'Try again' }));
			await settle();

			expect(mockAuth.signInWithRedirect).toHaveBeenCalledTimes(1);
			expect(sessionStorage.getItem(CALLBACK_RETRIED_KEY)).toBe('true');
		});
	});
});
