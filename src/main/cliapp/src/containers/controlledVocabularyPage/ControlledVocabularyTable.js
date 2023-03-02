import React, {useRef, useState, useReducer} from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { useMutation, useQuery} from 'react-query';
import { Toast } from 'primereact/toast';

import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {VocabularyService} from "../../service/VocabularyService";
import {TrueFalseDropdown} from "../../components/TrueFalseDropDownSelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {InputTextEditor} from "../../components/InputTextEditor";
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {NewTermForm} from "../../containers/controlledVocabularyPage/NewTermForm";
import {NewVocabularyForm} from "../../containers/controlledVocabularyPage/NewVocabularyForm";
import {Button} from "primereact/button";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const ControlledVocabularyTable = () => {
		const newTermReducer = (state, action) => {
				switch (action.type) {
						case 'RESET':
								return { name: "", obsolete: false };
						default:
								return { ...state, [action.field]: action.value };
				}
		};

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [isEnabled, setIsEnabled] = useState(true);
	const [vocabularies, setVocabularies] = useState(null);
	const [newTermDialog, setNewTermDialog] = useState(false);
	const [newVocabularyDialog, setNewVocabularyDialog] = useState(false);
	const [newTerm, newTermDispatch] = useReducer(newTermReducer, {});


	const obsoleteTerms = useControlledVocabularyService('generic_boolean_terms');
	let vocabularyService = new VocabularyService();

	useQuery("vocabularies",() => vocabularyService.getVocabularies(), {
			onSuccess: (data) => {
					setVocabularies(data.data.results.sort(function (a, b) {
						return a.name.localeCompare(b.name, 'en', {'sensitivity' : 'base'});
					}));
			},
			onError: (error) => {
					toast_topleft.current.show([
							{ life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
					]);
			}
	});

	const mutation = useMutation(updatedTerm => {
			if (!vocabularyService) {
					vocabularyService = new VocabularyService();
			}
			return vocabularyService.saveTerm(updatedTerm);
	});

	const handleNewTerm = () => {
		newTermDispatch({ type: "RESET" });
		setNewTermDialog(true);
	};

	const handleNewVocabulary = () => {
		setNewVocabularyDialog(true);
	};

	const createButtons = () => {
				return (
						<>
						<Button label="New Term" icon="pi pi-plus" onClick={handleNewTerm} />&nbsp;&nbsp;
						<Button label="New Vocabulary" icon="pi pi-plus" onClick={handleNewVocabulary} />
						</>
				);
		};

	const onNameEditorValueChange = (props, event) => {
		let updatedTerms = [...props.props.value];
		if (event.target.value || event.target.value === '') {
				updatedTerms[props.rowIndex].name = event.target.value;
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
							<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"name"} />
					</>
			);
	};

	const onAbbreviationEditorValueChange = (props, event) => {
			let updatedTerms = [...props.props.value];
			if (event.target.value || event.target.value === '') {
					updatedTerms[props.rowIndex].abbreviation = event.target.value;
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
							<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"abbreviation"} />
					</>
			);
	};

		const onVocabularyNameEditorValueChange = (props, event) => {
				let updatedTerms = [...props.props.value];
				if (event.value || event.value === '') {
					updatedTerms[props.rowIndex].vocabulary = event.value;
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
							<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"vocabulary.name"} />
					</>
			);
	};

	const onDefinitionEditorValueChange =(props, event) => {
			let updatedTerms = [...props.props.value];
			if (event.target.value || event.target.value === '') {
					updatedTerms[props.rowIndex].definition = event.target.value;
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
							<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"definition"} />
					</>
			);
	};

	const onObsoleteEditorValueChange = (props, event) => {
			let updatedTerms = [...props.props.value];
			if (event.value || event.value === '') {
					updatedTerms[props.rowIndex].obsolete = JSON.parse(event.value.name);
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
							<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"obsolete"} />
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
			sortable: false,
			showFilter: false,
			filterElement: {type: "none"}, 
		},
		{
			field: "name",
			header: "Name",
			sortable: isEnabled,
			filterElement: {type: "input", fieldSet: "nameFieldSet"}, 
			editor: (props) => nameEditorTemplate(props),
			body: nameBodyTemplate
		},
		{
			field: "abbreviation",
			header: "Abbreviation",
			sortable: isEnabled,
			filterElement: {type: "input", fieldSet: "abbreviationFieldSet"}, 
			editor: (props) => abbreviationEditorTemplate(props),
			body: abbreviationBodyTemplate
		},
		{
			field: "vocabulary.name",
			header: "Vocabulary",
			sortable: isEnabled,
			filterElement: {type: "input", fieldSet: "vocabularyNameFieldSet"}, 
			editor: (props) => vocabularyEditorTemplate(props),
			body: vocabularyBodyTemplate
		},
		{
			field: "definition",
			header: "Definition",
			sortable: isEnabled,
			filterElement: {type: "input", fieldSet: "definitionFieldSet"}, 
			editor: (props) => definitionEditorTemplate(props),
			body: definitionBodyTemplate
		},
		{
			field: "obsolete",
			header: "Obsolete",
			sortable: isEnabled,
			filterElement: {type: "input", fieldSet: "obsoleteFieldSet"}, 
			editor: (props) => obsoleteEditorTemplate(props),
			body: obsoleteBodyTemplate
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("ControlledVocabularyTerms", defaultColumnNames);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="vocabularyterm" 
					tableName="Controlled Vocabulary Terms" 
					columns={columns}  
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={true}
					mutation={mutation}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={13}
					errorObject = {{errorMessages, setErrorMessages}}
					headerButtons={createButtons}
					deletionEnabled={true}
					deletionMethod={vocabularyService.deleteTerm}
				/>
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
