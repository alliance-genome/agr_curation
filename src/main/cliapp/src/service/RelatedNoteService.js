import { BaseAuthService } from './BaseAuthService';

export class RelatedNoteService extends BaseAuthService {
<<<<<<< HEAD
	deleteRelatedNote(deletedRelatedNote) {
		return this.api.put(`/`, deletedRelatedNote);
=======
	deleteRelatedNote(id) {
		return this.api.put(`/note/${id}`);
>>>>>>> alpha
	}
}
