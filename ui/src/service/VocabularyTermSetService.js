import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class VocabularyTermSetService extends BaseAuthService {
	saveVocabularyTermSet(updatedVocabularyTermSet) {
		return this.api.put(`/vocabularytermset`, updatedVocabularyTermSet);
	}

	async deleteVocabularyTermSet(updatedVocabularyTermSet) {
		const deletionService = new DeletionService();
		return await deletionService.delete('vocabularytermset', updatedVocabularyTermSet.id);
	}

	createVocabularyTermSet(vocabularyTermSet) {
		let newVocabularyTermSet = { ...vocabularyTermSet };

		return this.api.post(`/vocabularytermset`, newVocabularyTermSet);
	}
}
