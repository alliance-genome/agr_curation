import React, { useRef, useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from "primereact/messages";
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";

export const OntologyTable = ({ endpoint, ontologyAbbreviation, columns }) => {
  const defaultColumnNames = columns.map((col) => {
    return col.header;
  });

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {},
  }

  const [tableState, setTableState] = useSessionStorage(`${ontologyAbbreviation}TableSettings`, initialTableState);

  const [terms, setTerms] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnList, setColumnList] = useState([]);
  const searchService = new SearchService();
  const errorMessage = useRef(null);

  const dataTable = useRef(null);

  useQuery(['terms', tableState],
    () => searchService.search(endpoint, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setTerms(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      errorMessage.current.show([
        { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
      ])
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
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

    const createMultiselectComponent = (tableState,defaultColumnNames,isEnabled) => {
        return (<MultiSelect
            value={tableState.selectedColumnNames}
            options={defaultColumnNames}
            onChange={e => setSelectedColumnNames(e.value)}
            style={{ width: '20em', textAlign: 'center' }}
            disabled={!isEnabled}
        />);
    };

    const header = (
      <DataTableHeaderFooterTemplate
          title = {ontologyAbbreviation+" Table"}
          tableState = {tableState}
          defaultColumnNames = {defaultColumnNames}
          multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
          onclickEvent = {(event) => resetTableState(event)}
          isEnabled = {isEnabled}
      />
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

  const obsoleteTemplate = (rowData) => {
    if (rowData && rowData.obsolete !== null && rowData.obsolete !== undefined) {
      return <EllipsisTableCell>{JSON.stringify(rowData.obsolete)}</EllipsisTableCell>
    }
  };

  useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames);

  const [columnWidths, setColumnWidths] = useState(() => {
    const width = 20;

    const widthsObject = {};

    columns.forEach((col) => {
      widthsObject[col.field] = width;
    });

    return widthsObject;
  });

  useEffect(() => {
    const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
    const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
    setColumnList(
      orderedColumns.map((col) => {
        if (col.field === 'obsolete') {
          return <Column
            style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
            headerClassName='surface-0'
            columnKey={col.field}
            key={col.field}
            field={col.field}
            header={col.header}
            sortable={isEnabled}
            body={obsoleteTemplate}
            filter
            showFilterMenu={false}
            filterElement={filterComponentTemplate(col.field + "Filter", [col.field])}
          />;
        }
        return <Column
          style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
          headerClassName='surface-0'
          columnKey={col.field}
          key={col.field}
          field={col.field}
          header={col.header}
          body={col.body}
          sortable={isEnabled}
          filter
          showFilterMenu={false}
          filterElement={filterComponentTemplate(col.field + "Filter", [col.field])}
        />;
      })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled, columnWidths]);


  const resetTableState = () => {
    setTableState(initialTableState);
    dataTable.current.state.columnOrder = initialTableState.selectedColumnNames;
    const _columnWidths = {...columnWidths};

    Object.keys(_columnWidths).map((key) => {
      _columnWidths[key] = 20;
    });

    setColumnWidths(_columnWidths);
  }

  const colReorderHandler = (event) => {
    let _columnNames = [...tableState.selectedColumnNames];
    _columnNames = reorderArray(_columnNames, event.dragIndex, event.dropIndex);
    setSelectedColumnNames(_columnNames);
  };

  const handleColumnResizeEnd = (event) => {
    const currentWidth = event.element.clientWidth;
    const delta = event.delta;
    const newWidth = Math.floor(((currentWidth + delta) / window.innerWidth) * 100);
    const field = event.column.props.field;

    const _columnWidths = {...columnWidths};

    _columnWidths[field] = newWidth;
    setColumnWidths(_columnWidths);
  };

  return (
      <div className="card">
        <Messages ref={errorMessage} />
        <DataTable value={terms} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable} filterDisplay="row" scrollHeight="62vh" scrollable
          tableClassName='w-12 p-datatable-md'
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          onColReorder={colReorderHandler}
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={tableState.first}
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
          resizableColumns columnResizeMode="expand" showGridlines onColumnResizeEnd={handleColumnResizeEnd}
        >
          {columnList}

        </DataTable>
      </div>
  )
}
