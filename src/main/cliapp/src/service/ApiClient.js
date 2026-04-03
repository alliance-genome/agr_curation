// Lightweight fetch wrapper that provides an axios-compatible API.
// Replaces axios to eliminate the dependency while keeping the same
// method signatures and response/error shapes that all services expect.

class ApiClient {
	constructor({ baseURL = '', headers = {} } = {}) {
		this.baseURL = baseURL;
		this.defaultHeaders = headers;
	}

	async request(url, { method = 'GET', data, headers = {}, responseType } = {}) {
		const fullURL = this.baseURL + url;
		const isFormData = data instanceof FormData;

		const config = {
			method,
			headers: {
				...this.defaultHeaders,
				// Only set Content-Type for JSON bodies; FormData sets its own boundary
				...(!isFormData && data ? { 'Content-Type': 'application/json' } : {}),
				...headers,
			},
		};

		if (data) {
			config.body = isFormData ? data : JSON.stringify(data);
		}

		const res = await fetch(fullURL, config);

		// Build an axios-compatible response object
		const response = {
			status: res.status,
			statusText: res.statusText,
			headers: Object.fromEntries(res.headers.entries()),
		};

		if (responseType === 'blob') {
			response.data = await res.blob();
		} else {
			const text = await res.text();
			try {
				response.data = JSON.parse(text);
			} catch {
				response.data = text;
			}
		}

		// axios throws on non-2xx; replicate that with error.response
		if (!res.ok) {
			const error = new Error(`Request failed with status ${res.status}`);
			error.response = response;
			throw error;
		}

		return response;
	}

	get(url, config) {
		return this.request(url, { method: 'GET', ...config });
	}

	post(url, data, config) {
		return this.request(url, { method: 'POST', data, ...config });
	}

	put(url, data, config) {
		return this.request(url, { method: 'PUT', data, ...config });
	}

	delete(url, config) {
		return this.request(url, { method: 'DELETE', ...config });
	}
}

export function createApiClient(options) {
	return new ApiClient(options);
}
