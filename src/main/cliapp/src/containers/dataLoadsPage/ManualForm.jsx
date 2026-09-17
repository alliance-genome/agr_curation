import React from 'react';
import { Dropdown } from 'primereact/dropdown';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const ManualForm = ({ hideManual, newBulkLoad, onChange }) => {
	const searchService = new SearchService();

	const { data } = useQuery({
		queryKey: ['manualLoadDataProviders'],
		queryFn: () => searchService.find(Endpoints.Entity.SPECIES, 100, 0, {}),
		staleTime: Infinity,
		refetchOnWindowFocus: false,
	});

	return (
		<>
			{!hideManual.current && (
				<div className="field">
					<label htmlFor="species">Data Provider</label>
					<Dropdown
						id="species"
						value={newBulkLoad.species?.id ?? newBulkLoad.species}
						options={data?.results}
						onChange={onChange}
						placeholder={'Select Data Provider'}
						className="p-col-12"
						name="species"
						optionLabel="displayName"
						optionValue="id"
					/>
				</div>
			)}
		</>
	);
};
