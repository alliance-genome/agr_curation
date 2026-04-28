import '@testing-library/jest-dom';
import { server } from './server.js';

if (typeof structuredClone === 'undefined') {
	global.structuredClone = (val) => JSON.parse(JSON.stringify(val));
}

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

Object.defineProperty(global, 'localStorage', { value: localStorageMock });
Object.defineProperty(global.window, 'crypto', {
	value: {
		randomUUID: vi.fn().mockReturnValue('mock-uuid-1234'),
	},
});

export const setLocalStorage = (id, data) => {
	window.localStorage.setItem(id, JSON.stringify(data));
};

const mockOktaTokenStorage = {
	accessToken: {
		tokenType: 'test type',
		accessToken: 'test access token',
	},
};
let mockConsoleError;
let mockConsoleWarn;
// Establish API and local storage mocking before all tests.
beforeAll(() => {
	server.listen({
		onUnhandledRequest: 'bypass',
	});
	window.localStorage.clear();
	setLocalStorage('okta-token-storage', mockOktaTokenStorage);
});

beforeEach(() => {
	mockConsoleError = vi.spyOn(console, 'error');
	mockConsoleWarn = vi.spyOn(console, 'warn');
	mockConsoleError.mockImplementation(() => {});
	mockConsoleWarn.mockImplementation(() => {});
});

// Reset any request handlers that we may add during the tests,
// so they don't affect other tests.
afterEach(() => {
	mockConsoleError.mockRestore();
	mockConsoleWarn.mockRestore();
	server.resetHandlers();
});

// Clean up after the tests are finished.
afterAll(() => server.close());
