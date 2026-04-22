import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from './SearchService';

export function useOrganizationService() {
	const [organizations, setOrganizations] = useState([]);
	const searchService = new SearchService();

	const { data, isSuccess } = useQuery({
		queryKey: ['organizations'],
		queryFn: () => searchService.find('organization', 100, 0, {}),
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data) {
			const orgs = (data.results || []).map((org) => ({
				...org,
				name: org.abbreviation,
			}));
			setOrganizations(orgs);
		}
	}, [data, isSuccess]);

	return organizations;
}
