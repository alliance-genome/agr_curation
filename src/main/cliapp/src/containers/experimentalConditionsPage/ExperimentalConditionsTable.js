import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { InputTextEditor } from '../../components/InputTextEditor';
import { AutocompleteEditor } from '../../components/AutocompleteEditor';
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Tooltip } from 'primereact/tooltip';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';


export const ExperimentalConditionsTable = () => {

  const [errorMessages, setErrorMessages] = useState({});
  const [isEnabled, setIsEnabled] = useState(true);

  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const searchService = new SearchService();
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);

  let experimentalConditionService = null;

  const sortMapping = {
    'conditionGeneOntology.name': ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace']
  }

  const mutation = useMutation(updatedCondition => {
    if (!experimentalConditionService) {
      experimentalConditionService = new ExperimentalConditionService();
    }
    return experimentalConditionService.saveExperimentalCondition(updatedCondition);
  });

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
  
  const internalBodyTemplate = (rowData) => {
    if (rowData && rowData.internal !== null && rowData.internal !== undefined) {
      return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
    }
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
      filterElement: {type: "input", filterName: "uniqueidFilter", fields: ["uniqueId"]}, 
    },
    {
      field: "conditionSummary",
      header: "Summary",
      sortable: isEnabled,
      filter: true,
      body: summaryBodyTemplate,
      filterElement: {type: "input", filterName: "conditionSummaryFilter", fields: ["conditionSummary"]}, 
    },
    {
      field: "conditionStatement",
      header: "Statement",
      sortable: isEnabled,
      filter: true,
      body: statementBodyTemplate,
      filterElement: {type: "input", filterName: "conditionStatementFilter", fields: ["conditionStatement"]}, 
    },
    {
      field: "conditionClass.name",
      header: "Class",
      sortable: isEnabled,
      body: conditionClassBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "conditionClassFilter", fields: ["conditionClass.curie", "conditionClass.name"]}, 
      editor: (props) => conditionClassEditorTemplate(props, curieAutocompleteFields)
    },
    {
      field: "conditionId.name",
      header: "Condition Term",
      sortable: isEnabled,
      body: conditionIdBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "conditionIdFilter", fields: ["conditionId.curie", "conditionId.name"]}, 
      editor: (props) => singleOntologyEditorTemplate(props, "conditionId", "experimentalconditionontologyterm", curieAutocompleteFields)
    },
    {
      field: "conditionGeneOntology.name",
      header: "Gene Ontology",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "conditionGeneOntologyFilter", fields: ["conditionGeneOntology.curie", "conditionGeneOntology.name"]}, 
      editor: (props) => singleOntologyEditorTemplate(props, "conditionGeneOntology", "goterm", curieAutocompleteFields),
      body: conditionGeneOntologyBodyTemplate
    },
    {
      field: "conditionChemical.name",
      header: "Chemical",
      sortable: isEnabled,
      body: conditionChemicalBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "conditionChemicalFilter", fields: ["conditionChemical.curie", "conditionChemical.name"]}, 
      editor: (props) => singleOntologyEditorTemplate(props, "conditionChemical", "chemicalterm", curieAutocompleteFields)
    },
    {
      field: "conditionAnatomy.name",
      header: "Anatomy",
      sortable: isEnabled,
      body: conditionAnatomyBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "conditionAnatomyFilter", fields: ["conditionAnatomy.curie", "conditionAnatomy.name"]}, 
      editor: (props) => singleOntologyEditorTemplate(props, "conditionAnatomy", "anatomicalterm", curieAutocompleteFields)
    },
    {
      field: "conditionTaxon.name",
      header: "Condition Taxon",
      sortable: isEnabled,
      body: conditionTaxonBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "conditionTaxonFilter", fields: ["conditionTaxon.curie", "conditionTaxon.name"]}, 
      editor: (props) => singleOntologyEditorTemplate(props, "conditionTaxon", "ncbitaxonterm", curieAutocompleteFields)
    },
    {
      field: "conditionQuantity",
      header: "Quantity",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "conditionQuantityFilter", fields: ["conditionQuantity"]}, 
      editor: (props) => freeTextEditor(props, "conditionQuantity")
    }
    ,
    {
      field: "conditionFreeText",
      header: "Free Text",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "conditionFreeTextFilter", fields: ["conditionFreeText"]}, 
      editor: (props) => freeTextEditor(props, "conditionFreeText")
    },
    { 
      field: "internal",
      header: "Internal",
      body: internalBodyTemplate,
      filter: true,
      filterElement: {type: "dropdown", filterName: "internalFilter", fields: ["internal"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
      sortable: isEnabled,
      editor: (props) => internalEditor(props)
  },
  ];

  return (
      <div className="card">
        <Toast ref={toast_topleft} position="top-left" />
        <Toast ref={toast_topright} position="top-right" />
        <GenericDataTable 
          endpoint="experimental-condition" 
          tableName="Experimental Conditions" 
          columns={columns}  
          isEditable={true}
          curieFields={["conditionClass", "conditionId", "conditionAnatomy", "conditionTaxon", "conditionGeneOntology", "conditionChemical"]}
          sortMapping={sortMapping}
          mutation={mutation}
          isEnabled={isEnabled}
          setIsEnabled={setIsEnabled}
          toasts={{toast_topleft, toast_topright }}
          initialColumnWidth={10}
          errorObject = {{errorMessages, setErrorMessages}}
        />
      </div>
  )
}
