import React, { useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';

import { AutocompleteEditor } from '../../components/AutocompleteEditor';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { FilterComponentDropDown } from '../../components/FilterComponentDropdown';
import { FilterMultiSelectComponent } from '../../components/FilterMultiSelectComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { SearchService } from '../../service/SearchService';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { RelatedNotesDialog } from './RelatedNotesDialog';
import { ConditionRelationsDialog } from './ConditionRelationsDialog';

import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ControlledVocabularyMultiSelectDropdown } from '../../components/ControlledVocabularyMultiSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { Button } from 'primereact/button';
import { Tooltip } from 'primereact/tooltip';

export const DiseaseAnnotationsTable = () => {
  const defaultColumnOptions = [
    "Unique ID", "MOD Entity ID", "Subject", "Disease Relation", "Negated", "Disease","Evidence Code", "With", "Reference", "Experimental Conditions",
    "Genetic Sex", "Disease Qualifiers", "SGD Strain Background", "Annotation Type", "Genetic Modifier Relation","Genetic Modifier", "Related Notes",
    "Data Provider", "Secondary Data Provider", "Modified By", "Date Updated", "Created By", "Date Created", "Internal"
  ];

  let initialTableState = {
    page: 0,
    first: 0,
    rows: 50,
    multiSortMeta: [],
    selectedColumnNames: defaultColumnOptions,
    filters: {},
    isFirst: true
  }

  const [tableState, setTableState] = useSessionStorage("DATableSettings", initialTableState);

  const [isEnabled, setIsEnabled] = useState(true); //needs better name
  const [conditionRelationsDialog, setConditionRelationsDialog] = useState(false);
  const [conditionRelations, setConditionRelations] = useState(false);
  const [relatedNotesData, setRelatedNotesData] = useState({
    relatedNotes: [],
    isInEdit: false,
    dialog: false,
    rowIndex: null,
    mainRowProps: {},
  });

  const diseaseRelationsTerms = useControlledVocabularyService('Disease Relation Vocabulary');
  const geneticSexTerms = useControlledVocabularyService('Genetic sexes');
  const annotationTypeTerms = useControlledVocabularyService('Annotation types')
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const geneticModifierRelationTerms = useControlledVocabularyService('Disease genetic modifier relations');
  const diseaseQualifiersTerms = useControlledVocabularyService('Disease qualifiers');

  const [errorMessages, setErrorMessages] = useState({});

  const searchService = new SearchService();

  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);

  let diseaseAnnotationService = null;

  const sortMapping = {
    'object.name': ['object.curie', 'object.namespace'],
    'subject.symbol': ['subject.name', 'subject.curie'],
    'with.symbol': ['with.name', 'with.curie'],
    'sgdStrainBackground.name': ['sgdStrainBackground.curie'],
    'diseaseGeneticModifier.symbol': ['diseaseGeneticModifier.name', 'diseaseGeneticModifier.curie']
  };

  const aggregationFields = [
    'diseaseRelation.name', 'geneticSex.name', 'annotationType.name', 'diseaseGeneticModifierRelation.name', 'diseaseQualifiers.name'
  ];

  const mutation = useMutation(updatedAnnotation => {
    if (!diseaseAnnotationService) {
      diseaseAnnotationService = new DiseaseAnnotationService();
    }
    return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
  });

  const onFilter = (filtersCopy) => {
    let _tableState = {
      ...tableState,
      filters: { ...filtersCopy }
    }
    setTableState(_tableState);
  };

  const handleRelatedNotesOpen = (event, rowData, isInEdit) => {
    let _relatedNotesData = {};
    _relatedNotesData["originalRelatedNotes"] = rowData.relatedNotes;
    _relatedNotesData["dialog"] = true;
    _relatedNotesData["isInEdit"] = isInEdit;
    setRelatedNotesData(() => ({
      ..._relatedNotesData
    }));
  };

  const handleRelatedNotesOpenInEdit = (event, rowProps, isInEdit) => {
    let _relatedNotesData = {};
    _relatedNotesData["originalRelatedNotes"] = rowProps.rowData.relatedNotes;
    _relatedNotesData["dialog"] = true;
    _relatedNotesData["isInEdit"] = isInEdit;
    _relatedNotesData["rowIndex"] = rowProps.rowIndex;
    _relatedNotesData["mainRowProps"] = rowProps;
    setRelatedNotesData(() => ({
      ..._relatedNotesData
    }));
  };

  const handleConditionRelationsOpen = (event, rowData) => {
    setConditionRelations(rowData.conditionRelations);
    setConditionRelationsDialog(true);
  };

  const withTemplate = (rowData) => {
    if (rowData && rowData.with) {
      const sortedWithGenes = rowData.with.sort((a, b) => (a.symbol > b.symbol) ? 1 : (a.curie === b.curie) ? 1 : -1);
      const listTemplate = (item) => {
        return (
          <EllipsisTableCell>
            {item.symbol + ' (' + item.curie + ')'}
          </EllipsisTableCell>
        );
      };
      return <ListTableCell template={listTemplate} listData={sortedWithGenes}/>
    }
  };


  const evidenceTemplate = (rowData) => {
    if (rowData && rowData.evidenceCodes) {
      const sortedEvidenceCodes = rowData.evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation) ? 1 : (a.curie === b.curie) ? 1 : -1);
      const listTemplate = (item) => {
        return (
          <EllipsisTableCell>
            {item.abbreviation + ' - ' + item.name + ' (' + item.curie + ')'}
          </EllipsisTableCell>
        );
      };
      return (
        <>
          <div className={`a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`}>
            <ListTableCell template={listTemplate} listData={sortedEvidenceCodes}/>
          </div>
          <Tooltip target={`.a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
            <ListTableCell template={listTemplate} listData={sortedEvidenceCodes}/>
          </Tooltip>
        </>
      );
    }
  };

  const diseaseQualifiersBodyTemplate = (rowData) => {
    if (rowData && rowData.diseaseQualifiers) {
      const sortedDiseaseQualifiers = rowData.diseaseQualifiers.sort((a, b) => (a.name > b.name) ? 1 : -1);
      const listTemplate = (item) => item.name;
      return <ListTableCell template={listTemplate} listData={sortedDiseaseQualifiers}/>
    }
  };

  const negatedTemplate = (rowData) => {
    if (rowData && rowData.negated !== null && rowData.negated !== undefined) {
      return <EllipsisTableCell>{JSON.stringify(rowData.negated)}</EllipsisTableCell>;
    }
  };
  
  const internalTemplate = (rowData) => {
    if (rowData && rowData.internal !== null && rowData.internal !== undefined) {
      return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
    }
  };

  const relatedNotesTemplate = (rowData) => {
    if (rowData?.relatedNotes) {
      return (
        <Button className="p-button-text"
          onClick={(event) => { handleRelatedNotesOpen(event, rowData, false) }} >
          <span style={{ textDecoration: 'underline' }}>
            {`Notes(${rowData.relatedNotes.length})`}
          </span>
        </Button>
      )
    }
  };

  const relatedNotesEditor = (props) => {
    if (props?.rowData?.relatedNotes) {
      return (
        <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, props, true) }} >
            <span style={{ textDecoration: 'underline' }}>
              {`Notes(${props.rowData.relatedNotes.length}) `}
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',  }}/>
            <span className="exclamation-icon" data-pr-tooltip="Edits made to this field will only be saved to the database once the entire annotation is saved.">
              <i className="pi pi-exclamation-circle" style={{ 'fontSize': '1em' }}></i>
            </span>
          </Button>
        </div>
          <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"relatedNotes.freeText"} style={{ 'fontSize': '1em' }}/>
        </>
      )
    } else {
      return (
        <>
          <div>
            <Button className="p-button-text"
              onClick={(event) => { handleRelatedNotesOpenInEdit(event, props, true) }} >
              <span style={{ textDecoration: 'underline' }}>
                Add Note
                <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
              </span>&nbsp;&nbsp;&nbsp;&nbsp;
              <Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',  }}/>
              <span className="exclamation-icon" data-pr-tooltip="Edits made to this field will only be saved to the database once the entire annotation is saved.">
                <i className="pi pi-exclamation-circle" style={{ 'fontSize': '1em' }}></i>
              </span>
            </Button>
          </div>
          <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"relatedNotes.freeText"} style={{ 'fontSize': '1em' }}/>
        </>
      )
    }
  };

  const conditionRelationsTemplate = (rowData) => {
    if (rowData.conditionRelations) {
        const handle = rowData.conditionRelations[0].handle
      return <EllipsisTableCell>
        <Button className="p-button-raised p-button-text"
          onClick={(event) => { handleConditionRelationsOpen(event, rowData) }} >
          <span style={{ textDecoration: 'underline' }}>
            {!handle && `Conditions (${rowData.conditionRelations.length})`}
              {handle && handle}
          </span>
        </Button>
      </EllipsisTableCell>;
    }
  };

  const diseaseBodyTemplate = (rowData) => {
    if (rowData.object) {
      return (
        <>
          <EllipsisTableCell otherClasses={`a${rowData.object.curie.replace(':', '')}`}>{rowData.object.name} ({rowData.object.curie})</EllipsisTableCell>
          <Tooltip target={`.a${rowData.object.curie.replace(':', '')}`} content={`${rowData.object.name} (${rowData.object.curie})`} />
        </>
      )
    }
  };

  const onDiseaseRelationEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].diseaseRelation = event.value;
    }
  };

  const diseaseRelationEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="diseaseRelation"
          options={diseaseRelationsTerms}
          editorChange={onDiseaseRelationEditorValueChange}
          props={props}
          showClear={false}
          placeholderText={props.rowData.diseaseRelation.name}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseRelation"} />
      </>
    );
  };

  const onGeneticSexEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    updatedAnnotations[props.rowIndex].geneticSex = event.value;
  };

  const geneticSexEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="geneticSex"
          options={geneticSexTerms}
          editorChange={onGeneticSexEditorValueChange}
          props={props}
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"geneticSex"} />
      </>
    );
  };

  const onAnnotationTypeEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    updatedAnnotations[props.rowIndex].annotationType = event.value;
  };

  const annotationTypeEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="annotationType"
          options={annotationTypeTerms}
          editorChange={onAnnotationTypeEditorValueChange}
          props={props}
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"annotationType"} />
      </>
    );
  };

  const onGeneticModifierRelationEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    updatedAnnotations[props.rowIndex].diseaseGeneticModifierRelation = event.value;
  };

  const geneticModifierRelationEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="diseaseGeneticModifierRelation"
          options={geneticModifierRelationTerms}
          editorChange={onGeneticModifierRelationEditorValueChange}
          props={props}
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
    }
  };

  const negatedEditor = (props) => {
    return (
      <>
        <TrueFalseDropdown
          options={booleanTerms}
          editorChange={onNegatedEditorValueChange}
          props={props}
          field={"negated"}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"negated"} />
      </>
    );
  };
  
  const onInternalEditorValueChange = (props, event) => {
    let updatedAnnotations = [...props.props.value];
    if (event.value || event.value === '') {
      updatedAnnotations[props.rowIndex].internal = JSON.parse(event.value.name);
    }
  };

  const internalEditor = (props) => {
    return (
      <>
        <TrueFalseDropdown
          options={booleanTerms}
          editorChange={onInternalEditorValueChange}
          props={props}
          field={"internal"}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
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
          isSubject={true}
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
          isSgdStrainBackground={true}
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
          isSubject={true}
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
          endpoint='gene'
          filterName='withFilter'
          fieldName='with'
          isWith={true}
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
        return (
          <>
            <div className={`overflow-hidden text-overflow-ellipsis a${rowData.subject.curie.replace(':', '')}`}
              dangerouslySetInnerHTML={{
                __html: rowData.subject.symbol + ' (' + rowData.subject.curie + ')'
              }}
            />
            <Tooltip target={`.a${rowData.subject.curie.replace(':', '')}`}>
              <div dangerouslySetInnerHTML={{
                __html: rowData.subject.symbol + ' (' + rowData.subject.curie + ')'
              }}
              />
            </Tooltip>
          </>
        )
      } else if (rowData.subject.name) {
        return (
          <>
            <div className={`overflow-hidden text-overflow-ellipsis a${rowData.subject.curie.replace(':', '')}`}
              dangerouslySetInnerHTML={{
                __html: rowData.subject.name + ' (' + rowData.subject.curie + ')'
              }}
            />
            <Tooltip target={`.a${rowData.subject.curie.replace(':', '')}`}>
              <div dangerouslySetInnerHTML={{
                __html: rowData.subject.name + ' (' + rowData.subject.curie + ')'
                }}
              />
            </Tooltip>
          </>
        )
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
      //the 'a' at the start is a hack since css selectors can't start with a number
      <>
        <EllipsisTableCell otherClasses={`a${rowData.id}`}>
          {rowData.uniqueId}
        </EllipsisTableCell>
        <Tooltip target={`.a${rowData.id}`} content={rowData.uniqueId} />
      </>
    )
  };

  const modEntityIdBodyTemplate = (rowData) => {
    return (
      //the 'a' at the start is a hack since css selectors can't start with a number
      <>
        <EllipsisTableCell otherClasses={`a${rowData.id}`}>
          {rowData.modEntityId}
        </EllipsisTableCell>
        <Tooltip target={`.a${rowData.id}`} content={rowData.modEntityId} />
      </>
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
    annotationsAggregations='diseaseAnnotationsAggregations'
    endpoint='disease-annotation'

    />);
  }

  const sgdStrainBackgroundEditorSelector = (props) => {
    if (props.rowData.type === "GeneDiseaseAnnotation") {
      return sgdStrainBackgroundEditorTemplate(props);
    }
    else {
      return null;
    }
  }

  const columns = [{
    field: "uniqueId",
    header: "Unique ID",
    sortable: isEnabled,
    filter: true,
    body: uniqueIdBodyTemplate,
    filterElement: filterComponentInputTextTemplate("uniqueidFilter", ["uniqueId"])
  },
  {
    field: "modEntityId",
    header: "MOD Entity ID",
    sortable: isEnabled,
    filter: true,
    body: modEntityIdBodyTemplate,
    filterElement: filterComponentInputTextTemplate("modentityidFilter", ["modEntityId"])
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
    sortable: isEnabled,
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
    editor: (props) => relatedNotesEditor(props),
    sortable: true,
    filter: true,
    filterElement: filterComponentInputTextTemplate("relatedNotesFilter", ["relatedNotes.freeText"])
  },
  {
    field: "conditionRelations.uniqueId",
    header: "Experimental Conditions",
    body: conditionRelationsTemplate,
    sortable: true,
    filter: true,
    filterElement: filterComponentInputTextTemplate(
      "conditionRelationsFilter",
      ["conditionRelations.conditions.conditionStatement", "conditionRelations.conditionRelationType.name", "conditionRelations.handle" ]
    )
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
    editor: (props) => sgdStrainBackgroundEditorSelector(props),
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
    filterElement: FilterMultiSelectComponentTemplate("geneticModifierRelationFilter", "diseaseGeneticModifierRelation.name", true),
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
    field: "modifiedBy.uniqueId",
    header: "Modified By",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("modifiedByFilter", ["modifiedBy.uniqueId"])
  },
  {
    field: "dateUpdated",
    header: "Date Updated",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("dateUpdatedFilter", ["dateUpdated"])
  },
  {
    field: "createdBy.uniqueId",
    header: "Created By",
    sortable: isEnabled,
    filter: true,
    filterElement: filterComponentInputTextTemplate("createdByFilter", ["createdBy.uniqueId"])
  },
  {
    field: "dateCreated",
    header: "Date Created",
    sortable: isEnabled,
    filter: true,
    filterType: "Date",
    filterElement: filterComponentInputTextTemplate("dateCreatedFilter", ["dateCreated"])
  },
  {
    field: "internal",
    header: "Internal",
    body: internalTemplate,
    filter: true,
    filterElement: FilterComponentDropDownTemplate("internalFilter", "internal", [{ text: "true" }, { text: "false" }], "text"),
    sortable: isEnabled,
    editor: (props) => internalEditor(props)
  },
  ];


  return (
    <>
      <div className="card">
        <Toast ref={toast_topleft} position="top-left" />
        <Toast ref={toast_topright} position="top-right" />
        <GenericDataTable 
          endpoint="disease-annotation" 
          tableName="Disease Annotations" 
          columns={columns}  
          aggregationFields={aggregationFields}
          isEditable={true}
          curieFields={["subject", "object", "diseaseGeneticModifier"]}
          sortMapping={sortMapping}
          mutation={mutation}
          isEnabled={isEnabled}
          setIsEnabled={setIsEnabled}
          toasts={{toast_topleft, toast_topright }}
        />
      </div>
      <RelatedNotesDialog
        originalRelatedNotesData={relatedNotesData}
        setOriginalRelatedNotesData={setRelatedNotesData}
        errorMessagesMainRow={errorMessages}
        setErrorMessagesMainRow={setErrorMessages}
      />
      <ConditionRelationsDialog
        conditonRelations={conditionRelations}
        conditionRelationsDialog={conditionRelationsDialog}
        setConditionRelationsDialog={setConditionRelationsDialog}
      />
    </>
  );
};
