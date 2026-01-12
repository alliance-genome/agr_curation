import axios from 'axios';

export class BaseAuthService {
	api;

	constructor() {
		let cognitoToken;

		try {
			cognitoToken = JSON.parse(localStorage.getItem('cognito-token-storage'));
		} catch (e) {
			console.warn(e);
			cognitoToken = undefined;
		}

		if (cognitoToken && cognitoToken.accessToken && cognitoToken.idToken) {
			this.api = axios.create({
				baseURL: '/api',
				headers: {
					Authorization: `${cognitoToken.accessToken.tokenType} ${cognitoToken.accessToken.accessToken}`,
					SiteIdentity: `${cognitoToken.idToken.idToken}`,
				},
			});
		} else {
			console.log('No accessToken');
		}
	}
}
