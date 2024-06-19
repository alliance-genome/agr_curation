import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';

export function useVocabularyTermSetService(vocabularyLabel) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();
	const termData = {};

	useQuery(
		['terms', vocabularyLabel],
		() => {
			return searchService.find('vocabularyterm', 15, 0, { 'vocabularyTermSets.vocabularyLabel': vocabularyLabel });
		},
		{
			onSuccess: (data) => {
				if (data.results) {
					setTerms(data.results.sort((a, b) => (a.name > b.name ? 1 : -1)));
				} else {
					if (termData[vocabularyLabel]) {
						setTerms(termData[vocabularyLabel]['terms']);
					}
				}
			},
			refetchOnWindowFocus: false,
		}
	);
	return terms;
}
