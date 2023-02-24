//This is in place so that using the service methods don't trigger an error due to the lack of an accessToken
import axios from 'axios';
import {BaseAuthService} from "./BaseAuthService";

export class SearchService extends BaseAuthService {
	search() {
	}

	find() {
	}
}
