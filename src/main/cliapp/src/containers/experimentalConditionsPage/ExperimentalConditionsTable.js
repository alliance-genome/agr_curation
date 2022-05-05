import React, { useRef, useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { Column } from 'primereact/column';
import { InputTextEditor } from '../../components/InputTextEditor';
import { AutocompleteEditor } from '../../components/AutocompleteEditor';
import { useMutation, useQuery } from 'react-query';
import { useOktaAuth } from '@okta/okta-react';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { MultiSelect } from 'primereact/multiselect';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { trimWhitespace, returnSorted, filterColumns, orderColumns, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Tooltip } from 'primereact/tooltip';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";


export const ExperimentalConditionsTable = () => {
  const defaultColumnNames = ["Unique ID", "Summary", "Statement", "Class", "Condition Term", "Gene Ontology", "Chemical", "Anatomy", "Condition Taxon", "Quantity", "Free Text"];
  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {},
    isFirst: true,
  }

  const [tableState, setTableState] = useSessionStorage("ExConTableSettings", initialTableState);

  let [experimentalConditions, setExperimentalConditions] = useState(null);

  const [errorMessages, setErrorMessages] = useState({});
  const [originalRows, setOriginalRows] = useState([]);
  const [editingRows, setEditingRows] = useState({});
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnList, setColumnList] = useState([]);

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const { authState } = useOktaAuth();
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const rowsInEdit = useRef(0);
  const dataTable = useRef(null);

  let experimentalConditionService = null;

  const sortMapping = {
    'conditionGeneOntology.name': ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace']
  }

  useQuery(['experimentalConditions', tableState],
    () => searchService.search('experimental-condition', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping), {
    onSuccess: (data) => {
      setExperimentalConditions(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      toast_topleft.current.show([
        { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
      ]);
    },
    onSettled: () => {
      setOriginalRows([]);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const mutation = useMutation(updatedCondition => {
    if (!experimentalConditionService) {
      experimentalConditionService = new ExperimentalConditionService(authState);
    }
    return experimentalConditionService.saveExperimentalCondition(updatedCondition);
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
    originalRows[event.index] = { ...experimentalConditions[event.index] };
    setOriginalRows(originalRows);
    console.log("in onRowEditInit");
  };

  const onRowEditCancel = (event) => {
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    };

    let conditions = [...experimentalConditions];
    conditions[event.index] = originalRows[event.index];
    delete originalRows[event.index];
    setOriginalRows(originalRows);
    setExperimentalConditions(conditions);
    const errorMessagesCopy = errorMessages;
    errorMessagesCopy[event.index] = {};
    setErrorMessages({ ...errorMessagesCopy });

  };


  const onRowEditSave = (event) => {
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    }
    if (event.data.conditionGeneOntology === null) {
      delete event.data.conditionGeneOntology;
    }
    let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy

    const curieFields = ["conditionClass", "conditionId", "conditionAnatomy", "conditionTaxon", "conditionGeneOntology", "conditionChemical"];
    for (var ix = 0; ix < curieFields.length; ix++) {
      if (event.data[curieFields[ix]] && Object.keys(event.data[curieFields[ix]]).length >= 1) {
        event.data[curieFields[ix]].curie = trimWhitespace(event.data[curieFields[ix]].curie);
        updatedRow[curieFields[ix]] = {};
        updatedRow[curieFields[ix]] = event.data[curieFields[ix]];
      }
    }
    mutation.mutate(updatedRow, {
      onSuccess: (response, variables, context) => {
        toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

        let conditions = [...experimentalConditions];
        const columns = Object.keys(response.data.entity);
        columns.forEach(column => {
          conditions[event.index][column] = response.data.entity[column];
        });
        setExperimentalConditions(conditions);
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

        let conditions = [...experimentalConditions];

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

        setExperimentalConditions(conditions);
        let _editingRows = { ...editingRows, ...{ [`${conditions[event.index].id}`]: true } };
        setEditingRows(_editingRows);
      },
      onSettled: (data, error, variables, context) => {

      },
    });
  };

  const onRowEditChange = (event) => {
    setEditingRows(event.data);
  };

  const freeTextEditor = (props, fieldname) => {
    return (
      <>
        <InputTextEditor
          rowProps={props}
          fieldName={fieldname}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldname} />
      </>
    );
  };


  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponentInputText
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
    />);
  };

  const uniqueIdBodyTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`a${rowData.id}`}>{rowData.uniqueId}</EllipsisTableCell>
        <Tooltip target={`.a${rowData.id}`} content={rowData.uniqueId} />
      </>
    )
  };

  const summaryBodyTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.conditionSummary}</EllipsisTableCell>
        <Tooltip target={`.b${rowData.id}`} content={rowData.conditionSummary} />
      </>
    )
  };

  const statementBodyTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`c${rowData.id}`}>{rowData.conditionStatement}</EllipsisTableCell>
        <Tooltip target={`.c${rowData.id}`} content={rowData.conditionStatement} />
      </>
    )
  };

  const conditionClassBodyTemplate = (rowData) => {
    if (rowData.conditionClass) {
      return (
        <>
          <EllipsisTableCell otherClasses={rowData.conditionClass.curie.replace(':', '')}>{rowData.conditionClass.name} ({rowData.conditionClass.curie})</EllipsisTableCell>
          <Tooltip target={`.${rowData.conditionClass.curie.replace(':', '')}`} content={`${rowData.conditionClass.name} ${rowData.conditionClass.curie}`} />
        </>
      )
    }
  };

  const conditionIdBodyTemplate = (rowData) => {
    if (rowData.conditionId) {
      return (
        <>
          <EllipsisTableCell otherClasses={rowData.conditionId.curie.replace(':', '')}>{rowData.conditionId.name} ({rowData.conditionId.curie})</EllipsisTableCell>
          <Tooltip target={`.${rowData.conditionId.curie.replace(':', '')}`} content={`${rowData.conditionId.name} ${rowData.conditionId.curie}`} />
        </>
      )
    }
  };

  const conditionGeneOntologyBodyTemplate = (rowData) => {
    if (rowData.conditionGeneOntology) {
      return <EllipsisTableCell>{rowData.conditionGeneOntology.name} ({rowData.conditionGeneOntology.curie})</EllipsisTableCell>;
    }
  };

  const conditionChemicalBodyTemplate = (rowData) => {
    if (rowData.conditionChemical) {
      return <EllipsisTableCell>{rowData.conditionChemical.name} ({rowData.conditionChemical.curie})</EllipsisTableCell>;
    }
  };

  const conditionAnatomyBodyTemplate = (rowData) => {
    if (rowData.conditionAnatomy) {
      return <EllipsisTableCell>{rowData.conditionAnatomy.name} ({rowData.conditionAnatomy.curie})</EllipsisTableCell>;
    }
  };

  const conditionTaxonBodyTemplate = (rowData) => {
    if (rowData.conditionTaxon) {
      return (
          <>
          <EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.conditionTaxon.curie.replace(':', '')}`}>
              {rowData.conditionTaxon.name} ({rowData.conditionTaxon.curie})
          </EllipsisTableCell>
          <Tooltip target={`.${"TAXON_NAME_"}${rowData.conditionTaxon.curie.replace(':', '')}`} content= {`${rowData.conditionTaxon.name} (${rowData.conditionTaxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
          </>
      );
    }
  };

  const conditionClassEditorTemplate = (props, autocomplete) => {
    return (
      <>
      <AutocompleteEditor
        autocompleteFields={autocomplete}
        rowProps={props}
        searchService={searchService}
        fieldname='conditionClass'
        endpoint='zecoterm'
        filterName='conditionClassEditorFilter'
        fieldName='conditionClass'
        otherFilters={{
          "obsoleteFilter": {
            "obsolete": {
              queryString: false
            }
          },
          "subsetFilter": {
            "subsets": {
              queryString: 'ZECO_0000267'
            }
          }
        }}
      />
      <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField='conditionClass'
        />
      </>
    );
  };

  const singleOntologyEditorTemplate = (props, fieldname, endpoint, autocomplete) => {
    return (
      <>
        <AutocompleteEditor
          autocompleteFields={autocomplete}
          rowProps={props}
          searchService={searchService}
          fieldname={fieldname}
          endpoint={endpoint}
          filterName='singleOntologyFilter'
          fieldName={fieldname}
          otherFilters={{
            obsoleteFilter: {
              "obsolete": {
                queryString: false
              }
            }
          }}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField={fieldname}
        />
      </>
    );
  };

  const curieAutocompleteFields = ["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms"];

  const columns = [
    {
      field: "uniqueId",
      header: "Unique ID",
      sortable: isEnabled,
      filter: true,
      body: uniqueIdBodyTemplate,
      filterElement: filterComponentTemplate("uniqueIdFilter", ["uniqueId"])
    },
    {
      field: "conditionSummary",
      header: "Summary",
      sortable: isEnabled,
      filter: true,
      body: summaryBodyTemplate,
      filterElement: filterComponentTemplate("conditionSummaryFilter", ["conditionSummary"])
    },
    {
      field: "conditionStatement",
      header: "Statement",
      sortable: isEnabled,
      filter: true,
      body: statementBodyTemplate,
      filterElement: filterComponentTemplate("conditionStatementFilter", ["conditionStatement"])
    },
    {
      field: "conditionClass.name",
      header: "Class",
      sortable: isEnabled,
      body: conditionClassBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("conditionClassFilter", ["conditionClass.curie", "conditionClass.name"]),
      editor: (props) => conditionClassEditorTemplate(props, curieAutocompleteFields)
    },
    {
      field: "conditionId.name",
      header: "Condition Term",
      sortable: isEnabled,
      body: conditionIdBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("conditionIdFilter", ["conditionId.curie", "conditionId.name"]),
      editor: (props) => singleOntologyEditorTemplate(props, "conditionId", "experimentalconditionontologyterm", curieAutocompleteFields)
    },
    {
      field: "conditionGeneOntology.name",
      header: "Gene Ontology",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("conditionGeneOntologyFilter", ["conditionGeneOntology.curie", "conditionGeneOntology.name"]),
      editor: (props) => singleOntologyEditorTemplate(props, "conditionGeneOntology", "goterm", curieAutocompleteFields),
      body: conditionGeneOntologyBodyTemplate
    },
    {
      field: "conditionChemical.name",
      header: "Chemical",
      sortable: isEnabled,
      body: conditionChemicalBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("conditionChemicalFilter", ["conditionChemical.curie", "conditionChemical.name"]),
      editor: (props) => singleOntologyEditorTemplate(props, "conditionChemical", "chemicalterm", curieAutocompleteFields)
    },
    {
      field: "conditionAnatomy.name",
      header: "Anatomy",
      sortable: isEnabled,
      body: conditionAnatomyBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("conditionAnatomyFilter", ["conditionAnatomy.curie", "conditionAnatomy.name"]),
      editor: (props) => singleOntologyEditorTemplate(props, "conditionAnatomy", "anatomicalterm", curieAutocompleteFields)
    },
    {
      field: "conditionTaxon.name",
      header: "Condition Taxon",
      sortable: isEnabled,
      body: conditionTaxonBodyTemplate,
      filter: true,
      filterElement: filterComponentTemplate("conditionTaxonFilter", ["conditionTaxon.curie", "conditionTaxon.name"]),
      editor: (props) => singleOntologyEditorTemplate(props, "conditionTaxon", "ncbitaxonterm", curieAutocompleteFields)
    },
    {
      field: "conditionQuantity",
      header: "Quantity",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("conditionQuantityFilter", ["conditionQuantity"]),
      editor: (props) => freeTextEditor(props, "conditionQuantity")
    }
    ,
    {
      field: "conditionFreeText",
      header: "Free Text",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("conditionFreeTextFilter", ["conditionFreeText"]),
      editor: (props) => freeTextEditor(props, "conditionFreeText")
    }

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
          editor={col.editor}
          body={col.body}
        />;
      })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled, columnWidths]);

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
          title = {"Experimental Conditions Table"}
          tableState = {tableState}
          defaultColumnNames = {defaultColumnNames}
          multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
          onclickEvent = {(event) => resetTableState(event)}
          isEnabled = {isEnabled}
      />
  );

  const resetTableState = () => {
    let _tableState = {
      ...initialTableState,
      isFirst: false,
    };

    setTableState(_tableState);
    setDefaultColumnOrder(columns, dataTable, defaultColumnNames);
    const _columnWidths = {...columnWidths};

    Object.keys(_columnWidths).map((key) => {
      _columnWidths[key] = 10;
    });

    setColumnWidths(_columnWidths);
    dataTable.current.el.children[1].scrollLeft = 0;
  }

  const colReorderHandler = (event) => {
    let _columnNames = [...tableState.selectedColumnNames];
    //minus one because of the rowEditor column at the start of the table
    _columnNames = reorderArray(_columnNames, event.dragIndex - 1, event.dropIndex - 1);
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
        <Messages ref={errorMessage} />
        <DataTable value={experimentalConditions} header={header} reorderableColumns={isEnabled}
          tableClassName='p-datatable-md' scrollable scrollDirection="horizontal" tableStyle={{ width: '200%' }} scrollHeight="62vh"
          ref={dataTable}
          filterDisplay="row"
          editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
          editingRows={editingRows} onRowEditChange={onRowEditChange}
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
