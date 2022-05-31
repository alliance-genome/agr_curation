import React, { useRef, useState, useEffect } from 'react';
import { useQuery } from 'react-query';

import { Column } from 'primereact/column';
import { MultiSelect } from 'primereact/multiselect';

import { FilterComponentInputText } from '../FilterComponentInputText'
import { EllipsisTableCell } from '../EllipsisTableCell';
import { DataTableHeaderFooterTemplate } from "../DataTableHeaderFooterTemplate";

import { SearchService } from '../../service/SearchService';
import { useSessionStorage } from '../../service/useSessionStorage';

import { trimWhitespace, returnSorted, filterColumns, orderColumns, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';

export const useGenericDataTable = ({ 
  endpoint, 
  tableName, 
  columns,  
  aggregationFields,
  isEditable,
  curieFields,
  sortMapping,
  mutation,
  isEnabled,
  setIsEnabled,
  toasts,
  initialColumnWidth,
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
  `${initialTableState.tableKeyName}TableSettings`, 
    initialTableState
  );

  //probably should be plural
  const [entity, setEntity] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [originalRows, setOriginalRows] = useState([]);
  const [columnList, setColumnList] = useState([]);
  const [editingRows, setEditingRows] = useState({});
  const searchService = new SearchService();

  const [errorMessages, setErrorMessages] = useState({});

  const rowsInEdit = useRef(0);
  const dataTable = useRef(null);

  const { toast_topleft, toast_topright } = toasts;

  //what is this doing?
  useQuery([`${tableState.tableKeyName}Aggregations`, aggregationFields, tableState],
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

  useQuery([tableState.tableKeyName, tableState],
    () => searchService.search(endpoint, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, []), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setEntity(data.results);
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
    originalRows[event.index] = { ...entity[event.index] };
    setOriginalRows(originalRows);
  };

  const onRowEditCancel = (event) => {
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {//can editingRows be used here instead of tracking this?
      setIsEnabled(true);
    };

    let _entity = [...entity];
    _entity[event.index] = originalRows[event.index];
    delete originalRows[event.index];
    setOriginalRows(originalRows);
    setEntity(_entity);
    const errorMessagesCopy = errorMessages;
    errorMessagesCopy[event.index] = {};
    setErrorMessages({ ...errorMessagesCopy });

  };

  const onRowEditSave = (event) => {
    rowsInEdit.current--;

    if (rowsInEdit.current === 0) {//can editingRows be used here instead of tracking this?
      setIsEnabled(true);
    }

    let updatedRow = global.structuredClone(event.data);//deep copy

    //does this need to happen to every field that is a plain object? If so, can we just check for objects in event.data and do this to each?
    curieFields.forEach((field) => {
      if (event.data[field] && Object.keys(event.data[field]).length >= 1) {
        event.data[field].curie = trimWhitespace(event.data[field].curie);
        updatedRow[field] = {};
        updatedRow[field] = event.data[field];
      }
    });

    mutation.mutate(updatedRow, {
      onSuccess: (response, variables, context) => {
        toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

        let _entity = global.structuredClone(entity);
        _entity[event.index] = response.data.entity;
        setEntity(_entity);
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

        let _entity = global.structuredClone(entity);

        const errorMessagesCopy = errorMessages;

        console.log(errorMessagesCopy);
        errorMessagesCopy[event.index] = {};
        Object.keys(error.response.data.errorMessages).forEach((field) => {
          let messageObject = {
            severity: "error",
            message: error.response.data.errorMessages[field]
          };
          errorMessagesCopy[event.index][field] = messageObject;
        });

        console.log(errorMessagesCopy);
        setErrorMessages({ ...errorMessagesCopy });

        setEntity(_entity);
        let _editingRows = { ...editingRows, ...{ [`${_entity[event.index].id}`]: true } };
        setEditingRows(_editingRows);
      },
    });
  };


  const onRowEditChange = (event) => {
    setEditingRows(event.data);
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
    const width = initialColumnWidth;

    const widthsObject = {};

    columns.forEach((col) => {
      widthsObject[col.field] = width;
    });

    return widthsObject;
  });

  /*filterElement on the columns will have an object like this:
    *{
  *    type: "input",
  *    name: "name",
  *    fields: ["field", "field.subField"]
  *  }
    *in this return, filterElement can be set equal to a funtion that returns a funtion. 
  *    Within that function there will be logic to select which type of element to return
  *    base on the type field*/
    /*may need some logic here to choose the appropriate filter based on this fields value
      also, this filter fields logic will need to change as well. perhaps the filterElement field
      can be on object with the type of filter and what fields need to be filtered*/
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
            body={obsoleteTemplate/*just have an inline check here instead of two different columns?*/}
            filter
            editor={col.editor}
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
          editor={col.editor}
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
      return _columnWidths[key] = initialColumnWidth;
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

  const tableProps = { 
          dataKey:'id',
          value:entity,
          header:header, 
          ref:dataTable, 
          filterDisplay:"row", 
          scrollHeight:"62vh", 
          scrollable: true,
          tableClassName:'p-datatable-md',
          editMode: "row",
          onRowEditInit: onRowEditInit,
          onRowEditCancel: onRowEditCancel, 
          onRowEditSave: onRowEditSave,
          editingRows: editingRows, 
          onRowEditChange:onRowEditChange,
          sortMode:"multiple", 
          removableSort:true, 
          onSort:onSort, 
          multiSortMeta:tableState.multiSortMeta,
          onColReorder:colReorderHandler, 
          reorderableColumns: true, 
          resizableColumns: true, 
          columnResizeMode:"expand", 
          showGridlines: true, 
          onColumnResizeEnd:handleColumnResizeEnd,
          paginator: true, 
          totalRecords:totalRecords, 
          onPage:onLazyLoad, 
          lazy: true, 
          first:tableState.first,
          paginatorTemplate:"CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown",
          currentPageReportTemplate:"Showing {first} to {last} of {totalRecords}", 
          rows:tableState.rows, 
          rowsPerPageOptions:[10, 20, 50, 100, 250, 1000],
  }

  return {
    tableProps,
    columnList
  };
};
