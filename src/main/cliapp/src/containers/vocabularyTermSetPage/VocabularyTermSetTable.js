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
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';
import { NewVocabularyTermSetForm } from './NewVocabularyTermSetForm';
import { useNewVocabularyTermSetReducer } from './useNewVocabularyTermSetReducer';
import { InputTextEditor } from "../../components/InputTextEditor";
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import {AutocompleteEditor} from "../../components/Autocomplete/AutocompleteEditor";
import {autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange, multipleAutocompleteOnChange} from "../../utils/utils";
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS, FILTER_CONFIGS } from '../../constants/FilterFields';


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

	const onVocabularyChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "vocabularyTermSetVocabulary", setFieldValue, "name");
	}
	const vocabularySearch = (event, setFiltered, setQuery) => {
		const autocompleteFields =["name"];
		const endpoint="vocabulary";
		const filterName="vocabularyFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const vocabularyEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={vocabularySearch}
					initialValue={props.rowData.vocabularyTermSetVocabulary?.name}
					rowProps={props}
					fieldName='vocabularyTermSetVocabulary'
					subField={"name"}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
						<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
					onValueChangeHandler={onVocabularyChange}
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

	const onMemberTermsChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "memberTerms", setFieldValue);
	};

	const memberTermSearch = (event, setFiltered, setInputValue, props) => {
		const autocompleteFields =["name"];
		const endpoint = "vocabularyterm";
		const filterName = "memberTermsFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			vocabularyFilter: {
				"vocabulary.name": {
					queryString: props.props.value[props.rowIndex].vocabularyTermSetVocabulary.name
				}
			}
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const memberTermsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					name="memberTerms"
					fieldName='memberTerms'
					subField='name'
					initialValue={props.rowData.memberTerms}
					rowProps={props}
					search={memberTermSearch}
					onValueChangeHandler={onMemberTermsChange}
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
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
			editor: (props) => nameEditor(props)
		},
		{
			field: "vocabularyTermSetVocabulary.name",
			header: "Vocabulary",
			sortable: isEnabled,
			filter: true,
			body: vocabularyTemplate,
			filterConfig: FILTER_CONFIGS.vocabularyFieldSetFilterConfig,
			editor: (props) => vocabularyEditorTemplate(props)
		},
		{
			field: "memberTerms.name",
			header: "Member Terms",
			sortable: isEnabled,
			filter: true,
			body: memberTermsTemplate,
			filterConfig: FILTER_CONFIGS.vocabularyMemberTermsFilterConfig,
			editor: (props) => memberTermsEditorTemplate(props)
		},
		{
			field: "vocabularyTermSetDescription",
			header: "Description",
			sortable: isEnabled,
			filter: true,
			body: (rowData) => rowData.vocabularyTermSetDescription,
			filterConfig: {type: "input", fieldSet: "vocabularyTermSetDescriptionFieldSet"},
			editor: (props) => descriptionEditor(props)
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("VocabularyTermSets", defaultColumnNames);

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
				defaultColumnNames={defaultColumnNames}
				initialTableState={initialTableState}
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
