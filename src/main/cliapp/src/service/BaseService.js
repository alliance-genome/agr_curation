import axios from 'axios';

export class BaseService {

	api;

	constructor() {
		this.api = axios.create({
			baseURL: "/"
		});
	}

}
