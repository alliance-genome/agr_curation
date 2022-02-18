import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { DataTable } from 'primereact/datatable';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';

export const GenesTable = () => {
  const defaultColumnNames = ["Curie", "Name", "Symbol", "Taxon"];

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {},
  }

  const [tableState, setTableState] = useSessionStorage("geneTableSettings", initialTableState);

  const [isEnabled, setIsEnabled] = useState(true);
  const [genes, setGenes] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [columnMap, setColumnMap] = useState([]);

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const dataTable = useRef(null);

  useQuery(['genes', tableState],
    () => searchService.search('gene', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setGenes(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      errorMessage.current.show([
        { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
      ])
    },
    keepPreviousData: true
  });

  const onLazyLoad = (event) => {
    let _tableState = {
      ...tableState,
      rows: event.rows,
      page: event.page,
      first: event.first
    };

    setTableState(_tableState);
  }

  const onSort = (event) => {
    let _tableState = {
      ...tableState,
      multiSortMeta: returnSorted(event, tableState.multiSortMeta)
    }
    setTableState(_tableState);
  };

  const onFilter = (filtersCopy) => {
    let _tableState = {
      ...tableState,
      filters: { ...filtersCopy }
    }
    setTableState(_tableState);
  };

  const setSelectedColumnNames = (newValue) => {
    let _tableState = {
      ...tableState,
      selectedColumnNames: newValue
    };

    setTableState(_tableState);
  };

  const header = (
    <>
      <div style={{ textAlign: 'left' }}>
        <MultiSelect
          value={tableState.selectedColumnNames}
          options={defaultColumnNames}
          onChange={(event) => setSelectedColumnNames(event.value)}
          style={{ width: '20em' }}
          disabled={!isEnabled}
        />
      </div>
      <div style={{ textAlign: 'right' }}>
        <Button onClick={(event) => resetTableState(event)}>Reset Table</Button>
      </div>
    </>
  );

  const filterComponentTemplate = (filterName, fields) => {
    return (
      <FilterComponentInputText
        isEnabled={isEnabled}
        fields={fields}
        filterName={filterName}
        currentFilters={tableState.filters}
        onFilter={onFilter}
      />);
  };

  const columns = [
    {
      field: "curie",
      header: "Curie",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"]),
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' }
    },
    {
      field: "name",
      header: "Name",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("nameFilter", ["name"])
    },
    {
      field: "symbol",
      header: "Symbol",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("symbolFilter", ["symbol"])
    },
    {
      field: "taxon.curie",
      header: "Taxon",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("taxonFilter", ["taxon.curie"])
    }
  ];

  useEffect(() => {
    const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
    const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
    setColumnMap(
      orderedColumns.map((col) => {
        return <Column
          field={col.field}
          header={col.header}
          filter={col.filter}
          columnKey={col.field}
          key={col.field}
          sortable={isEnabled}
          filterElement={col.filterElement}
          style={col.style}
        />;
      })
    );


    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled]);

  const resetTableState = () => {
    setTableState(initialTableState);
    dataTable.current.state.columnOrder = initialTableState.selectedColumnNames;
  }


  const colReorderHandler = (event) => {
    let _columnNames = [...tableState.selectedColumnNames];
    _columnNames = reorderArray(_columnNames, event.dragIndex, event.dropIndex);
    setSelectedColumnNames(_columnNames);
  };

  return (
    <div>
      <div className="card">
        <h3>Genes Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={genes} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable}
          filterDisplay="row"
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          onColReorder={colReorderHandler}
          first={tableState.first}
          resizableColumns columnResizeMode="fit" showGridlines
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnMap}
        </DataTable>

      </div>
    </div>
  )
}
