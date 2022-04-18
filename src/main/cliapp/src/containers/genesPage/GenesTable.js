import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { DataTable } from 'primereact/datatable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { MultiSelect } from 'primereact/multiselect';
import { Tooltip } from 'primereact/tooltip';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";

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
  const [columnList, setColumnList] = useState([]);

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
          title = {"Genes Table"}
          tableState = {tableState}
          defaultColumnNames = {defaultColumnNames}
          multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
          onclickEvent = {(event) => resetTableState(event)}
          isEnabled = {isEnabled}
      />
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

  const nameBodyTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`a${rowData.curie.replace(':', '')}`}>
          {rowData.name}
        </EllipsisTableCell>
        <Tooltip target={`.a${rowData.curie.replace(':', '')}`} content={rowData.name} />
      </>
    );
  };

  const taxonBodyTemplate = (rowData) => {
      if (rowData.taxon) {
          return (
              <>
                  <EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`}>
                      {rowData.taxon.name} ({rowData.taxon.curie})
                  </EllipsisTableCell>
                  <Tooltip target={`.${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
              </>
          );
      }
  };

  const columns = [
    {
      field: "curie",
      header: "Curie",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"]),
    },
    {
      field: "name",
      header: "Name",
      sortable: isEnabled,
      filter: true,
      body: nameBodyTemplate,
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
      field: "taxon.name",
      header: "Taxon",
      sortable: isEnabled,
      body: taxonBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("taxonFilter", ["taxon.curie","taxon.name"])
    }
  ];

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
        return <Column
          style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
          headerClassName='surface-0'
          field={col.field}
          header={col.header}
          filter={col.filter}
          columnKey={col.field}
          key={col.field}
          sortable={isEnabled}
          showFilterMenu={false}
          filterElement={col.filterElement}
          body={col.body}
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
        <DataTable value={genes} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable} filterDisplay="row" scrollHeight="62vh" scrollable
          tableClassName='w-12 p-datatable-md'
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          onColReorder={colReorderHandler}
          first={tableState.first}
          resizableColumns columnResizeMode="expand" showGridlines onColumnResizeEnd={handleColumnResizeEnd}
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnList}
        </DataTable>
      </div>
  )
}
