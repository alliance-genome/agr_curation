import { BaseAuthService } from './BaseAuthService';

export class DataSubmissionService extends BaseAuthService {

	sendFile(formData) {
		return this.api.post('/data/submit', formData).then(res => res.data);
	}

}
