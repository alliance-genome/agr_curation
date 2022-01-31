import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useClearSessionStorage } from '../../service/useClearSessionStorage';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent';
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted, clearSessionStorage } from '../../utils/utils';
import { Button } from 'primereact/button';

export const AllelesTable = () => {

  const [alleles, setAlleles] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const dataTable = useRef(null);
  const stateKeys = {
    tableKey: "alleleTable.Config",
    columnsKey: "alleleTable.Columns",
    filtersKey: "alleleTable.Filters"
  }

  const defaultColumnNames = ["Curie", "Description", "Symbol", "Taxon"];
  const [selectedColumnNames, setSelectedColumnNames] = useSessionStorage(stateKeys.columnsKey, defaultColumnNames);
  const [filters, setFilters] = useSessionStorage(stateKeys.filtersKey, {});
  const defaultColumnsWidths = 100 / defaultColumnNames.length;
  const [columnsWidths, setColumnWidths] = useState(defaultColumnsWidths);

  useQuery(['alleles', rows, page, multiSortMeta, filters],
    () => searchService.search("allele", rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setAlleles(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      errorMessage.current.show([
        { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
      ])
    },
    keepPreviousData: true
  })


  const onLazyLoad = (event) => {
    setRows(event.rows);
    setPage(event.page);
    setFirst(event.first);
  }

  const onFilter = (filtersCopy) => {
    setFilters({ ...filtersCopy });
  };

  const onSort = (event) => {
    setMultiSortMeta(
      returnSorted(event, multiSortMeta)
    )
  };

  const symbolTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
  }

  const taxonTemplate = (rowData) => {
    return <div>{rowData.taxon.curie}</div>;
  }

  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponent
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={filters}
      onFilter={onFilter}
    />);
  }

  const columns = [
    {
      field: "curie",
      header: "Curie",
      sortable: { isEnabled },
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"])
    },
    {
      field: "description",
      header: "Description",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("descriptionFilter", ["description"])
    },
    {
      field: "symbol",
      header: "Symbol",
      body: symbolTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("symbolFilter", ["symbol"])
    },
    {
      field: "taxon.curie",
      header: "Taxon",
      body: taxonTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("taxonFilter", ["taxon.curie"])
    }
  ];

  const header = (
    <>
      <div style={{ textAlign: 'left' }}>
        <MultiSelect
          value={selectedColumnNames}
          options={defaultColumnNames}
          onChange={e => setSelectedColumnNames(e.value)}
          style={{ width: '20em' }}
          disabled={!isEnabled}
        />
      </div>
      <div style={{ textAlign: 'right' }}>
        <Button onClick={() => clearSessionStorage(stateKeys)}>Clear</Button>
      </div>
    </>

  );


  const clearSessionStorage = (dataKeys) => {
    console.log(dataTable);
    dataTable.current.clearState("alleleTableConfig");
    Object.values(dataKeys).forEach((value) => {
      sessionStorage.removeItem(value);
    });
    setFilters({});
    setSelectedColumnNames(defaultColumnNames);
    setColumnWidths(defaultColumnsWidths);
  }

  const filteredColumns = columns.filter((col) => {
    return selectedColumnNames.includes(col.header);
  });

  const columnMap = filteredColumns.map((col) => {
    return <Column
      columnKey={col.field}
      key={col.field}
      field={col.field}
      header={col.header}
      sortable={isEnabled}
      filter={col.filter}
      filterElement={col.filterElement}
      body={col.body}
      style={{ width: `${columnsWidths}%` }}
    />;
  });

  return (
    <div>
      <div className="card">
        <h3>Alleles Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={alleles} className="p-datatable-sm" header={header} reorderableColumns
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first}
          ref={dataTable}
          stateStorage="session" stateKey={stateKeys.tableKey}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          resizableColumns columnResizeMode="fit" showGridlines
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnMap}
        </DataTable>
      </div>
    </div>
  )
}
