//import axios from 'axios';

export class ControlledVocabularyService {
    terms = {
        "generic_boolean_terms": {
            "id": 23323,
            "name": "generic_boolean_terms",
            "displayName": "generic_boolean_terms",
            "terms": [
                {
                    "id": 213423,
                    "name": "true"
                }, {
                    id: 3428828,
                    "name": "false"
                }
            ]
        }
    }

    getTerms(termType) {
        return this.terms[termType].terms;
    }

}




