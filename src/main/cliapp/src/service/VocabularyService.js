import { BaseAuthService } from './BaseAuthService';

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
}


