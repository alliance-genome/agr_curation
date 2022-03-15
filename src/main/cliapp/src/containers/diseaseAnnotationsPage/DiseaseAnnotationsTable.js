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
import { RelatedNotesDialog } from './RelatedNotesDialog';

import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ControlledVocabularyMultiSelectDropdown } from '../../components/ControlledVocabularyMultiSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { MultiSelect } from 'primereact/multiselect';
import { Button } from 'primereact/button';

export const DiseaseAnnotationsTable = () => {
  const defaultColumnNames = ["Unique Id", "Subject", "Disease Relation", "Negated", "Disease", "Reference", "With", "Evidence Code", "Genetic Sex", "Disease Qualifiers",
    "SGD Strain Background", "Annotation Type", "Genetic Modifier Relation", "Genetic Modifier", "Data Provider", "Secondary Data Provider", "Modified By", "Date Last Modified", "Created By", "Creation Date", "Related Notes"];
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
  const [relatedNotesDialog, setRelatedNotesDialog] = useState(false);
  const [relatedNotes, setRelatedNotes] = useState(false);

  const diseaseRelationsTerms = useControlledVocabularyService('Disease Relation Vocabulary');
  const geneticSexTerms = useControlledVocabularyService('Genetic sexes');
  const annotationTypeTerms = useControlledVocabularyService('Annotation types')
  const negatedTerms = useControlledVocabularyService('generic_boolean_terms');
  const geneticModifierRelationTerms = useControlledVocabularyService('Disease genetic modifier relations');
  const diseaseQualifiersTerms = useControlledVocabularyService('Disease qualifiers');

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
    'with.symbol': ['with.name', 'with.curie'],
    'sgdStrainBackground.name': ['sgdStrainBackground.curie'],
    'diseaseGeneticModifier.symbol': ['diseaseGeneticModifer.name', 'diseaseGeneticModifier.curie']
  };

  const aggregationFields = [
    'diseaseRelation.name', 'geneticSex.name', 'annotationType.name', 'diseaseGeneticModifierRelation.name', 'diseaseQualifiers.name'
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

  const handleRelatedNotesOpen = (event, rowData) => {
    setRelatedNotes(rowData.relatedNotes);
    setRelatedNotesDialog(true);
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

  const diseaseQualifiersBodyTemplate = (rowData) => {
    if (rowData && rowData.diseaseQualifiers) {
      const sortedDiseaseQualifiers = rowData.diseaseQualifiers.sort((a, b) => (a.name > b.name) ? 1 : -1);
      return (<div>
        <ul stype={{ listStypeType: 'none' }}>
          {sortedDiseaseQualifiers.map((a, index) =>
            <li key={index}>
              <EllipsisTableCell>
                {a.name}
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

  const relatedNotesTemplate = (rowData) => {
    if (rowData.relatedNotes) {
      return <EllipsisTableCell><button
        style={{
          color: 'blue',
          background: 'none',
          border: 'none',
          cursor: 'pointer',
        }}
        onClick={(event) => { handleRelatedNotesOpen(event, rowData) }} ><span style={{ textDecoration: 'underline' }}>{`Notes(${rowData.relatedNotes.length})`}</span></button></EllipsisTableCell>;
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
      updatedAnnotations[props.rowIndex].diseaseRelation = event.value;
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
          placeholderText={props.rowData.diseaseRelation.name}
          showClear={false}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseRelation"} />
      </>
    );
  };

  const onGeneticSexEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    console.log(updatedAnnotations);
    updatedAnnotations[props.rowIndex].geneticSex = event.value;
    setDiseaseAnnotations(updatedAnnotations);
  };

  const geneticSexEditor = (props) => {
    let placeholderText = '';
    if (props.rowData.geneticSex) {
      placeholderText = props.rowData.geneticSex.name;
    }
    return (
      <>
        <ControlledVocabularyDropdown
          options={geneticSexTerms}
          editorChange={onGeneticSexEditorValueChange}
          props={props}
          placeholderText={placeholderText}
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"geneticSex"} />
      </>
    );
  };

  const onAnnotationTypeEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    console.log(updatedAnnotations);
    updatedAnnotations[props.rowIndex].annotationType = event.value;
    setDiseaseAnnotations(updatedAnnotations);
  };

  const annotationTypeEditor = (props) => {
    let placeholderText = '';
    if (props.rowData.annotationType) {
      placeholderText = props.rowData.annotationType.name;
    }
    return (
      <>
        <ControlledVocabularyDropdown
          options={annotationTypeTerms}
          editorChange={onAnnotationTypeEditorValueChange}
          props={props}
          placeholderText={placeholderText}
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"annotationType"} />
      </>
    );
  };

  const onGeneticModifierRelationEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    console.log(updatedAnnotations);
    updatedAnnotations[props.rowIndex].diseaseGeneticModifierRelation = event.value;
    setDiseaseAnnotations(updatedAnnotations);
  };

  const geneticModifierRelationEditor = (props) => {
    let placeholderText = '';
    if (props.rowData.diseaseGeneticModifierRelation) {
      placeholderText = props.rowData.diseaseGeneticModifierRelation.name;
    }
    return (
      <>
        <ControlledVocabularyDropdown
          options={geneticModifierRelationTerms}
          editorChange={onGeneticModifierRelationEditorValueChange}
          props={props}
          placeholderText={placeholderText}
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseGeneticModifierRelation"} />
      </>
    );
  };

  const onDiseaseQualifiersEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].diseaseQualifiers = event.value;
      setDiseaseAnnotations(updatedAnnotations);
    }
  };

  const diseaseQualifiersEditor = (props) => {
    let placeholderText = '';
    if (props.rowData.diseaseQualifiers) {
      let placeholderTextElements = [];
      props.rowData.diseaseQualifiers.forEach((x, i) =>
        placeholderTextElements.push(x.name));
      placeholderText = placeholderTextElements.join();

    }
    return (
      <>
        <ControlledVocabularyMultiSelectDropdown
          options={diseaseQualifiersTerms}
          editorChange={onDiseaseQualifiersEditorValueChange}
          props={props}
          placeholderText={placeholderText}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseQualifiers"} />
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

  const sgdStrainBackgroundEditorTemplate = (props) => {
    return (
      <>
        <AutocompleteEditor
          autocompleteFields={["name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          endpoint='agm'
          filterName='sgdStrainBackgroundFilter'
          fieldName='sgdStrainBackground'
          isGene={true}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField={"sgdStrainBackground"}
        />
      </>
    );
  };

  const geneticModifierEditorTemplate = (props) => {
    return (
      <>
        <AutocompleteEditor
          autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
          rowProps={props}
          searchService={searchService}
          endpoint='biologicalentity'
          filterName='geneticModifierFilter'
          fieldName='diseaseGeneticModifier'
          isGene={true}
        />
        <ErrorMessageComponent
          errorMessages={errorMessages[props.rowIndex]}
          errorField={"diseaseGeneticModifier"}
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

  const sgdStrainBackgroundBodyTemplate = (rowData) => {
    if (rowData.sgdStrainBackground) {
      if (rowData.sgdStrainBackground.name) {
        return <div className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{
            __html: rowData.sgdStrainBackground.name + ' (' + rowData.sgdStrainBackground.curie + ')'
          }}
        />;
      } else {
        return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.sgdStrainBackground.curie}</div>;
      }
    }
  };

  const geneticModifierBodyTemplate = (rowData) => {
    if (rowData.diseaseGeneticModifier) {
      if (rowData.diseaseGeneticModifier.symbol) {
        return <div className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{
            __html: rowData.diseaseGeneticModifier.symbol + ' (' + rowData.diseaseGeneticModifier.curie + ')'
          }}
        />;
      } else if (rowData.diseaseGeneticModifier.name) {
        return <div className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{
            __html: rowData.diseaseGeneticModifier.name + ' (' + rowData.diseaseGeneticModifier.curie + ')'
          }}
        />;
      } else {
        return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.diseaseGeneticModifier.curie}</div>;
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
  },
  {
    field: "relatedNotes.freeText",
    header: "Related Notes",
    body: relatedNotesTemplate,
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("relatedNotesFilter", ["relatedNotes.freeText"])
  },
  {
    field: "geneticSex.name",
    header: "Genetic Sex",
    sortable: isEnabled,
    filter: true,
    filterElement: FilterMultiSelectComponentTemplate("geneticSexFilter", "geneticSex.name"),
    editor: (props) => geneticSexEditor(props)
  },
  {
    field: "diseaseQualifiers.name",
    header: "Disease Qualifiers",
    sortable: isEnabled,
    filter: true,
    filterElement: FilterMultiSelectComponentTemplate("diseaseQualifiersFilter", "diseaseQualifiers.name"),
    editor: (props) => diseaseQualifiersEditor(props),
    body: diseaseQualifiersBodyTemplate
  },
  {
    field: "sgdStrainBackground.name",
    header: "SGD Strain Background",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("sgdStrainBackgroundFilter", ["sgdStrainBackground.name", "sgdStrainBackground.curie"]),
    editor: (props) => sgdStrainBackgroundEditorTemplate(props),
    body: sgdStrainBackgroundBodyTemplate
  },
  {
    field: "annotationType.name",
    header: "Annotation Type",
    sortable: isEnabled,
    filter: true,
    filterElement: FilterMultiSelectComponentTemplate("annotationTypeFilter", "annotationType.name"),
    editor: (props) => annotationTypeEditor(props)
  },
  {
    field: "diseaseGeneticModifierRelation.name",
    header: "Genetic Modifier Relation",
    sortable: isEnabled,
    filter: true,
    filterElement: FilterMultiSelectComponentTemplate("geneticModifierRelationFilter", "diseaseGeneticModifierRelation.name"),
    editor: (props) => geneticModifierRelationEditor(props)
  },
  {
    field: "diseaseGeneticModifier.symbol",
    header: "Genetic Modifier",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("geneticModifierFilter", ["diseaseGeneticModifier.symbol", "diseaseGeneticModifier.name", "diseaseGeneticModifier.curie"]),
    editor: (props) => geneticModifierEditorTemplate(props),
    body: geneticModifierBodyTemplate
  },
  {
    field: "dataProvider",
    header: "Data Provider",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("dataProviderFilter", ["dataProvider"])
  },
  {
    field: "secondaryDataProvider",
    header: "Secondary Data Provider",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("secondaryDataProviderFilter", ["secondaryDataProvider"])
  },
  {
    field: "modifiedBy",
    header: "Modified By",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("modifiedByFilter", ["modifiedBy"])
  },
  {
    field: "dateLastModified",
    header: "Date Last Modified",
    filter: false
  },
  {
    field: "createdBy",
    header: "Created By",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("createdByFilter", ["createdBy"])
  },
  {
    field: "creationDate",
    header: "Creation Date",
    filter: false
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
      <RelatedNotesDialog
        relatedNotes={relatedNotes}
        relatedNotesDialog={relatedNotesDialog}
        setRelatedNotesDialog={setRelatedNotesDialog}
      />
    </div>
  );
};
