import React, { useRef, useState, useEffect } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useMutation, useQuery } from 'react-query';
import { useOktaAuth } from '@okta/okta-react';
import { Toast } from 'primereact/toast';

import { trimWhitespace, returnSorted, filterColumns, orderColumns, reorderArray } from '../../utils/utils';
import { AutocompleteEditor } from '../../components/AutocompleteEditor';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { FilterComponentDropDown } from '../../components/FilterComponentDropdown';
import { FilterMultiSelectComponent } from '../../components/FilterMultiSelectComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
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
    'diseaseRelation.name'
  ];

  useQuery(['diseaseAnnotationsAggregations', aggregationFields, tableState],
    () => searchService.search('disease-annotation', 0, 0, null, {}, {}, aggregationFields), {
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

  useQuery(['diseaseAnnotations', tableState],
    () => searchService.search('disease-annotation', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, []), {
    onSuccess: (data) => {
      setDiseaseAnnotations(data.results);
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
      return <>
        <ul style={{ listStyleType: 'none' }}>
          {sortedWithGenes.map((a, index) =>
            <li key={index}>
              <EllipsisTableCell>
                {a.symbol + ' (' + a.curie + ')'}
              </EllipsisTableCell>
            </li>
          )}
        </ul>
      </>;
    }
  };

  const evidenceTemplate = (rowData) => {
    if (rowData && rowData.evidenceCodes) {
      const sortedEvidenceCodes = rowData.evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation) ? 1 : (a.curie === b.curie) ? 1 : -1);
      return (<div>
        <ul style={{ listStyleType: 'none' }}>
          {sortedEvidenceCodes.map((a, index) =>
            <li key={index}>
              <EllipsisTableCell>
                {a.abbreviation + ' - ' + a.name + ' (' + a.curie + ')'}
              </EllipsisTableCell>
            </li>
          )}
        </ul>
      </div>);
    }
  };

  const negatedTemplate = (rowData) => {
    if (rowData && rowData.negated !== null && rowData.negated !== undefined) {
      return <EllipsisTableCell>{JSON.stringify(rowData.negated)}</EllipsisTableCell>;
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
    console.log(event);
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
      onSuccess: (data) => {
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
      return <EllipsisTableCell>{rowData.object.name} ({rowData.object.curie})</EllipsisTableCell>;
    }
  };



  const onDiseaseRelationEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    console.log(updatedAnnotations);
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].diseaseRelation = event.value;//this needs to be fixed. Otherwise, we won't have access to the other subject fields
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
          placeholderText={props.rowData[props.field]}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseRelation"} />
      </>
    );
  };

  const onNegatedEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
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
        <AutocompleteEditor
          autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          endpoint='biologicalentity'
          filterName='subjectFilter'
          fieldName='subject'
          isGene={true}
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
        <AutocompleteEditor
          autocompleteFields={["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms"]}
          rowProps={props}
          searchService={searchService}
          endpoint='doterm'
          filterName='diseaseFilter'
          fieldName='object'
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
          errorField={"object"}
        />
      </>
    );
  };

  const withEditorTemplate = (props) => {
    return (
      <>
        <AutocompleteEditor
          autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
          endpoint='gene'
          filterName='withFilter'
          fieldName='with'
          isWith={true}
          isGene={true}
          isMultiple={true}
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
        <AutocompleteEditor
          autocompleteFields={["curie", "name", "abbreviation"]}
          rowProps={props}
          searchService={searchService}
          setDiseaseAnnotations={setDiseaseAnnotations}
          endpoint='ecoterm'
          filterName='evidenceFilter'
          fieldName='evidenceCodes'
          isMultiple={true}
          otherFilters={{
            obsoleteFilter: {
              "obsolete": {
                queryString: false
              }
            },
            subsetFilter: {
              "subsets": {
                queryString: "agr_eco_terms"
              }
            }
          }} />
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
        return <div className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{
            __html: rowData.subject.symbol + ' (' + rowData.subject.curie + ')'
          }}
        />;
      } else if (rowData.subject.name) {
        return <div className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{
            __html: rowData.subject.name + ' (' + rowData.subject.curie + ')'
          }}
        />;
      } else {
        return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.subject.curie}</div>;
      }
    }
  };

  const uniqueIdBodyTemplate = (rowData) => {
    return (
      <EllipsisTableCell>
        {rowData.uniqueId}
      </EllipsisTableCell>
    )
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

  const FilterComponentDropDownTemplate = (filterName, field, options, optionField) => {
    return (<FilterComponentDropDown
      isEnabled={isEnabled}
      field={field}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
      options={options}
      optionField={optionField}
    />);
  }

  const FilterMultiSelectComponentTemplate = (filterName, field) => {
    return (<FilterMultiSelectComponent
      isEnabled={isEnabled}
      field={field}
      filterName={filterName}
      currentFilters={tableState.filters}
      onFilter={onFilter}
      aggregationFields={aggregationFields}
      tableState={tableState}
    />);
  }

  const columns = [{
    field: "uniqueId",
    header: "Unique Id",
    sortable: isEnabled,
    filter: true,
    body: uniqueIdBodyTemplate,
    filterElement: filterComponentInputTextTemplate("uniqueidFilter", ["uniqueId"])
  },
  {
    field: "subject.symbol",
    header: "Subject",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("subjectFilter", ["subject.symbol", "subject.name", "subject.curie"]),
    editor: (props) => subjectEditorTemplate(props),
    body: subjectBodyTemplate,
  },
  {
    field: "diseaseRelation.name",
    header: "Disease Relation",
    sortable: isEnabled,
    filter: true,
    filterElement: FilterMultiSelectComponentTemplate("diseaseRelationFilter", "diseaseRelation.name"),
    editor: (props) => diseaseRelationEditor(props)
  },
  {
    field: "negated",
    header: "Negated",
    body: negatedTemplate,
    filter: true,
    filterElement: FilterComponentDropDownTemplate("negatedFilter", "negated", [{ text: "true" }, { text: "false" }], "text"),
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
    field: "singleReference.curie",
    header: "Reference",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("singleReferenceFilter", ["singleReference.curie"])
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
          style={{ width: `${100 / orderedColumns.length}%` }}
          className='overflow-hidden text-overflow-ellipsis'
          key={col.field}
          columnKey={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          showFilterMenu={false}
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
          style={{ width: '20%' }}
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
        <DataTable value={diseaseAnnotations} header={header} reorderableColumns={isEnabled} ref={dataTable}
          tableClassName='w-12 p-datatable-md'
          editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
          onColReorder={colReorderHandler}
          editingRows={editingRows} onRowEditChange={onRowEditChange}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
          first={tableState.first}
          filterDisplay="row"
          dataKey="id" resizableColumns columnResizeMode="expand" showGridlines
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
