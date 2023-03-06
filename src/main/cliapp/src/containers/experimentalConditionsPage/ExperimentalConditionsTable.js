import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { InputTextEditor } from '../../components/InputTextEditor';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Tooltip } from 'primereact/tooltip';
import { Button } from 'primereact/button';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { NewConditionForm } from './NewConditionForm';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { useNewConditionReducer } from './useNewConditionReducer';
import {defaultAutocompleteOnChange, autocompleteSearch, buildAutocompleteFilter} from "../../utils/utils";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const ExperimentalConditionsTable = () => {

	const [errorMessages, setErrorMessages] = useState({});
	const [isEnabled, setIsEnabled] = useState(true);
	const [newExperimentalCondition, setNewExperimentalCondition] = useState(null);
	const { newConditionState, newConditionDispatch } = useNewConditionReducer();

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const searchService = new SearchService();
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let experimentalConditionService = new ExperimentalConditionService();

	const curieAutocompleteFields = ["curie", "name", "crossReferences.referencedCurie", "secondaryIdentifiers", "synonyms.name"];

	const sortMapping = {
		'conditionGeneOntology.name': ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace']
	}

	const mutation = useMutation(updatedCondition => {
		if (!experimentalConditionService) {
			experimentalConditionService = new ExperimentalConditionService();
		}
		return experimentalConditionService.saveExperimentalCondition(updatedCondition);
	});

	const handleNewConditionOpen = () => {
		newConditionDispatch({type: "OPEN_DIALOG"})
	};

	const freeTextEditor = (props, fieldname) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={fieldname}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={fieldname} />
			</>
		);
	};

	const uniqueIdBodyTemplate = (rowData) => {
		if (rowData) {
			return (
				<>
					<EllipsisTableCell otherClasses={`a${rowData.id}`}>{rowData.uniqueId}</EllipsisTableCell>
					<Tooltip target={`.a${rowData.id}`} content={rowData.uniqueId} />
				</>
			)
		}
	};

	const summaryBodyTemplate = (rowData) => {
		if (rowData) {
			return (
				<>
					<EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.conditionSummary}</EllipsisTableCell>
					<Tooltip target={`.b${rowData.id}`} content={rowData.conditionSummary} />
				</>
			)
		}
	};

	const conditionClassBodyTemplate = (rowData) => {
		if (rowData?.conditionClass) {
			return (
				<>
					<EllipsisTableCell otherClasses={`.a${rowData.id}${rowData.conditionClass.curie.replace(':', '')}`}>{rowData.conditionClass.name} ({rowData.conditionClass.curie})</EllipsisTableCell>
					<Tooltip target={`.a${rowData.id}${rowData.conditionClass.curie.replace(':', '')}`} content={`${rowData.conditionClass.name} ${rowData.conditionClass.curie}`} />
				</>
			)
		}
	};

	const conditionIdBodyTemplate = (rowData) => {
		if (rowData?.conditionId) {
			return (
				<>
					<EllipsisTableCell otherClasses={`.a${rowData.id}${rowData.conditionId.curie.replace(':', '')}`}>{rowData.conditionId.name} ({rowData.conditionId.curie})</EllipsisTableCell>
					<Tooltip target={`.a${rowData.id}${rowData.conditionId.curie.replace(':', '')}`} content={`${rowData.conditionId.name} ${rowData.conditionId.curie}`} />
				</>
			)
		}
	};

	const conditionGeneOntologyBodyTemplate = (rowData) => {
		if (rowData?.conditionGeneOntology) {
			return <EllipsisTableCell>{rowData.conditionGeneOntology.name} ({rowData.conditionGeneOntology.curie})</EllipsisTableCell>;
		}
	};

	const conditionChemicalBodyTemplate = (rowData) => {
		if (rowData?.conditionChemical) {
			return <EllipsisTableCell>{rowData.conditionChemical.name} ({rowData.conditionChemical.curie})</EllipsisTableCell>;
		}
	};

	const conditionAnatomyBodyTemplate = (rowData) => {
		if (rowData?.conditionAnatomy) {
			return <EllipsisTableCell>{rowData.conditionAnatomy.name} ({rowData.conditionAnatomy.curie})</EllipsisTableCell>;
		}
	};

	const conditionTaxonBodyTemplate = (rowData) => {
		if (rowData?.conditionTaxon) {
			return (
					<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.id}${rowData.conditionTaxon.curie.replace(':', '')}`}>
							{rowData.conditionTaxon.name} ({rowData.conditionTaxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"TAXON_NAME_"}${rowData.id}${rowData.conditionTaxon.curie.replace(':', '')}`} content= {`${rowData.conditionTaxon.name} (${rowData.conditionTaxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
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
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const onConditionClassValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "conditionClass", setFieldValue);
	};

	const conditionClassSearch = (event, setFiltered, setQuery) => {
		const endpoint = "zecoterm";
		const filterName = "conditionClassEditorFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		const otherFilters = {
			"subsetFilter": {
				"subsets": {
					queryString: 'ZECO_0000267'
				}
			}
		}

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const conditionClassEditorTemplate = (props) => {
		return (
			<>
			<AutocompleteEditor
				search={conditionClassSearch}
				initialValue={props.rowData.conditionClass?.curie}
				rowProps={props}
				fieldName="conditionClass"
				onValueChangeHandler={onConditionClassValueChange}
			/>
			<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='conditionClass'
				/>
			</>
		);
	};

	const onSingleOntologyValueChange = (event, setFieldValue, props, fieldName) => {
		defaultAutocompleteOnChange(props, event, fieldName, setFieldValue);
	};

	const singleOntologySearch = (event, setFiltered, endpoint, autocomplete, setQuery) => {
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, autocomplete);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const singleOntologyEditorTemplate = (props, fieldname, endpoint, autocomplete) => {
		return (
			<>
				<AutocompleteEditor
					search={(event, setFiltered, setQuery) => singleOntologySearch(event, setFiltered, endpoint, autocomplete, setQuery)}
					initialValue={props.rowData[fieldname]?.curie}
					rowProps={props}
					filterName='singleOntologyFilter'
					fieldName={fieldname}
					onValueChangeHandler={onSingleOntologyValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={fieldname}
				/>
			</>
		);
	};


	const columns = [
		{
			field: "uniqueId",
			header: "Unique ID",
			sortable: isEnabled,
			body: uniqueIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: "conditionSummary",
			header: "Summary",
			sortable: isEnabled,
			body: summaryBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionSummaryFilterConfig,
		},
		{
			field: "conditionClass.name",
			header: "Class",
			sortable: isEnabled,
			body: conditionClassBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionClassFilterConfig,
			editor: (props) => conditionClassEditorTemplate(props, curieAutocompleteFields)
		},
		{
			field: "conditionId.name",
			header: "Condition Term",
			sortable: isEnabled,
			body: conditionIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionIdFilterConfig,
			editor: (props) => singleOntologyEditorTemplate(props, "conditionId", "experimentalconditionontologyterm", curieAutocompleteFields)
		},
		{
			field: "conditionGeneOntology.name",
			header: "Gene Ontology",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.conditionGeneOntologyFilterConfig,
			editor: (props) => singleOntologyEditorTemplate(props, "conditionGeneOntology", "goterm", curieAutocompleteFields),
			body: conditionGeneOntologyBodyTemplate
		},
		{
			field: "conditionChemical.name",
			header: "Chemical",
			sortable: isEnabled,
			body: conditionChemicalBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionChemicalFilterConfig,
			editor: (props) => singleOntologyEditorTemplate(props, "conditionChemical", "chemicalterm", curieAutocompleteFields)
		},
		{
			field: "conditionAnatomy.name",
			header: "Anatomy",
			sortable: isEnabled,
			body: conditionAnatomyBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionAnatomyFilterConfig,
			editor: (props) => singleOntologyEditorTemplate(props, "conditionAnatomy", "anatomicalterm", curieAutocompleteFields)
		},
		{
			field: "conditionTaxon.name",
			header: "Condition Taxon",
			sortable: isEnabled,
			body: conditionTaxonBodyTemplate,
			filterConfig: FILTER_CONFIGS.conditionTaxonFilterConfig,
			editor: (props) => singleOntologyEditorTemplate(props, "conditionTaxon", "ncbitaxonterm", curieAutocompleteFields)
		},
		{
			field: "conditionQuantity",
			header: "Quantity",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.conditionQuantityFilterConfig,
			editor: (props) => freeTextEditor(props, "conditionQuantity")
		}
		,
		{
			field: "conditionFreeText",
			header: "Free Text",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.conditionFreeTextFilterConfig,
			editor: (props) => freeTextEditor(props, "conditionFreeText")
		},
		{
			field: "internal",
			header: "Internal",
			body: internalBodyTemplate,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: isEnabled,
			editor: (props) => internalEditor(props)
	},
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("ExperimentalConditions", defaultColumnNames);

	const headerButtons = () => {
		return (
			<>
				<Button label="New Condition" icon="pi pi-plus" onClick={handleNewConditionOpen} />&nbsp;&nbsp;
			</>
		);
	};

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="experimental-condition"
					tableName="Experimental Conditions"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={true}
					curieFields={["conditionClass", "conditionId", "conditionAnatomy", "conditionTaxon", "conditionGeneOntology", "conditionChemical"]}
					sortMapping={sortMapping}
					mutation={mutation}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					headerButtons={headerButtons}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={10}
					errorObject = {{errorMessages, setErrorMessages}}
					newEntity={newExperimentalCondition}
					deletionEnabled={true}
					deletionMethod={experimentalConditionService.deleteExperimentalCondition}
				/>
				<NewConditionForm
					newConditionState={newConditionState}
					newConditionDispatch={newConditionDispatch}
					searchService={searchService}
					mutation={mutation}
					setNewExperimentalCondition={setNewExperimentalCondition}
					curieAutocompleteFields={curieAutocompleteFields}
				/>
			</div>
	)
}
