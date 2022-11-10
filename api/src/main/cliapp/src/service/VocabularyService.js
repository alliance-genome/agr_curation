import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class VocabularyService extends BaseAuthService {
		saveTerm(updatedTerm) { //EDIT
				return this.api.put(`/vocabularyterm`, updatedTerm);
		}

		createVocabulary(vocabulary){ //new Vocab creation
				return this.api.post(`/vocabulary`, vocabulary);
		}

		createTerm(term){ //new Term creation
				return this.api.post(`/vocabularyterm`, term);
		}

		getVocabularies(){ //get all dropdown list of Vocabs
				return this.api.post(`/vocabulary/find?limit=100`, {});
		}

		async deleteTerm(term) { 
			const deletionService = new DeletionService();
			return await deletionService.delete(`vocabularyterm`, term.id);
		}
}


