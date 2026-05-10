import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { VocabularyTermSetService } from '../../service/VocabularyTermSetService';
import { NewVocabularyTermSetForm } from './NewVocabularyTermSetForm';
import { useNewVocabularyTermSetReducer } from './useNewVocabularyTermSetReducer';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { VocabularyTableEditor } from '../../components/Editors/autocomplete/vocabulary/VocabularyTableEditor';
import { MemberTermsTableEditor } from '../../components/Editors/autocomplete/vocabularyTerm/MemberTermsTableEditor';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { setNewEntity } from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';

export const VocabularyTermSetTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newVocabularyTermSetState, newVocabularyTermSetDispatch } = useNewVocabularyTermSetReducer();

	const [termSets, setTermSets] = useState();

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let vocabularyTermSetService = new VocabularyTermSetService();

	const mutation = useMutation({
		mutationFn: (updatedVocabularyTermSet) => {
			if (!vocabularyTermSetService) {
				vocabularyTermSetService = new VocabularyTermSetService();
			}
			return vocabularyTermSetService.saveVocabularyTermSet(updatedVocabularyTermSet);
		},
	});

	const handleNewVocabularyTermSetOpen = () => {
		newVocabularyTermSetDispatch({ type: 'OPEN_DIALOG' });
	};

	const columns = useMemo(
		() => [
			{
				field: 'name',
				header: 'Name',
				body: (rowData) => <StringTemplate string={rowData.name} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'vocabularyTermSetVocabulary',
				columnKey: 'vocabularyTermSetVocabulary.name',
				header: 'Vocabulary',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.vocabularyTermSetVocabulary?.name} />,
				filterConfig: FILTER_CONFIGS.vocabularyFieldSetFilterConfig,
				editor: (editorOptions) => (
					<VocabularyTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'memberTerms',
				columnKey: 'memberTerms.name',
				header: 'Member Terms',
				sortable: true,
				body: (rowData) => <StringListTemplate list={rowData.memberTerms?.map((memberTerm) => memberTerm?.name)} />,
				filterConfig: FILTER_CONFIGS.vocabularyMemberTermsFilterConfig,
				editor: (editorOptions) => (
					<MemberTermsTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'vocabularyTermSetDescription',
				header: 'Description',
				body: (rowData) => <StringTemplate string={rowData.vocabularyTermSetDescription} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.vocabularyTermSetDescriptionFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="vocabularyTermSetDescription"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'vocabularyLabel',
				header: 'Label',
				body: (rowData) => <StringTemplate string={rowData.vocabularyLabel} />,
				filterConfig: FILTER_CONFIGS.vocabularyLabelFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 15;
	const SEARCH_ENDPOINT = Endpoints.Vocabulary.TERM_SET;

	const initialTableState = useMemo(
		() => getDefaultTableState('VocabularyTermSets', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setTermSets,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button
					label="New Vocabulary Term Set"
					icon="pi pi-plus"
					onClick={handleNewVocabularyTermSetOpen}
					disabled={disabled}
				/>
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<Messages ref={errorMessage} />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Vocabulary Term Sets"
				entities={termSets}
				setEntities={setTermSets}
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
				headerButtons={headerButtons}
				deletionEnabled={true}
				deletionMethod={vocabularyTermSetService.deleteVocabularyTermSet}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewVocabularyTermSetForm
				newVocabularyTermSetState={newVocabularyTermSetState}
				newVocabularyTermSetDispatch={newVocabularyTermSetDispatch}
				searchService={searchService}
				vocabularyTermSetService={vocabularyTermSetService}
				setNewVocabularyTermSet={(newTermSet, queryClient) =>
					setNewEntity(tableState, setTermSets, newTermSet, queryClient)
				}
			/>
		</div>
	);
};
