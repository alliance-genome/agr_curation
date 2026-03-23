import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class VocabularyTermSetService extends BaseAuthService {
	saveVocabularyTermSet(updatedVocabularyTermSet) {
		return this.api.put(`/vocabularytermset`, updatedVocabularyTermSet);
	}

	async deleteVocabularyTermSet(updatedVocabularyTermSet) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Vocabulary.TERM_SET, updatedVocabularyTermSet.id);
	}

	createVocabularyTermSet(vocabularyTermSet) {
		let newVocabularyTermSet = { ...vocabularyTermSet };

		return this.api.post(`/vocabularytermset`, newVocabularyTermSet);
	}
}
