import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';

export function useGenomeAssemblyService() {
	const [genomeAssemblies, setGenomeAssemblies] = useState([]);
	const searchService = new SearchService();

	const { data, isSuccess } = useQuery({
		queryKey: ['genomeAssemblies'],
		queryFn: () => searchService.find('genomeassembly', 100, 0, {}),
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data) {
			const assemblies = (data.results || []).map((assembly) => ({
				...assembly,
				name: assembly.primaryExternalId || assembly.curie,
			}));
			setGenomeAssemblies(assemblies);
		}
	}, [data, isSuccess]);

	return genomeAssemblies;
}
