import React, {useRef, useState } from 'react';
import {useMutation} from 'react-query';
import {Toast} from 'primereact/toast';
import {SearchService} from '../../service/SearchService';
import {Messages} from 'primereact/messages';
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {EllipsisTableCell} from "../../components/EllipsisTableCell";
import {ListTableCell} from "../../components/ListTableCell";
import {Tooltip} from 'primereact/tooltip';
import { Button } from 'primereact/button';
import {ConditionRelationService} from "../../service/ConditionRelationService";
import { AutocompleteEditor } from "../../components/Autocomplete/AutocompleteEditor";
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { NewRelationForm } from './NewRelationForm';
import { useNewRelationReducer } from './useNewRelationReducer';
import {InputTextEditor} from "../../components/InputTextEditor";
import {GenericDataTable} from '../../components/GenericDataTable/GenericDataTable';
import {defaultAutocompleteOnChange, autocompleteSearch, buildAutocompleteFilter, getRefString, multipleAutocompleteOnChange} from '../../utils/utils';
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const ConditionRelationTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [newConditionRelation, setNewConditionRelation] = useState(null);
	const { newRelationState, newRelationDispatch } = useNewRelationReducer();

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;



	let conditionRelationService = new ConditionRelationService();

	const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');

	const mutation = useMutation(updatedRelation => {
		if (!conditionRelationService) {
			conditionRelationService = new ConditionRelationService();
		}
		return conditionRelationService.saveConditionRelation(updatedRelation);
	});

	const handleNewRelationOpen = () => {
		newRelationDispatch({type: "OPEN_DIALOG"})
	};


	const aggregationFields = [
		'conditionRelationType.name'
	];

	const onConditionRelationTypeValueChange = (props, event) => {
		let updatedConditions = [...props.props.value];
		if (event.value || event.value === '') {
			updatedConditions[props.rowIndex].conditionRelationType = event.value;
		}
	};


	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType.name"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationTypeValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelationType.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"conditionRelationType.name"}/>
			</>
		);
	};

	const onReferenceValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "singleReference", setFieldValue);
	};

	const referenceSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "curieFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={referenceSearch}
					initialValue={() => getRefString(props.rowData.singleReference)}
					rowProps={props}
					fieldName='singleReference'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onReferenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"singleReference"}
				/>
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


	const onConditionRelationValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "conditions", setFieldValue);
	};

	const conditionRelationSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["conditionSummary"];
		const endpoint = "experimental-condition";
		const filterName = "experimentalConditionFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionRelationTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={conditionRelationSearch}
					initialValue={props.rowData.conditions}
					rowProps={props}
					fieldName='conditions'
					subField='conditionSummary'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<ExConAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onConditionRelationValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField="conditions"
				/>
			</>
		);
	};

	const handleEditor = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={'handle'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"handle"}/>
			</>
		);
	};

	const singleReferenceBodyTemplate = (rowData) => {
		if (rowData && rowData.singleReference) {
			let refString = getRefString(rowData.singleReference);
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: refString
						}}
					/>
					<Tooltip target={`.a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: refString
						}}
						/>
					</Tooltip>
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
			filterElement: {type: "input", filterName: "handleFilter", fields: FILTER_FIELDS.handleFilter},
			editor: (props) => handleEditor(props)
		},
		{
			field: "singleReference.curie",
			header: "Reference",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "singleReferenceFilter", fields: FILTER_FIELDS.singleReferenceFilter},
			editor: (props) => referenceEditorTemplate(props),
			body: singleReferenceBodyTemplate
		},
		{
			field: "conditionRelationType.name",
			header: "Relation",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "conditionRelationFilter", fields: FILTER_FIELDS.conditionRelationFilter},
			editor: (props) => conditionRelationTypeEditor(props)
		},
		{
			field: "conditions.conditionSummary",
			header: "Experimental Conditions",
			sortable: isEnabled,
			filter: true,
			body: conditionTemplate,
			filterElement: {type: "input", filterName: "experimentalConditionFilter", fields: FILTER_FIELDS.experimentalConditionFilter},
			editor: (props) => conditionRelationTemplate(props)
		},

	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("Experiments", defaultColumnNames);

	const headerButtons = () => {
		return (
			<>
				<Button label="New Condition Relation" icon="pi pi-plus" onClick={handleNewRelationOpen} />&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left"/>
			<Toast ref={toast_topright} position="top-right"/>
			<Messages ref={errorMessage}/>
			<GenericDataTable
				endpoint="condition-relation"
				tableName="Experiments"
				columns={columns}
				defaultColumnNames={defaultColumnNames}
				initialTableState={initialTableState}
				aggregationFields={aggregationFields}
				isEditable={true}
				curieFields={["singleReference"]}
				idFields={["conditionRelationType"]}
				mutation={mutation}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				toasts={{toast_topleft, toast_topright }}
				initialColumnWidth={10}
				errorObject={{errorMessages, setErrorMessages}}
				headerButtons={headerButtons}
				newEntity={newConditionRelation}
				deletionEnabled={true}
				deletionMethod={conditionRelationService.deleteConditionRelation}
			/>
			<NewRelationForm
				newRelationState={newRelationState}
				newRelationDispatch={newRelationDispatch}
				searchService={searchService}
				conditionRelationService={conditionRelationService}
				conditionRelationTypeTerms={conditionRelationTypeTerms}
				setNewConditionRelation={setNewConditionRelation}
			/>
		</div>
	)
}
