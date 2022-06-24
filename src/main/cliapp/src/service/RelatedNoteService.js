import { BaseAuthService } from './BaseAuthService';

export class RelatedNoteService extends BaseAuthService {
	deleteRelatedNote(deletedRelatedNote) {
		return this.api.put(`/`, deletedRelatedNote);
	}
}
