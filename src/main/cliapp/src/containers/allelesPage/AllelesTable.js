import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { Button } from 'primereact/button';

export const AllelesTable = () => {
  const defaultColumnNames = ["Curie", "Description", "Symbol", "Taxon"];

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {}
  };

  const [tableState, setTableState] = useSessionStorage("alleleTableSettings", initialTableState);

  const [alleles, setAlleles] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnMap, setColumnMap] = useState([]);

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const dataTable = useRef(null);

  useQuery(['alleles', tableState],
    () => searchService.search("allele", tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
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
    let _tableState = {
      ...tableState,
      rows: event.rows,
      page: event.page,
      first: event.first
    };

    setTableState(_tableState);
  }

  const onFilter = (filtersCopy) => {
    let _tableState = {
      ...tableState,
      filters: { ...filtersCopy }
    }
    setTableState(_tableState);
  };

  const onSort = (event) => {
    let _tableState = {
      ...tableState,
      multiSortMeta: returnSorted(event, tableState.multiSortMeta)
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
    return (<FilterComponentInputText
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
    />);
  }

  const symbolTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
  }

  const taxonTemplate = (rowData) => {
    return <div>{rowData.taxon.curie}</div>;
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

  useEffect(() => {
    const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
    const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
    setColumnMap(
      orderedColumns.map((col) => {
        return <Column
          columnKey={col.field}
          key={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          filter={col.filter}
          showFilterMenu={false}
          style={{whiteSpace: 'normal'}}
          filterElement={col.filterElement}
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
        <h3>Alleles Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={alleles} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable}
          filterDisplay="row"
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={tableState.first}
          onColReorder={colReorderHandler}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          resizableColumns columnResizeMode="fit" showGridlines
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnMap}
        </DataTable>
      </div>
    </div>
  )
}
