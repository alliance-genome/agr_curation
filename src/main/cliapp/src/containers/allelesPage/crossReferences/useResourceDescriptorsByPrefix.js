import { useQuery } from '@tanstack/react-query';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';

// Comfortably more descriptors than the registry holds, so every one arrives in the single request.
const DESCRIPTOR_LIMIT = 1000;

const NO_DESCRIPTORS = new Map();

/**
 * Every resource descriptor that is not obsolete, keyed by its exact prefix, each carrying its
 * `resourcePages`. They are read once and shared, so a curie's descriptor can be found as soon as the
 * curie is typed, without waiting on a request.
 *
 * @returns {Map<string, Object>} descriptors by prefix; empty until they have been read
 */
export const useResourceDescriptorsByPrefix = () => {
	const { data } = useQuery({
		queryKey: ['resourceDescriptorsByPrefix'],
		queryFn: async () => {
			const response = await new SearchService().search(Endpoints.Resource.DESCRIPTOR, DESCRIPTOR_LIMIT, 0, [], {
				obsoleteFilter: { obsolete: { queryString: false } },
			});
			if (response?.totalResults > DESCRIPTOR_LIMIT) {
				console.warn(
					`Read ${DESCRIPTOR_LIMIT} of ${response.totalResults} resource descriptors; the rest cannot be filled in`
				);
			}
			return new Map((response?.results ?? []).map((descriptor) => [descriptor.prefix, descriptor]));
		},
		staleTime: Infinity,
		refetchOnWindowFocus: false,
	});

	return data ?? NO_DESCRIPTORS;
};
