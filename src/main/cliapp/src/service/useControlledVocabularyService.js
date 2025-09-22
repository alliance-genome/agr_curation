import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';

const TERM_DATA = {
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

export function useControlledVocabularyService(vocabularyLabel) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();

	const { data, isSuccess } = useQuery({
		queryKey: ['terms', vocabularyLabel],
		queryFn: () => {
			return searchService.find('vocabularyterm', 20, 0, { 'vocabulary.vocabularyLabel': vocabularyLabel });
		},
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data) {
			if (vocabularyLabel === 'generic_boolean_terms') {
				setTerms(TERM_DATA[vocabularyLabel]);
			} else {
				setTerms(data.data);
			}
		}
	}, [data, isSuccess, vocabularyLabel]);

	return terms;
}
