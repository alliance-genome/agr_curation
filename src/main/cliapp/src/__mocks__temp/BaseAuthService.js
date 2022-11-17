//This is in place so that using the service methods don't trigger an error due to the lack of an accessToken
import axios from 'axios';

export class BaseAuthService {
	api;
	constructor() {
		this.api = axios.create({
			baseURL: "/api",
		});
	}
}
