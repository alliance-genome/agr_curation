import React, { useRef, useState, useReducer, useEffect, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';

import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { VocabularyService } from '../../service/VocabularyService';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { InputTextEditor } from '../../components/InputTextEditor';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { NewTermForm } from '../../containers/controlledVocabularyPage/NewTermForm';
import { NewVocabularyForm } from '../../containers/controlledVocabularyPage/NewVocabularyForm';
import { Button } from 'primereact/button';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { setNewEntity } from '../../utils/utils';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

export const ControlledVocabularyTable = () => {
	const newTermReducer = (state, action) => {
		switch (action.type) {
			case 'RESET':
				return { name: '', obsolete: false };
			default:
				return { ...state, [action.field]: action.value };
		}
	};

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const [vocabularies, setVocabularies] = useState(null);
	const [newTermDialog, setNewTermDialog] = useState(false);
	const [newVocabularyDialog, setNewVocabularyDialog] = useState(false);
	const [newTerm, newTermDispatch] = useReducer(newTermReducer, {});

	const [terms, setTerms] = useState([]);

	const searchService = new SearchService();

	const obsoleteTerms = useControlledVocabularyService('generic_boolean_terms');
	let vocabularyService = new VocabularyService();

	const {
		data: vocabulariesData,
		isSuccess: vocabulariesIsSuccess,
		isError: vocabulariesIsError,
		error: vocabulariesError,
	} = useQuery({
		queryKey: ['vocabularies'],
		queryFn: () => vocabularyService.getVocabularies(),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle vocabularies data (was in onSuccess callback)
	useEffect(() => {
		if (vocabulariesIsSuccess && vocabulariesData?.data?.results) {
			//TODO: check this data object
			console.log('ControlledVocabularyTable data', vocabulariesData);
			setVocabularies(
				vocabulariesData.data.results.sort(function (a, b) {
					return a.name.localeCompare(b.name, 'en', { sensitivity: 'base' });
				})
			);
		}
	}, [vocabulariesData, vocabulariesIsSuccess]);

	// Handle vocabularies error (was in onError callback)
	useEffect(() => {
		if (vocabulariesIsError && vocabulariesError) {
			toast_topleft.current.show([
				{ life: 7000, severity: 'error', summary: 'Page error: ', detail: vocabulariesError.message, sticky: false },
			]);
		}
	}, [vocabulariesIsError, vocabulariesError]);

	const mutation = useMutation({
		mutationFn: (updatedTerm) => {
			if (!vocabularyService) {
				vocabularyService = new VocabularyService();
			}
			return vocabularyService.saveTerm(updatedTerm);
		},
	});

	const handleNewTerm = () => {
		newTermDispatch({ type: 'RESET' });
		setNewTermDialog(true);
	};

	const handleNewVocabulary = () => {
		setNewVocabularyDialog(true);
	};

	const createButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Term" icon="pi pi-plus" onClick={handleNewTerm} disabled={disabled} />
				&nbsp;&nbsp;
				<Button label="New Vocabulary" icon="pi pi-plus" onClick={handleNewVocabulary} disabled={disabled} />
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
				<InputTextEditor editorChange={onNameEditorValueChange} rowProps={props} fieldName={'name'} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'name'} />
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
				<InputTextEditor editorChange={onAbbreviationEditorValueChange} rowProps={props} fieldName={'abbreviation'} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'abbreviation'} />
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
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'vocabulary.name'}
				/>
			</>
		);
	};

	const onDefinitionEditorValueChange = (props, event) => {
		let updatedTerms = [...props.props.value];
		if (event.target.value || event.target.value === '') {
			updatedTerms[props.rowIndex].definition = event.target.value;
		}
	};

	const definitionEditorTemplate = (props) => {
		return (
			<>
				<InputTextEditor editorChange={onDefinitionEditorValueChange} rowProps={props} fieldName={'definition'} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'definition'} />
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
					options={obsoleteTerms?.terms || []}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={'obsolete'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'obsolete'} />
			</>
		);
	};

	const columns = useMemo(
		() => [
			{
				field: 'id',
				header: 'Id',
				sortable: false,
				body: (rowData) => <IdTemplate id={rowData.id} />,
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (props) => nameEditorTemplate(props),
				body: (rowData) => <StringTemplate string={rowData.name} />,
			},
			{
				field: 'abbreviation',
				header: 'Abbreviation',
				sortable: true,
				filterConfig: FILTER_CONFIGS.abbreviationFilterConfig,
				editor: (props) => abbreviationEditorTemplate(props),
				body: (rowData) => <StringTemplate string={rowData.abbreviation} />,
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				sortable: true,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
				body: (rowData) => <StringListTemplate list={rowData.synonyms} />,
			},
			{
				field: 'vocabulary.name',
				header: 'Vocabulary',
				sortable: true,
				filterConfig: FILTER_CONFIGS.vocabularyNameFilterConfig,
				editor: (props) => vocabularyEditorTemplate(props),
				body: (rowData) => <StringTemplate string={rowData.vocabulary?.name} />,
			},
			{
				field: 'definition',
				header: 'Definition',
				sortable: true,
				filterConfig: FILTER_CONFIGS.definitionFilterConfig,
				editor: (props) => definitionEditorTemplate(props),
				body: (rowData) => <StringTemplate string={rowData.definition} />,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Updated By',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Date Updated',
				sortable: true,
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: true,
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: true,
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				sortable: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				editor: (props) => obsoleteEditorTemplate(props),
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[obsoleteTerms, vocabularies]
	);

	const DEFAULT_COLUMN_WIDTH = 13;
	const SEARCH_ENDPOINT = 'vocabularyterm';

	const initialTableState = useMemo(
		() => getDefaultTableState('ControlledVocabularyTerms', columns, DEFAULT_COLUMN_WIDTH),
		[columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setTerms,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Controlled Vocabulary Terms"
				entities={terms}
				setEntities={setTerms}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				headerButtons={createButtons}
				deletionEnabled={true}
				deletionMethod={vocabularyService.deleteTerm}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewTermForm
				newTermDialog={newTermDialog}
				setNewTermDialog={setNewTermDialog}
				newTerm={newTerm}
				newTermDispatch={newTermDispatch}
				vocabularies={vocabularies}
				obsoleteTerms={obsoleteTerms}
				vocabularyService={vocabularyService}
				setNewTerm={(newTerm, queryClient) => setNewEntity(tableState, setTerms, newTerm, queryClient)}
			/>
			<NewVocabularyForm newVocabularyDialog={newVocabularyDialog} setNewVocabularyDialog={setNewVocabularyDialog} />
		</div>
	);
};
