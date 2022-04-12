import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";
import { Tooltip } from 'primereact/tooltip';

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
  const [columnList, setColumnList] = useState([]);

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
          title = {"Alleles Table"}
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
  }

  const descriptionTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`a${rowData.curie.replace(':', '')}`}>
          {rowData.description}
        </EllipsisTableCell>
        <Tooltip target={`.a${rowData.curie.replace(':', '')}`} content={rowData.description} />
      </>
    );
  }

  const symbolTemplate = (rowData) => {
    return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
  }

  const taxonTemplate = (rowData) => {
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
      body: descriptionTemplate,
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
      field: "taxon.name",
      header: "Taxon",
      body: taxonTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("taxonFilter", ["taxon.curie","taxon.name"])
    }
  ];

  useSetDefaultColumnOrder(columns, dataTable);

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
          columnKey={col.field}
          key={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          filter={col.filter}
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
    const _columnWidths = { ...columnWidths };

    Object.keys(_columnWidths).map((key) => {
      return _columnWidths[key] = 20;
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

    const _columnWidths = { ...columnWidths };

    _columnWidths[field] = newWidth;
    setColumnWidths(_columnWidths);
  };
  
  return (
      <div className="card">
        <Messages ref={errorMessage} />
        <DataTable value={alleles} className="p-datatable-sm" header={header} reorderableColumns
          ref={dataTable} scrollHeight="62vh" scrollable
          tableClassName='w-12 p-datatable-md'
          filterDisplay="row"
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={tableState.first}
          onColReorder={colReorderHandler} onColumnResizeEnd={handleColumnResizeEnd}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          resizableColumns columnResizeMode="expand" showGridlines
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnList}
        </DataTable>
      </div>
  )
}
