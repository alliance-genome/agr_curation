import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';
import { Endpoints } from '../constants/Endpoints';
import { setSpeciesTaxaCache } from '../constants/speciesTaxa';

// Loads the Species table once and populates the module-level speciesTaxa cache
// that the disease-annotation autocomplete species narrowing reads (SCRUM-6220).
// Mount once on a page that hosts those autocompletes (DiseaseAnnotationsPage).
export function useSpeciesTaxa() {
	const searchService = new SearchService();
	const { data, isSuccess } = useQuery({
		queryKey: ['speciesTaxa'],
		// /species/find is relational (no OpenSearch index dependency); {} returns all.
		queryFn: () => searchService.find(Endpoints.Entity.SPECIES, 100, 0, {}),
		staleTime: Infinity, // species rarely change within a session
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data?.results) {
			setSpeciesTaxaCache(data.results);
		}
	}, [isSuccess, data]);
}
