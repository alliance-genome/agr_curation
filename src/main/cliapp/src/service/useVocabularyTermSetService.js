import { useState } from 'react';
import { useQuery } from 'react-query';
import { SearchService } from './SearchService';

export function useVocabularyTermSetService(setName) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();
	const termData = {};

	useQuery(['terms', setName],
		() => {
			return searchService.find("vocabularyterm", 15, 0, {"vocabularyTermSets.name" : setName } )
		}, {
			onSuccess: (data) => {
				if (data.results) {
					setTerms(data.results.sort((a, b) => (a.name > b.name) ? 1 : -1));
				} else {
					if (termData[setName]) {
						setTerms(termData[setName]['terms']);
					}
				}
			},
			refetchOnWindowFocus: false
		}
	);
	return terms;
}






