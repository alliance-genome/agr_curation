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

import { returnSorted, filterColumns, orderColumns, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";

export const GenericDataTable = ({ 
  endpoint, 
  tableName, 
  columns,  
  mutation,
  sortMapping,
  aggregationFields,
}) => {
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
    isFirst: true,
    tableKeyName: tableName.replace(/\s+/g, ''), //remove whitespace from tableName
  }

  
  const [tableState, setTableState] = useSessionStorage(
  `${tableKeyName}TableSettings`, 
    initialTableState
);

  const [data, setData] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [originalRows, setOriginalRows] = useState([]);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnList, setColumnList] = useState([]);
  const searchService = new SearchService();

  const [errorMessages, setErrorMessages] = useState({});

  const rowsInEdit = useRef(0);
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const dataTable = useRef(null);

  useQuery([`${tableKeyName}Aggregations`, aggregationFields, tableState],
    () => searchService.search(endpoint, 0, 0, null, {}, {}, aggregationFields), {
    onSuccess: (data) => {
    },
    onError: (error) => {
      toast_topleft.current.show([
        { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
      ]);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  }
  );

  useQuery([tableKeyName, tableState],
    () => searchService.search(endpoint, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setData(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      toast_topleft.current.show([
        { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
      ])
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const setIsFirst = (value) => {
    let _tableState = {
      ...tableState,
      isFirst: value,
    };

    setTableState(_tableState);
  }

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

  const onRowEditInit = (event) => {
    rowsInEdit.current++;
    setIsEnabled(false);
    originalRows[event.index] = { ...data[event.index] };
    setOriginalRows(originalRows);
    //console.log(dataTable.current.state);
  };

  const onRowEditCancel = (event) => {
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    };

    let _data = [...data];
    _data[event.index] = originalRows[event.index];
    delete originalRows[event.index];
    setOriginalRows(originalRows);
    setData(_data);
    const errorMessagesCopy = errorMessages;
    errorMessagesCopy[event.index] = {};
    setErrorMessages({ ...errorMessagesCopy });

  };

 const onRowEditSave = (event) => {//possible to shrink?
    //console.log(event);
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    }
    let updatedRow = global.structuredClone(event.data);//deep copy
   
   //can this be changed? I believe this is happening because the backend is expecting an just on object with a curie for these
   //fields. Is that still the case? Can that be changed? If not, can this be extracted into some sort of utility method? Identify
   //what fields need this and if that info can kept in the method and just pass event.data through that method.
    if (Object.keys(event.data.subject).length >= 1) {
      event.data.subject.curie = trimWhitespace(event.data.subject.curie);
      updatedRow.subject = {};
      updatedRow.subject.curie = event.data.subject.curie;
    }
    if (Object.keys(event.data.object).length >= 1) {
      event.data.object.curie = trimWhitespace(event.data.object.curie);
      updatedRow.object = {};
      updatedRow.object.curie = event.data.object.curie;
    }
    if (event.data.diseaseGeneticModifier && Object.keys(event.data.diseaseGeneticModifier).length >= 1) {
      event.data.diseaseGeneticModifier.curie = trimWhitespace(event.data.diseaseGeneticModifier.curie);
      updatedRow.diseaseGeneticModifier = {};
      updatedRow.diseaseGeneticModifier.curie = event.data.diseaseGeneticModifier.curie;
    }


    mutation.mutate(updatedRow, {
      onSuccess: (data) => {
        toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });
        let annotations = [...diseaseAnnotations];
        annotations[event.index] = data.data.entity;
        setDiseaseAnnotations(annotations);
        const errorMessagesCopy = errorMessages;
        errorMessagesCopy[event.index] = {};
        setErrorMessages({ ...errorMessagesCopy });
      },
      onError: (error, variables, context) => {
        rowsInEdit.current++;
        setIsEnabled(false);
        toast_topright.current.show([
          { life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false }
        ]);

        let annotations = [...diseaseAnnotations];

        const errorMessagesCopy = errorMessages;

        //console.log(errorMessagesCopy);
        errorMessagesCopy[event.index] = {};
        Object.keys(error.response.data.errorMessages).forEach((field) => {
          let messageObject = {
            severity: "error",
            message: error.response.data.errorMessages[field]
          };
          errorMessagesCopy[event.index][field] = messageObject;
        });

        //console.log(errorMessagesCopy);
        setErrorMessages({ ...errorMessagesCopy });

        setDiseaseAnnotations(annotations);
        let _editingRows = { ...editingRows, ...{ [`${annotations[event.index].id}`]: true } };
        setEditingRows(_editingRows);
      },
      onSettled: (data, error, variables, context) => {

      },
    });
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
        title = {tableName + " Table"}
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

  useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

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
    let _tableState = {
      ...initialTableState,
      isFirst: false,
    };

    setTableState(_tableState);
    setDefaultColumnOrder(columns, dataTable, defaultColumnNames);
    const _columnWidths = {...columnWidths};

    Object.keys(_columnWidths).map((key) => {
      return _columnWidths[key] = 20;
    });

    setColumnWidths(_columnWidths);
    dataTable.current.el.children[1].scrollLeft = 0;
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
        <Toast ref={toast_topleft} position="top-left" />
        <Toast ref={toast_topright} position="top-right" />
        <DataTable value={data} className="p-datatable-sm" header={header} reorderableColumns
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
