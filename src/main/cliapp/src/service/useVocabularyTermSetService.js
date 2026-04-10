import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';
import { Endpoints } from '../constants/Endpoints';

export function useVocabularyTermSetService(vocabularyLabel) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();

	const { data, isSuccess } = useQuery({
		queryKey: ['terms', vocabularyLabel],
		queryFn: () => {
			return searchService.find(Endpoints.Vocabulary.TERM, 30, 0, {
				'vocabularyTermSets.vocabularyLabel': vocabularyLabel,
			});
		},
		placeholderData: (previousData) => previousData,

		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (isSuccess && data) {
			setTerms(data.results);
		}
	}, [data, isSuccess]);

	return terms;
}
