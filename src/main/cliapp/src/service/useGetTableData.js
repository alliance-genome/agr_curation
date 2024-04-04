import { useQuery } from 'react-query';

export const useGetTableData = ({
  tableState,
  endpoint,
  sortMapping,
  nonNullFieldsTable,
  setIsInEditMode,
  setEntities,
  setTotalRecords,
  toast_topleft,
  searchService
}) => {

  useQuery([tableState.tableKeyName, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters],
    () => searchService.search(
      endpoint,
      tableState.rows,
      tableState.page,
      tableState.multiSortMeta,
      tableState.filters,
      sortMapping,
      [],
      nonNullFieldsTable
    ),
    {
      onSuccess: (data) => {
        setIsInEditMode(false);
        setEntities(data.results);
        setTotalRecords(data.totalResults);
      },
      onError: (error) => {
        toast_topleft.current.show([
          { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
        ]);
      },
      keepPreviousData: true,
      refetchOnWindowFocus: false,
      enabled: !!(tableState.rows)
    }
  );
};