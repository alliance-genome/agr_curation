import axios from 'axios';

export class BaseAuthService {
	api;

	constructor() {
		let cognitoTokenStorage = localStorage.getItem('cognito-token-storage');
		let accessToken;

		try {
			accessToken = JSON.parse(cognitoTokenStorage).accessToken;
		} catch (e) {
			accessToken = undefined;
		}

		if (accessToken) {
			const authHeader = `${accessToken.tokenType} ${accessToken.accessToken}`;
			this.api = axios.create({
				baseURL: '/api',
				headers: {
					Authorization: authHeader,
				},
			});
		} else {
			this.api = axios.create({
				baseURL: '/api',
			});
		}
	}
}
