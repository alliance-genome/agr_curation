import React, {useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { ErrorMessageComponent } from "../../components/ErrorMessageComponent";
import { EllipsisTableCell } from "../../components/EllipsisTableCell";
import { ListTableCell } from "../../components/ListTableCell";
import { Button } from 'primereact/button';
import { VocabularyTermSetService } from "../../service/VocabularyTermSetService";
import { AutocompleteRowEditor } from "../../components/Autocomplete/AutocompleteRowEditor";
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';
import { NewVocabularyTermSetForm } from './NewVocabularyTermSetForm';
import { useNewVocabularyTermSetReducer } from './useNewVocabularyTermSetReducer';
import { InputTextEditor } from "../../components/InputTextEditor";
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';


export const VocabularyTermSetTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [newVocabularyTermSet, setNewVocabularyTermSet] = useState(null);
	const { newVocabularyTermSetState, newVocabularyTermSetDispatch } = useNewVocabularyTermSetReducer(); 

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;



	let vocabularyTermSetService = new VocabularyTermSetService();

	const mutation = useMutation(updatedVocabularyTermSet => {
		if (!vocabularyTermSetService) {
			vocabularyTermSetService = new VocabularyTermSetService();
		}
		return vocabularyTermSetService.saveVocabularyTermSet(updatedVocabularyTermSet);
	});

	const handleNewVocabularyTermSetOpen = () => {
		newVocabularyTermSetDispatch({type: "OPEN_DIALOG"})
	};


	const aggregationFields = [
		'vocabularyTermSetVocabulary.name'
	];

	const vocabularyTemplate = (rowData) => {
		if (rowData.vocabularyTermSetVocabulary) {
			return (
				<EllipsisTableCell>
					{rowData.vocabularyTermSetVocabulary.name}
				</EllipsisTableCell>
			);
		}
	};
	
	const vocabularyEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteRowEditor
					autocompleteFields={["name"]}
					rowProps={props}
					searchService={searchService}
					endpoint='vocabulary'
					filterName='vocabularyFilter'
					fieldName='vocabularyTermSetVocabulary'
					subField='name'
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
						<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"vocabularyTermSetVocabulary"}
				/>
			</>
		);
	};

	const memberTermsTemplate = (rowData) => {
		if (rowData.memberTerms) {
			const listTemplate = (memberTerm) => {
				return (
					<EllipsisTableCell>
						{memberTerm.name}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.memberTerms} showBullets={true}/>
				</>
			);
		}
	};

	const memberTermsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteRowEditor
					autocompleteFields={["name"]}
					rowProps={props}
					searchService={searchService}
					endpoint='vocabularyterm'
					filterName='memberTermsFilter'
					fieldName='memberTerms'
					subField='name'
					isMultiple={true}
					isMemberTerms={true}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
						<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField="memberTerms"
				/>
			</>
		);
	};

	const nameEditor = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={'name'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"name"}/>
			</>
		);
	};

	const descriptionEditor = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={'vocabularyTermSetDescription'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"vocabularyTermSetDescription"}/>
			</>
		);
	};

	const columns = [
		{
			field: "name",
			header: "Name",
			sortable: isEnabled,
			filter: true,
			body: (rowData) => rowData.name,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]},
			editor: (props) => nameEditor(props)
		},
		{
			field: "vocabularyTermSetVocabulary.name",
			header: "Vocabulary",
			sortable: isEnabled,
			filter: true,
			body: vocabularyTemplate,
			filterElement: {type: "input", filterName: "vocabularyFilter", fields: ["vocabularyTermSetVocabulary.name"]},
			editor: (props) => vocabularyEditorTemplate(props)
		},
		{
			field: "memberTerms.name",
			header: "Member Terms",
			sortable: isEnabled,
			filter: true,
			body: memberTermsTemplate,
			filterElement: {type: "input", filterName: "memberTermsFilter", fields: ["memberTerms.name"]},
			editor: (props) => memberTermsEditorTemplate(props)
		},
		{
			field: "vocabularyTermSetDescription",
			header: "Description",
			sortable: isEnabled,
			filter: true,
			body: (rowData) => rowData.vocabularyTermSetDescription,
			filterElement: {type: "input", filterName: "vocabularyTermSetDescriptionFilter", fields: ["vocabularyTermSetDescription"]},
			editor: (props) => descriptionEditor(props)
		}
	];

	const headerButtons = () => {
		return (
			<>
				<Button label="New Vocabulary Term Set" icon="pi pi-plus" onClick={handleNewVocabularyTermSetOpen} />&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left"/>
			<Toast ref={toast_topright} position="top-right"/>
			<Messages ref={errorMessage}/>
			<GenericDataTable
				endpoint="vocabularytermset"
				tableName="Vocabulary Term Sets"
				columns={columns}
				aggregationFields={aggregationFields}
				isEditable={true}
				idFields={["vocabularyTermSetVocabulary, memberTerms"]}
				mutation={mutation}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				toasts={{toast_topleft, toast_topright }}
				initialColumnWidth={15}
				errorObject={{errorMessages, setErrorMessages}}
				headerButtons={headerButtons}
				newEntity={newVocabularyTermSet}
				deletionEnabled={true}
				deletionMethod={vocabularyTermSetService.deleteVocabularyTermSet}
			/>
			<NewVocabularyTermSetForm
				newVocabularyTermSetState={newVocabularyTermSetState}
				newVocabularyTermSetDispatch={newVocabularyTermSetDispatch}
				searchService={searchService}
				vocabularyTermSetService={vocabularyTermSetService}
				setNewVocabularyTermSet={setNewVocabularyTermSet}
			/>
		</div>
	)
}
