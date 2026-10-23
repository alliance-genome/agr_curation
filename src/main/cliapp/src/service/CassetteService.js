import { BaseAuthService } from './BaseAuthService';

export class CassetteService extends BaseAuthService {
	saveCassette(updatedCassette) {
		return this.api.put(`/cassette`, updatedCassette);
	}
}
