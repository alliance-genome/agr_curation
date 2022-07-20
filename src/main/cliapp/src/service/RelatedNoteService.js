import { BaseAuthService } from './BaseAuthService';

export class RelatedNoteService extends BaseAuthService {
	deleteRelatedNote(id) {
		return this.api.put(`/note/${id}`);
	}
}
