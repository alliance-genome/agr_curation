import { useRef, useState } from 'react';
import { useQuery } from 'react-query';

import { SearchService } from '../../service/SearchService';
import { useSessionStorage } from '../../service/useSessionStorage';

import { trimWhitespace, returnSorted, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';

export const useGenericDataTable = ({ 
  endpoint, 
  tableName, 
  columns,  
  aggregationFields,
  curieFields,
  sortMapping,
  mutation,
  setIsEnabled,
  toasts,
  initialColumnWidth,
  errorObject,
  defaultVisibleColumns
}) => {

  const defaultColumnNames = columns.map((col) => {
    return col.header;
  });

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultVisibleColumns ? defaultVisibleColumns : defaultColumnNames,
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
  const [columnWidths, setColumnWidths] = useState(() => {
    const width = initialColumnWidth;

    const widthsObject = {};

    columns.forEach((col) => {
      widthsObject[col.field] = width;
    });

    return widthsObject;
  });

  const searchService = new SearchService();

  const { errorMessages, setErrorMessages } = errorObject;

  const rowsInEdit = useRef(0);
  const dataTable = useRef(null);

  const { toast_topleft, toast_topright } = toasts;


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

  useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

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

    if(curieFields){
      curieFields.forEach((field) => {
        if (event.data[field] && Object.keys(event.data[field]).length >= 1) {
          const curie = trimWhitespace(event.data[field].curie);
          updatedRow[field] = {};
          updatedRow[field].curie = curie;
        }
      });
    };

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

  return {
    setSelectedColumnNames, 
    defaultColumnNames, 
    tableState, 
    resetTableState, 
    onFilter, 
    setColumnList, 
    columnWidths, 
    entity, 
    dataTable, 
    editingRows, 
    onRowEditInit, 
    onRowEditCancel,
    onRowEditSave,
    onRowEditChange,
    onSort,
    colReorderHandler,
    handleColumnResizeEnd,
    totalRecords,
    onLazyLoad,
    columnList,
  };
};
