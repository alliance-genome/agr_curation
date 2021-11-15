
export class ControlledVocabularyService {
    terms = {
        "disease_relation_terms": {
            "id": 12355,
            "name": "disease_relation_terms",
            "displayName": "Disease Relation Terms",
            "terms": [
                {
                    "id": 12355,
                    "name": "is_model_of",
                    "definition": "Is model for thing",
                    "displayOrder": 1
                }, 
                {
                    "id": 123412,
                    "name": "is_marker_for",
                    "displayOrder": 2
                },
                {
                    "id": 123517,
                    "name": "is_implicated_in",
                    "displayOrder": 3
                }
            ]
        },
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




