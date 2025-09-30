import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';

export const useGetTableData = ({
	tableState,
	endpoint,
	sortMapping,
	nonNullFieldsTable,
	setIsInEditMode,
	setEntities,
	setTotalRecords,
	toast_topleft,
	searchService,
}) => {
	const { data, isSuccess, isError, isPending } = useQuery({
		queryKey: [tableState.tableKeyName, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters],

		queryFn: () =>
			searchService.search(
				endpoint,
				tableState.rows,
				tableState.page,
				tableState.multiSortMeta,
				tableState.filters,
				sortMapping,
				[],
				nonNullFieldsTable
			),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
		enabled: !!tableState.rows,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (isError && data) {
			let message = '';
			if (data.statusText) {
				if (data.statusText.includes('NOT FOUND')) {
					message = 'Record not found';
				} else {
					message = data.statusText;
				}
			} else {
				message = 'Page error';
			}

			if (toast_topleft && toast_topleft.current) {
				toast_topleft.current.show({
					severity: 'error',
					summary: message,
					life: 7000,
				});
			}
		}

		if (isSuccess && data) {
			setIsInEditMode(false);
			setEntities(data.results);
			setTotalRecords(data.totalResults);
		}
	}, [data, setIsInEditMode, setEntities, setTotalRecords, toast_topleft, isError, isSuccess]);

	return { data, isSuccess, isError, isPending };
};
