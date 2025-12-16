import axios from 'axios';

export class BaseAuthService {
	api;

	constructor() {
		let cognitoTokenStorage = localStorage.getItem('cognito-token-storage');
		let accessToken;

		try {
			accessToken = JSON.parse(cognitoTokenStorage).accessToken;
		} catch (e) {
			console.warn(e);
			accessToken = undefined;
		}

		if (accessToken) {
			this.api = axios.create({
				baseURL: '/api',
				headers: {
					Authorization: `${accessToken.tokenType} ${accessToken.accessToken}`,
				},
			});
		} else {
			console.log('No accessToken');
		}
	}
}
