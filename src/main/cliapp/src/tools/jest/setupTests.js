import '@testing-library/jest-dom';
import { server } from './server.js'

const localStorageMock = (function () {
	let store = {};

	return {
		getItem(key) {
			return store[key] || null;
		},

		setItem(key, value) {
			store[key] = value;
		},

		clear() {
			store = {};
		},

		removeItem(key) {
			delete store[key];
		},

		getAll() {
			return store;
		},
	};
})();

Object.defineProperty(global, "localStorage", { value: localStorageMock });

export const setLocalStorage = (id, data) => {
	window.localStorage.setItem(id, JSON.stringify(data));
};

const mockOktaTokenStorage = {
	accessToken: {
		tokenType: "test type",
		accessToken: "test access token",
	}
}
// Establish API and local storage mocking before all tests.
beforeAll(() => {
	server.listen({
		onUnhandledRequest: 'warn',
	})
	window.localStorage.clear();
	setLocalStorage('okta-token-storage', mockOktaTokenStorage);
})

// Reset any request handlers that we may add during the tests,
// so they don't affect other tests.
afterEach(() => server.resetHandlers())

// Clean up after the tests are finished.
afterAll(() => server.close())
