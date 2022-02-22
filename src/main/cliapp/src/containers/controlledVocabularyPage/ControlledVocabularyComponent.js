import React, { useRef, useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { useSessionStorage } from '../../service/useSessionStorage';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { MultiSelect } from 'primereact/multiselect';
import { Button } from 'primereact/button';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';

export const ControlledVocabularyComponent = () => {
  const defaultColumnNames = ["Id", "Name", "Abbreviation", "Vocabulary", "Definition", "Obsolete"];

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {}
  };

  const [tableState, setTableState] = useSessionStorage("ConVocTableSettings", initialTableState);

  const [terms, setTerms] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnMap, setColumnMap] = useState([]);

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const dataTable = useRef(null);

  useQuery(['genes', tableState],
    () => searchService.search("vocabularyterm", tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setTerms(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      errorMessage.current.show([
        { severity: 'error', summary: 'Error:&nbsp;', detail: error.message, sticky: true }
      ]);
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
          onChange={e => setSelectedColumnNames(e.value)}
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
  };

  const columns = [
    {
      field: "id",
      header: "Id"
    },
    {
      field: "name",
      header: "Name",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("nameFilter", ["name"])
    },
    {
      field: "abbreviation",
      header: "Abbreviation",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("abbreviationFilter", ["abbreviation"])
    },
    {
      field: "vocabulary.name",
      header: "Vocabulary",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("vocabularyNameFilter", ["vocabulary.name"])
    },
    {
      field: "definition",
      header: "Definition",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("definitionFilter", ["definition"])
    },
    {
      field: "isObsolete",
      header: "Obsolete",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("isObsoleteFilter", ["isObsolete"])
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
        <h3>Controlled Vocabulary Terms Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={terms} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable}
          filterDisplay="row"
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          onColReorder={colReorderHandler}
          first={tableState.first} onFilter={onFilter} filters={tableState.filters}
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
          resizableColumns columnResizeMode="fit" showGridlines
        >
          {columnMap}
        </DataTable>

      </div>
    </div>
  );
};
