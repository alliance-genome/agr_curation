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
import {getRefString} from '../../utils/utils';


export const ConditionRelationTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const { newRelationState, newRelationDispatch } = useNewRelationReducer(); 

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;



	let conditionRelationService = null;

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

	const onConditionRelationValueChange = (props, event) => {
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
					editorChange={onConditionRelationValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelationType.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"conditionRelationType.name"}/>
			</>
		);
	};

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["curie", "cross_references.curie"]}
					rowProps={props}
					searchService={searchService}
					endpoint='literature-reference'
					filterName='curieFilter'
					isReference={true}
					fieldName='singleReference'
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
						<LiteratureAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
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

	const conditionRelationTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["conditionSummary"]}
					rowProps={props}
					searchService={searchService}
					endpoint='experimental-condition'
					filterName='experimentalConditionFilter'
					fieldName='conditions'
					subField='conditionSummary'
					isMultiple={true}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
						<ExConAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
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
			filterElement: {type: "input", filterName: "uniqueIdFilter", fields: ["handle"]},
			editor: (props) => handleEditor(props)
		},
		{
			field: "singleReference.curie",
			header: "Reference",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "singleReferenceFilter", fields: ["singleReference.curie", "singleReference.crossReferences.curie"]},
			editor: (props) => referenceEditorTemplate(props),
			body: singleReferenceBodyTemplate
		},
		{
			field: "conditionRelationType.name",
			header: "Relation",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "conditionRelationFilter", fields: ["conditionRelationType.name"]},
			editor: (props) => conditionRelationTypeEditor(props)
		},
		{
			field: "conditions.conditionSummary",
			header: "Conditions",
			sortable: isEnabled,
			filter: true,
			body: conditionTemplate,
			filterElement: {type: "input", filterName: "experimentalConditionFilter", fields: ["conditions.conditionSummary"]},
			editor: (props) => conditionRelationTemplate(props)
		},

	];

	const headerButtons = () => {
		return (
			<>
				<Button label="New Relation" icon="pi pi-plus" onClick={handleNewRelationOpen} />&nbsp;&nbsp;
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
				tableName="Condition Relations Handles"
				columns={columns}
				aggregationFields={aggregationFields}
				nonNullFields={['handle', 'singleReference']}
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
			/>
			<NewRelationForm
				newRelationState={newRelationState}
				newRelationDispatch={newRelationDispatch}
				searchService={searchService}
				conditionRelationService={conditionRelationService}
				conditionRelationTypeTerms={conditionRelationTypeTerms}
				onConditionRelationValueChange={onConditionRelationValueChange}
			/>
		</div>
	)
}
