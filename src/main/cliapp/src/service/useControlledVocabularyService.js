import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';
import { Endpoints } from '../constants/Endpoints';

export function useControlledVocabularyService(vocabularyLabel) {
	const [terms, setTerms] = useState();
	const searchService = new SearchService();

	const { data, isSuccess } = useQuery({
		queryKey: ['terms', vocabularyLabel],
		queryFn: () => {
			return searchService.find(Endpoints.Vocabulary.TERM, 20, 0, { 'vocabulary.vocabularyLabel': vocabularyLabel });
		},
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data) {
			setTerms(data.results);
		}
	}, [data, isSuccess]);

	return terms;
}
