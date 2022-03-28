import React, {useRef, useState, useEffect, useReducer} from 'react';
import { DataTable } from 'primereact/datatable';
import { useSessionStorage } from '../../service/useSessionStorage';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useMutation, useQuery} from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { MultiSelect } from 'primereact/multiselect';
import { Toast } from 'primereact/toast';
import {useOktaAuth} from "@okta/okta-react";

import {returnSorted, filterColumns, orderColumns, reorderArray} from '../../utils/utils';
import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {VocabularyService} from "../../service/VocabularyService";
import {TrueFalseDropdown} from "../../components/TrueFalseDropDownSelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {InputTextEditor} from "../../components/InputTextEditor";
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {NewTermForm} from "../../containers/controlledVocabularyPage/NewTermForm";
import {NewVocabularyForm} from "../../containers/controlledVocabularyPage/NewVocabularyForm";
import {DataTableHeaderFooterTemplate} from "../../components/DataTableHeaderFooterTemplate";
import {Button} from "primereact/button";

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

    const newTermReducer = (state, action) => {
        switch (action.type) {
            case 'RESET':
                return { name: "" };
            default:
                return { ...state, [action.field]: action.value };
        }
    };

  const [tableState, setTableState] = useSessionStorage("ConVocTableSettings", initialTableState);

  const [originalCVCRows, setOriginalCVCRows] = useState([]);
  const [editingCVCRows, setEditingCVCRows] = useState({});

  const rowsCVCInEdit = useRef(0);
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const [errorMessages, setErrorMessages] = useState({});

  const [terms, setTerms] = useState(null);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const [columnMap, setColumnMap] = useState([]);
  const [vocabularies, setVocabularies] = useState(null);
  const [newTermDialog, setNewTermDialog] = useState(false);
  const [newVocabularyDialog, setNewVocabularyDialog] = useState(false);
  const [newTerm, newTermDispatch] = useReducer(newTermReducer, {});

  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const dataTable = useRef(null);

  const { authState } = useOktaAuth();
  const obsoleteTerms = useControlledVocabularyService('generic_boolean_terms');
  let vocabularyService = new VocabularyService(authState);

  useQuery("vocabularies",() => vocabularyService.getVocabularies(), {
      onSuccess: (data) => {
          setVocabularies(data.data.results);
      },
      onError: (error) => {
          toast_topleft.current.show([
              { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
          ]);
      }
  });

  useQuery(['vocabterms', tableState],
    () => searchService.search("vocabularyterm", tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setTerms(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
        toast_topleft.current.show([
            { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
        ]);
    },
    keepPreviousData: true
  });

  const mutation = useMutation(updatedTerm => {
      if (!vocabularyService) {
          vocabularyService = new VocabularyService(authState);
      }
      return vocabularyService.saveTerm(updatedTerm);
  });

  const onLazyLoad = (event) => {
    let _tableState = {
      ...tableState,
      rows: event.rows,
      page: event.page,
      first: event.first
    };

    setTableState(_tableState);
  };

  const onFilter = (filtersCopy) => {
    let _tableState = {
      ...tableState,
      filters: { ...filtersCopy }
    };
    setTableState(_tableState);
  };

  const onSort = (event) => {
    let _tableState = {
      ...tableState,
      multiSortMeta: returnSorted(event, tableState.multiSortMeta)
    };
    setTableState(_tableState);
  };

  const setSelectedColumnNames = (newValue) => {
    let _tableState = {
      ...tableState,
      selectedColumnNames: newValue
    };

    setTableState(_tableState);
  };

  const handleNewTerm = (event) => {
    newTermDispatch({ type: "RESET" });
    setNewTermDialog(true);
  };

  const handleNewVocabulary = (event) => {
    setNewVocabularyDialog(true);
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

  const createButtons = () => {
        return (
            <>
            <Button label="New Term" icon="pi pi-plus" onClick={handleNewTerm} />&nbsp;&nbsp;
            <Button label="New Vocabulary" icon="pi pi-plus" onClick={handleNewVocabulary} />
            </>
        );
    };

  const header = (
      <DataTableHeaderFooterTemplate
          title = {"Controlled Vocabulary Terms Table"}
          tableState = {tableState}
          defaultColumnNames = {defaultColumnNames}
          multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
          buttons = {createButtons()}
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

  const onNameEditorValueChange = (props, event) => {
    let updatedTerms = [...props.props.value];
    if (event.target.value || event.target.value === '') {
        updatedTerms[props.rowIndex].name = event.target.value;
        setTerms(updatedTerms);
    }
  };

  const nameEditorTemplate = (props) => {
      return (
          <>
              <InputTextEditor
                  editorChange={onNameEditorValueChange}
                  rowProps={props}
                  fieldName={'name'}
              />
              <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"name"} />
          </>
      );
  };

  const onAbbreviationEditorValueChange = (props, event) => {
      let updatedTerms = [...props.props.value];
      if (event.target.value || event.target.value === '') {
          updatedTerms[props.rowIndex].abbreviation = event.target.value;
          setTerms(updatedTerms);
      }
  };

  const abbreviationEditorTemplate = (props) => {
      return (
          <>
              <InputTextEditor
                  editorChange={onAbbreviationEditorValueChange}
                  rowProps={props}
                  fieldName={'abbreviation'}
              />
              <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"abbreviation"} />
          </>
      );
  };

    const onVocabularyNameEditorValueChange = (props, event) => {
        let updatedTerms = [...props.props.value];
        if (event.value || event.value === '') {
          updatedTerms[props.rowIndex].vocabulary = event.value;
          setTerms(updatedTerms);
        }
    };

  const vocabularyEditorTemplate = (props) => {
      return (
          <>
              <ControlledVocabularyDropdown
                  options={vocabularies}
                  editorChange={onVocabularyNameEditorValueChange}
                  props={props}
                  placeholderText={props.rowData.vocabulary.name}
              />
              <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"vocabulary.name"} />
          </>
      );
  };

  const onDefinitionEditorValueChange =(props, event) => {
      let updatedTerms = [...props.props.value];
      if (event.target.value || event.target.value === '') {
          updatedTerms[props.rowIndex].definition = event.target.value;
          setTerms(updatedTerms);
      }
  };

  const definitionEditorTemplate = (props) => {
      return (
          <>
              <InputTextEditor
                  editorChange={onDefinitionEditorValueChange}
                  rowProps={props}
                  fieldName={'definition'}
              />
              <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"definition"} />
          </>
      );
  };

  const onObsoleteEditorValueChange = (props, event) => {
      let updatedTerms = [...props.props.value];
      if (event.value || event.value === '') {
          updatedTerms[props.rowIndex].obsolete = JSON.parse(event.value.name);
          setTerms(updatedTerms);
      }
  };

  const obsoleteEditorTemplate = (props) => {
      return (
          <>
              <TrueFalseDropdown
                  options={obsoleteTerms}
                  editorChange={onObsoleteEditorValueChange}
                  props={props}
              />
              <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"obsolete"} />
          </>
      );
  };

  const nameBodyTemplate = (rowData) => {
      if (rowData.name) {
          return <div>{rowData.name}</div>;
      }
  };

  const abbreviationBodyTemplate = (rowData) => {
      if (rowData.abbreviation) {
          return <div>{rowData.abbreviation}</div>;
      }
  };

  const vocabularyBodyTemplate = (rowData) => {
      if (rowData.vocabulary && rowData.vocabulary.name) {
          return <div>{rowData.vocabulary.name}</div>;
      }
  };

  const definitionBodyTemplate = (rowData) => {
      if (rowData.definition) {
          return <div>{rowData.definition}</div>;
      }
  };

  const obsoleteBodyTemplate = (rowData) => {
      if (rowData && rowData.obsolete !== null && rowData.obsolete !== undefined) {
          return <div>{JSON.stringify(rowData.obsolete)}</div>;
      }
  };

  const columns = [
    {
      field: "id",
      header: "Id",
      sortable: false
    },
    {
      field: "name",
      header: "Name",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("nameFilter", ["name"]),
      editor: (props) => nameEditorTemplate(props),
      body: nameBodyTemplate
    },
    {
      field: "abbreviation",
      header: "Abbreviation",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("abbreviationFilter", ["abbreviation"]),
      editor: (props) => abbreviationEditorTemplate(props),
      body: abbreviationBodyTemplate
    },
    {
      field: "vocabulary.name",
      header: "Vocabulary",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("vocabularyNameFilter", ["vocabulary.name"]),
      editor: (props) => vocabularyEditorTemplate(props),
      body: vocabularyBodyTemplate
    },
    {
      field: "definition",
      header: "Definition",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("definitionFilter", ["definition"]),
      editor: (props) => definitionEditorTemplate(props),
      body: definitionBodyTemplate
    },
    {
      field: "obsolete",
      header: "Obsolete",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("isObsoleteFilter", ["obsolete"]),
      editor: (props) => obsoleteEditorTemplate(props),
      body: obsoleteBodyTemplate
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
          sortable={col.sortable}
          filter={col.filter}
          showFilterMenu={false}
          filterElement={col.filterElement}
          style={{whiteSpace: 'normal', display: 'inline-grid'}}
          headerClassName='surface-0'
          editor={col.editor}
          body={col.body}
        />;
      })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled]);

  const resetTableState = () => {
    setTableState(initialTableState);
    dataTable.current.state.columnOrder = initialTableState.selectedColumnNames;
  };

  const colReorderHandler = (event) => {
    let _columnNames = [...tableState.selectedColumnNames];
    _columnNames = reorderArray(_columnNames, event.dragIndex, event.dropIndex);
    setSelectedColumnNames(_columnNames);
  };

    const onRowEditCVCInit = (event) => {
        rowsCVCInEdit.current++;
        setIsEnabled(false);
        originalCVCRows[event.index] = { ...terms[event.index] };
        setOriginalCVCRows(originalCVCRows);
        console.log(dataTable.current.state);
    };

    const onRowEditCVCCancel = (event) => {
        rowsCVCInEdit.current--;
        if (rowsCVCInEdit.current === 0) {
            setIsEnabled(true);
        }

        let t = [...terms];
        t[event.index] = originalCVCRows[event.index];
        delete originalCVCRows[event.index];
        setOriginalCVCRows(originalCVCRows);
        setTerms(t);
        const errorMessagesCopy = errorMessages;
        errorMessagesCopy[event.index] = {};
        setErrorMessages({ ...errorMessagesCopy });
    };


    const onRowEditCVCSave = (event) => {//possible to shrink?
        rowsCVCInEdit.current--;
        if (rowsCVCInEdit.current === 0) {
            setIsEnabled(true);
        }
        let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy

        mutation.mutate(updatedRow, {
            onSuccess: (data, variables, context) => {
                console.log(data);
                toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

                let t = [...terms];
                setTerms(t);
                const errorMessagesCopy = errorMessages;
                errorMessagesCopy[event.index] = {};
                setErrorMessages({ ...errorMessagesCopy });
            },
            onError: (error, variables, context) => {
                rowsCVCInEdit.current++;
                setIsEnabled(false);
                toast_topright.current.show([
                    { life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false }
                ]);
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

                setTerms(terms);
                let _editingRows = { ...editingCVCRows, ...{ [`${terms[event.index].id}`]: true } };
                setEditingCVCRows(_editingRows);
            },
            onSettled: (data, error, variables, context) => {

            },
        });
    };

    const onRowEditCVCChange = (event) => {
        setEditingCVCRows(event.data);
    };

  return (
      <div className="card">
        {/*<h3>Controlled Vocabulary Terms Table</h3>*/}
        <Toast ref={toast_topleft} position="top-left" />
        <Toast ref={toast_topright} position="top-right" />
        <Messages ref={errorMessage} />
        <DataTable value={terms} className="p-datatable-sm" header={header} reorderableColumns={isEnabled}
          ref={dataTable} scrollHeight="62vh" scrollable /*scrollable columnResizeMode="expand" responsiveLayout="scroll"*/
          editMode="row" onRowEditInit={onRowEditCVCInit} onRowEditCancel={onRowEditCVCCancel} onRowEditSave={(props) => onRowEditCVCSave(props)}
          editingRows={editingCVCRows} onRowEditChange={onRowEditCVCChange} dataKey="id"
          filterDisplay="row"
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          onColReorder={colReorderHandler}
          first={tableState.first} onFilter={onFilter} filters={tableState.filters}
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
          resizableColumns columnResizeMode="expand" showGridlines
        >
          {columnMap}
          <Column rowEditor headerStyle={{width: '10rem'}} bodyStyle={{textAlign: 'center'}}/>
        </DataTable>
        <NewTermForm
            newTermDialog = {newTermDialog}
            setNewTermDialog = {setNewTermDialog}
            newTerm = {newTerm}
            newTermDispatch = {newTermDispatch}
            vocabularies = {vocabularies}
            obsoleteTerms = {obsoleteTerms}
            vocabularyService = {vocabularyService}
        />
        <NewVocabularyForm
            newVocabularyDialog = {newVocabularyDialog}
            setNewVocabularyDialog = {setNewVocabularyDialog}
        />

      </div>
  );
};
