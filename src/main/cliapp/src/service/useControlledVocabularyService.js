import { useState } from 'react';
import { useQuery } from 'react-query';
import { SearchService } from './SearchService';

export function useControlledVocabularyService(accessToken, termType) {
  const [terms, setTerms] = useState();
  const searchService = new SearchService(accessToken);

  const termData = {
    "generic_boolean_terms": {
      "id": 23323,
      "name": "generic_boolean_terms",
      "displayName": "generic_boolean_terms",
      "terms": [
        {
          id: 213423,
          name: true,
          text: "true"
        }, {
          id: 3428828,
          name: false,
          text: "false"
        }
      ]
    }
  };


  useQuery(['terms', termType],
    () => {
      return searchService.find("vocabularyterm", 15, 0, {"vocabulary.name" : termType } )
    }, {
      onSuccess: (data) => {
        if (data.results) {
          setTerms(data.results);
        } else {
          if (termData[termType]) {
            setTerms(termData[termType]['terms']);
          }
        }
      },
      refetchOnWindowFocus: false
    }
  );
  return terms;
}






