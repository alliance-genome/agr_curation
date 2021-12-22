import {useState} from 'react'
import { useQuery } from 'react-query';
import { SearchService } from './SearchService';

export function useControlledVocabularyService(termType) {
    const searchService = new SearchService();

    const [terms, setTerms] = useState();

    const termData = {
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


    useQuery(['terms', termType],
        () => searchService.search("vocabularyterm", 15, 0, null, { "vocabFilter": { "vocabulary.name": termType } }), {
        onSuccess: (data) => {
            if (data.results) {
                setTerms(data.results);
            } else {
                if(termData[termType]){
                    setTerms(termData[termType]['terms']);
                }
            }
        },
        refetchOnWindowFocus: false
    }
    )
    return terms;
}






