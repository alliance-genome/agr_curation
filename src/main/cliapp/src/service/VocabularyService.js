import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class VocabularyService extends BaseAuthService {
    //eslint-disable-next-line
    constructor(authState) {
        super(authState);
    }

    saveTerm(updatedTerm) { //EDIT
        return axios.put(`api/vocabularyterm`, updatedTerm, this.apiAuthHeader);
    }

    createVocabulary(vocabulary){ //new Vocab creation
        return axios.post(`api/vocabulary`, vocabulary, this.apiAuthHeader);
    }

    createTerm(term){ //new Term creation
        return axios.post(`api/vocabularyterm`, term, this.apiAuthHeader);
    }

    getVocabularies(){ //get all dropdown list of Vocabs
        return axios.post(`api/vocabulary/find?limit=100`, {});
    }
}


