import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';

export function useControlledVocabularyService(vocabularyLabel) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();

	const termData = {
		generic_boolean_terms: {
			id: 23323,
			name: 'generic_boolean_terms',
			displayName: 'generic_boolean_terms',
			terms: [
				{
					id: 213423,
					name: true,
					text: 'true',
				},
				{
					id: 3428828,
					name: false,
					text: 'false',
				},
			],
		},
	};

	useQuery(
		['terms', vocabularyLabel],
		() => {
			return searchService.find('vocabularyterm', 20, 0, { 'vocabulary.vocabularyLabel': vocabularyLabel });
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
