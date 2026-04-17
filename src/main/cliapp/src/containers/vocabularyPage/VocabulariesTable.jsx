import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { VocabularyService } from '../../service/VocabularyService';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { NewVocabularyForm } from '../../containers/controlledVocabularyPage/NewVocabularyForm';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { BooleanTableEditor } from '../../components/Editors/boolean/BooleanTableEditor';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { setNewEntity } from '../../utils/utils';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

export const VocabulariesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [vocabularies, setVocabularies] = useState([]);
	const [newVocabularyDialog, setNewVocabularyDialog] = useState(false);

	const searchService = new SearchService();

	let vocabularyService = new VocabularyService();

	const mutation = useMutation({
		mutationFn: (updatedVocabulary) => {
			if (!vocabularyService) {
				vocabularyService = new VocabularyService();
			}
			return vocabularyService.saveVocabulary(updatedVocabulary);
		},
	});

	const columns = useMemo(
		() => [
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'vocabularyDescription',
				header: 'Description',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.vocabularyDescription} />,
				filterConfig: FILTER_CONFIGS.vocabularyDescriptionFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="vocabularyDescription"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				sortable: true,
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				editor: (editorOptions) => (
					<BooleanTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						field="obsolete"
					/>
				),
			},
			{
				field: 'vocabularyLabel',
				header: 'Label',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.vocabularyLabel} />,
				filterConfig: FILTER_CONFIGS.vocabularyLabelFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = Endpoints.Vocabulary.VOCABULARY;

	const initialTableState = useMemo(
		() => getDefaultTableState('Vocabularies', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setVocabularies,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const handleOpenNewVocabulary = () => {
		setNewVocabularyDialog(true);
	};

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Vocabulary" icon="pi pi-plus" onClick={handleOpenNewVocabulary} disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Vocabularies"
				entities={vocabularies}
				setEntities={setVocabularies}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				headerButtons={headerButtons}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={true}
				deletionMethod={vocabularyService.deleteVocabulary}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewVocabularyForm
				newVocabularyDialog={newVocabularyDialog}
				setNewVocabularyDialog={setNewVocabularyDialog}
				setNewVocabulary={(newVocabulary, queryClient) =>
					setNewEntity(tableState, setVocabularies, newVocabulary, queryClient)
				}
			/>
		</div>
	);
};
