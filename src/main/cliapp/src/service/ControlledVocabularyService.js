import axios from 'axios';

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

    getVocabTerms(limit, page, sorts, filters) {
      var sortOptions = {};

      var sortArray = {};
      if(sorts) {
        sorts.forEach((o) => {
          sortArray[o.field] = o.order;
        });
      }

      var filterArray = {};
      if(filters) {
        Object.keys(filters).forEach((key) => {
          filterArray[key] = filters[key]["value"];
        });
      }

      if(Object.keys(filterArray).length > 0) {
        sortOptions["searchFilters"] = filterArray;
      }
      if(Object.keys(sortArray).length > 0) {
        sortOptions["sortOrders"] = sortArray;
      }

      return axios.post('/api/vocabularyterm/search?limit=' + limit + '&page=' + page, sortOptions).then(res => res.data);
    }

}




