import axios from 'axios';

export class BaseAuthService {

	api;

	constructor() {
		let oktaTokenStorage = localStorage.getItem('okta-token-storage');
		let accessToken;

		try{
			 accessToken = JSON.parse(oktaTokenStorage).accessToken;
		} catch(e){
			console.warn(e);
			accessToken = undefined;

		}

		if (accessToken) {
			this.api = axios.create({
				baseURL: "/api",
				headers: {
					Authorization: `${accessToken.tokenType} ${accessToken.accessToken}`
				}
			});
		} else {
			console.log('No accessToken');
		}
	}

}
