import React, {useEffect, useRef, useState} from 'react';
import {DataTable} from 'primereact/datatable';
import {useSessionStorage} from '../../service/useSessionStorage';
import {useSetDefaultColumnOrder} from '../../utils/useSetDefaultColumnOrder';
import {Column} from 'primereact/column';
import {useQuery} from 'react-query';
import {Toast} from 'primereact/toast';
import {SearchService} from '../../service/SearchService';
import {Messages} from 'primereact/messages';
import {MultiSelect} from 'primereact/multiselect';
import {filterColumns, orderColumns, reorderArray, returnSorted} from '../../utils/utils';
import {DataTableHeaderFooterTemplate} from "../../components/DataTableHeaderFooterTemplate";
import {FilterComponentInputText} from "../../components/FilterComponentInputText";
import {FilterMultiSelectComponent} from "../../components/FilterMultiSelectComponent";
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {EllipsisTableCell} from "../../components/EllipsisTableCell";
import {ListTableCell} from "../../components/ListTableCell";


export const ConditionRelationTable = () => {
  const defaultColumnNames = ["Handle", "Reference", "Relation", "Conditions"];

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {},
    isFirst: true
  }

  const [tableState, setTableState] = useSessionStorage("ConRelTableSettings", initialTableState);

  let [conditionRelations, setConditionRelations] = useState(null);

  const [originalRows, setOriginalRows] = useState([]);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnList, setColumnList] = useState([]);

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const dataTable = useRef(null);
  const [errorMessages, setErrorMessages] = useState({});

  const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');

  useQuery(['conditionRelations', tableState],
    () => searchService.search('condition-relation', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, null, null,['handle']), {
      onSuccess: (data) => {
        setConditionRelations(data.results);
        setTotalRecords(data.totalResults);
      },
      onError: (error) => {
        toast_topleft.current.show([
          {life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false}
        ]);
      },
      onSettled: () => {
        setOriginalRows([]);
      },
      keepPreviousData: true,
      refetchOnWindowFocus: false
    });

  const setIsFirst = (value) => {
    let _tableState = {
      ...tableState,
      first: value,
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
      filters: {...filtersCopy}
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

  const aggregationFields = [
    'conditionRelationType.name'
  ];

  const nonNullFields = [
    'handle'
  ];


  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponentInputText
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
    />);
  };

  const filterComponentInputTextTemplate = (filterName, fields) => {
    return (<FilterComponentInputText
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
    />);
  };

  const FilterMultiSelectComponentTemplate = (filterName, field, useKeywordFields = false) => {
    return (<FilterMultiSelectComponent
      isEnabled={isEnabled}
      field={field}
      useKeywordFields={useKeywordFields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
      aggregationFields={aggregationFields}
      tableState={tableState}
      annotationsAggregations='conditionRelationAnnotationAggregation'
      endpoint='condition-relation'
    />);
  }

  const conditionRelationTypeEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="conditionRelationType"
          options={conditionRelationTypeTerms}
          //editorChange={onDiseaseRelationEditorValueChange}
          props={props}
          showClear={false}
          placeholderText={props.rowData.conditionRelationType.name}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionRelationType"}/>
      </>
    );
  };

  const conditionTemplate = (rowData) => {
    if (rowData.conditions) {
      const listTemplate = (condition) => {
        return (
          <EllipsisTableCell>
            {condition.conditionSummary}
          </EllipsisTableCell>
        );
      };
      return (
        <>
            <ListTableCell template={listTemplate} listData={rowData.conditions} showBullets={true}/>
        </>
      );
    }
  };


  const columns = [
    {
      field: "handle",
      header: "Handle",
      sortable: isEnabled,
      filter: true,
      body: (rowData) => rowData.handle,
      filterElement: filterComponentTemplate("uniqueIdFilter", ["handle"])
    },
    {
      field: "singleReference.curie",
      header: "Reference",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("singleReferenceFilter", ["singleReference.curie"])
    },
    {
      field: "conditionRelationType.name",
      header: "Relation",
      sortable: isEnabled,
      filter: true,
      filterElement: FilterMultiSelectComponentTemplate("conditionRelationFilter", ["conditionRelationType.name"]),
      editor: (props) => conditionRelationTypeEditor(props)
    },
    {
      field: "conditions.conditionSummary",
      header: "Conditions",
      sortable: isEnabled,
      filter: true,
      body: conditionTemplate,
      filterElement: filterComponentTemplate("conditionsFilter", ["conditions.conditionSummary"])
    },

  ];

  useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

  const [columnWidths, setColumnWidths] = useState(() => {
    const width = 10;

    const widthsObject = {};

    columns.forEach((col) => {
      widthsObject[col.field] = width;
    });

    return widthsObject;
  });


  const createMultiselectComponent = (tableState, defaultColumnNames, isEnabled) => {
    return (<MultiSelect
      value={tableState.selectedColumnNames}
      options={defaultColumnNames}
      onChange={e => setSelectedColumnNames(e.value)}
      style={{width: '20em', textAlign: 'center'}}
      disabled={!isEnabled}
    />);
  };

  const header = (
    <DataTableHeaderFooterTemplate
      title={"Condition Relation Handles Table"}
      tableState={tableState}
      defaultColumnNames={defaultColumnNames}
      multiselectComponent={createMultiselectComponent(tableState, defaultColumnNames, isEnabled)}
      onclickEvent = {(event) => resetTableState(event)}
      isEnabled={isEnabled}
    />
  );

  const resetTableState = () => {
    setTableState(initialTableState);
    dataTable.current.state.columnOrder = initialTableState.selectedColumnNames;
    const _columnWidths = {...columnWidths};

    Object.keys(_columnWidths).map((key) => {
      return _columnWidths[key] = 10;
    });

    setColumnWidths(_columnWidths);
    dataTable.current.el.children[1].scrollLeft = 0;
  };


  useEffect(() => {
    const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
    const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
    setColumnList(
      orderedColumns.map((col) => {
        return <Column
          style={{'minWidth': `${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
          headerClassName='surface-0'
          columnKey={col.field}
          key={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          filter={col.filter}
          showFilterMenu={false}
          filterElement={col.filterElement}
          editor={col.editor}
          body={col.body}
        />;
      })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled, columnWidths]);

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
      <Toast ref={toast_topleft} position="top-left"/>
      <Toast ref={toast_topright} position="top-right"/>
      <Messages ref={errorMessage}/>
      <DataTable value={conditionRelations} header={header} reorderableColumns={isEnabled}
              tableClassName='p-datatable-md' scrollable scrollDirection="horizontal" tableStyle={{width: '200%'}} scrollHeight="62vh"
              ref={dataTable}
              filterDisplay="row"
              onColReorder={colReorderHandler}
              sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
              first={tableState.first}
              dataKey="id" resizableColumns columnResizeMode="expand" showGridlines onColumnResizeEnd={handleColumnResizeEnd}
              paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
              paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
              currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
      >
          <Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}}
            headerStyle={{ width: '7rem', position: 'sticky' }} bodyStyle={{ textAlign: 'center' }} frozen headerClassName='surface-0'/>
        {columnList}
      </DataTable>
    </div>
  )
}
