import React, { useRef, useState, useEffect } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useMutation, useQuery } from 'react-query';
import { useOktaAuth } from '@okta/okta-react';
import { Toast } from 'primereact/toast';

import { trimWhitespace, returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { SubjectEditor } from './SubjectEditor';
import { DiseaseEditor } from './DiseaseEditor';
import { WithEditor } from './WithEditor';
import { EvidenceEditor } from './EvidenceEditor';
import { FilterComponent } from '../../components/FilterComponent';
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { FilterComponentDropDown } from '../../components/FilterComponentDropdown';
import { SearchService } from '../../service/SearchService';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';

import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { MultiSelect } from 'primereact/multiselect';
import { Button } from 'primereact/button';

export const DiseaseAnnotationsTable = () => {
  const defaultColumnNames = ["Unique Id", "Subject", "Disease Relation", "Negated", "Disease", "Reference", "With", "Evidence Code"];
  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnNames,
    filters: {},
  }

  const [tableState, setTableState] = useSessionStorage("DATableSettings", initialTableState);

  let [diseaseAnnotations, setDiseaseAnnotations] = useState(null);
  let [tableAggregations, setTableAggregations] = useState(null);

  const [totalRecords, setTotalRecords] = useState(0);
  const [originalRows, setOriginalRows] = useState([]);
  const [editingRows, setEditingRows] = useState({});
  const [columnMap, setColumnMap] = useState([]);
  const [isEnabled, setIsEnabled] = useState(true); //needs better name

  const diseaseRelationsTerms = useControlledVocabularyService('Disease Relation Vocabulary');
  const negatedTerms = useControlledVocabularyService('generic_boolean_terms');

  const [errorMessages, setErrorMessages] = useState({});
  const { authState } = useOktaAuth();


  const searchService = new SearchService();

  const rowsInEdit = useRef(0);
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const dataTable = useRef(null);

  let diseaseAnnotationService = null;

  const sortMapping = {
    'object.name': ['object.curie', 'object.namespace'],
    'subject.symbol': ['subject.name', 'subject.curie'],
    'with.symbol': ['with.name', 'with.curie']
  };

  const aggregationFields = [
    'object.name', 'subject.name', 'with.name'
  ];

  useQuery(['diseaseAnnotations', tableState],
    () => searchService.search('disease-annotation', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, aggregationFields), {
    onSuccess: (data) => {

      setDiseaseAnnotations(data.results);
      setTotalRecords(data.totalResults);
      setTableAggregations(data.aggregations);
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
  }
  );

  const mutation = useMutation(updatedAnnotation => {
    if (!diseaseAnnotationService) {
      diseaseAnnotationService = new DiseaseAnnotationService(authState);
    }
    return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
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

  const withTemplate = (rowData) => {
    if (rowData && rowData.with) {
      const sortedWithGenes = rowData.with.sort((a, b) => (a.symbol > b.symbol) ? 1 : (a.curie === b.curie) ? 1 : -1);
      return <div>
        <ul style={{ listStyleType: 'none' }}>
          {sortedWithGenes.map((a, index) => <li key={index}>{a.symbol + ' (' + a.curie + ')'}</li>)}
        </ul>
      </div>;
    }
  };

  const evidenceTemplate = (rowData) => {
    if (rowData && rowData.evidenceCodes) {
      const sortedEvidenceCodes = rowData.evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation) ? 1 : (a.curie === b.curie) ? 1 : -1);
      return (<div>
        <ul style={{ listStyleType: 'none' }}>
          {sortedEvidenceCodes.map((a, index) =>
            <li key={index}>{a.abbreviation + ' - ' + a.name + ' (' + a.curie + ')'}</li>
          )}
        </ul>
      </div>);
    }
  };

  const negatedTemplate = (rowData) => {
    if (rowData && rowData.negated !== null && rowData.negated !== undefined) {
      return <div>{JSON.stringify(rowData.negated)}</div>;
    }
  };

  const onRowEditInit = (event) => {
    rowsInEdit.current++;
    setIsEnabled(false);
    originalRows[event.index] = { ...diseaseAnnotations[event.index] };
    setOriginalRows(originalRows);
    console.log(dataTable.current.state);
  };

  const onRowEditCancel = (event) => {
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    };

    let annotations = [...diseaseAnnotations];
    annotations[event.index] = originalRows[event.index];
    delete originalRows[event.index];
    setOriginalRows(originalRows);
    setDiseaseAnnotations(annotations);
    const errorMessagesCopy = errorMessages;
    errorMessagesCopy[event.index] = {};
    setErrorMessages({ ...errorMessagesCopy });

  };


  const onRowEditSave = (event) => {//possible to shrink?
    rowsInEdit.current--;
    if (rowsInEdit.current === 0) {
      setIsEnabled(true);
    }
    let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy
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


    mutation.mutate(updatedRow, {
      onSuccess: (data, variables, context) => {
        console.log(data);
        toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

        let annotations = [...diseaseAnnotations];
        annotations[event.index].subject = data.data.entity.subject;
        annotations[event.index].object = data.data.entity.object;
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



        setDiseaseAnnotations(annotations);
        let _editingRows = { ...editingRows, ...{ [`${annotations[event.index].id}`]: true } };
        setEditingRows(_editingRows);
      },
      onSettled: (data, error, variables, context) => {

      },
    });
  };

  const onRowEditChange = (event) => {
    setEditingRows(event.data);
  };


  const diseaseBodyTemplate = (rowData) => {
    if (rowData.object) {
      return <div>{rowData.object.name} ({rowData.object.curie})</div>;
    }
  };



  const onDiseaseRelationEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.value];
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].diseaseRelation = event.value.name;//this needs to be fixed. Otherwise, we won't have access to the other subject fields
      setDiseaseAnnotations(updatedAnnotations);
    }
  };

  const diseaseRelationEditor = (props) => {

    return (
      <>
        <ControlledVocabularyDropdown
          options={diseaseRelationsTerms}
          editorChange={onDiseaseRelationEditorValueChange}
          props={props}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseRelation"} />
      </>
    );
  };

  const onNegatedEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.value];
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].negated = JSON.parse(event.value.name);
      setDiseaseAnnotations(updatedAnnotations);
    }
  };

  const negatedEditor = (props) => {
    return (
      <>
        <TrueFalseDropdown
          options={negatedTerms}
          editorChange={onNegatedEditorValueChange}
          props={props}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"negated"} />
      </>
    );
  };

  const subjectEditorTemplate = (props) => {
    return (
      <>
        <SubjectEditor
          autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField={"subject"}
        />
      </>
    );
  };

  const diseaseEditorTemplate = (props) => {
    return (
      <>
        <DiseaseEditor
          autocompleteFields={["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField={"object"}
        />
      </>
    );
  };

  const withEditorTemplate = (props) => {
    return (
      <>
        <WithEditor
          autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField="with"
        />
      </>
    );
  };

  const evidenceEditorTemplate = (props) => {
    return (
      <>
        <EvidenceEditor
          autocompleteFields={["curie", "name", "abbreviation"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField="evidence"
        />
      </>
    );
  };

  const subjectBodyTemplate = (rowData) => {
    if (rowData.subject) {
      if (rowData.subject.symbol) {
        return <div dangerouslySetInnerHTML={{ __html: rowData.subject.symbol + ' (' + rowData.subject.curie + ')' }} />;
      } else if (rowData.subject.name) {
        return <div dangerouslySetInnerHTML={{ __html: rowData.subject.name + ' (' + rowData.subject.curie + ')' }} />;
      } else {
        return <div>{rowData.subject.curie}</div>;
      }
    }
  };

  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponent
      isEnabled={isEnabled}
      fields={fields}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
    />);
  };

  const filterComponentInputTextTemplate = (filterName, fields, tokenOperator="AND") => {
        return (<FilterComponentInputText
            isEnabled={isEnabled}
            fields={fields}
            filterName={filterName}
            currentFilters={tableState.filters}
            onFilter={onFilter}
            tokenOperator={tokenOperator}
        />);
   };

  const FilterComponentDropDownTemplate = (filterName, field, tokenOperator="OR", options, optionField) => {
      return (<FilterComponentDropDown
          isEnabled={isEnabled}
          field={field}
          filterName={filterName}
          currentFilters={tableState.filters}
          onFilter={onFilter}
          options={options}
          tokenOperator = {tokenOperator}
          optionField = {optionField}
      />);
  }

  const columns = [
    {
      field: "uniqueId",
      header: "Unique Id",
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("uniqueidFilter", ["uniqueId"])
    },
    {
      field: "subject.symbol",
      header: "Subject",
      sortable: isEnabled,
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      filter: true,
      filterElement: filterComponentInputTextTemplate("subjectFilter", ["subject.symbol", "subject.name", "subject.curie"]),
      editor: (props) => subjectEditorTemplate(props),
      body: subjectBodyTemplate,
    },
    {
      field: "diseaseRelation",
      header: "Disease Relation",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("diseaseRelationFilter", ["diseaseRelation"]),
      editor: (props) => diseaseRelationEditor(props)
    },
    {
      field: "negated",
      header: "Negated",
      body: negatedTemplate,
      filter: true,
      filterElement: FilterComponentDropDownTemplate("negatedFilter", "negated", "OR", [{text : "true"} , { text : "false"}], "text"),
      sortable: isEnabled,
      editor: (props) => negatedEditor(props)
    },
    {
      field: "object.name",
      header: "Disease",
      sortable: { isEnabled },
      filter: true,
      filterElement: filterComponentInputTextTemplate("objectFilter", ["object.curie", "object.name"]),
      editor: (props) => diseaseEditorTemplate(props),
      body: diseaseBodyTemplate
    },
    {
      field: "reference.curie",
      header: "Reference",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("referenceFilter", ["reference.curie"])
    },
    {
      field: "evidenceCodes.abbreviation",
      header: "Evidence Code",
      body: evidenceTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("evidenceCodesFilter", ["evidenceCodes.curie", "evidenceCodes.name", "evidenceCodes.abbreviation"]),
      editor: (props) => evidenceEditorTemplate(props)
    },
    {
      field: "with.symbol",
      header: "With",
      body: withTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentInputTextTemplate("withFilter", ["with.symbol", "with.name", "with.curie"]),
      editor: (props) => withEditorTemplate(props)
    }
  ];

  useEffect(() => {
    const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
    const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
    setColumnMap(
      orderedColumns.map((col) => {
        return <Column
          key={col.field}
          columnKey={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          filter={col.filter}
          filterElement={col.filterElement}
          editor={col.editor}
          body={col.body}
        />;
      })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tableState, isEnabled]);

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
        <Button disabled={!isEnabled} onClick={(event) => resetTableState(event)}>Reset Table</Button>
      </div>
    </>
  );

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
        <Toast ref={toast_topleft} position="top-left" />
        <Toast ref={toast_topright} position="top-right" />
        <h3>Disease Annotations Table</h3>
        <DataTable value={diseaseAnnotations} className="p-datatable-md" header={header} reorderableColumns={isEnabled}
          ref={dataTable}
          editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
          onColReorder={colReorderHandler}
          editingRows={editingRows} onRowEditChange={onRowEditChange}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          first={tableState.first}
          dataKey="id" resizableColumns columnResizeMode="fit" showGridlines
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[1, 10, 20, 50, 100, 250, 1000]}
        >

          {columnMap}

          <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
        </DataTable>
      </div>
    </div>
  );
};
