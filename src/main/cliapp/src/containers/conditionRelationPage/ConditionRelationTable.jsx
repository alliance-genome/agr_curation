import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { Messages } from 'primereact/messages';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { Button } from 'primereact/button';
import { ConditionRelationService } from '../../service/ConditionRelationService';
import { NewRelationForm } from './NewRelationForm';
import { useNewRelationReducer } from './useNewRelationReducer';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { SingleReferenceTableEditor } from '../../components/Editors/references/SingleReferenceTableEditor';
import { ConditionsTableEditor } from '../../components/Editors/experimentalCondition/ConditionsTableEditor';
import { ControlledVocabularyTableEditor } from '../../components/Editors/controlledVocabulary/ControlledVocabularyTableEditor';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { setNewEntity } from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { conditionsSort } from '../../components/Templates/utils/sortMethods';

export const ConditionRelationTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newRelationState, newRelationDispatch } = useNewRelationReducer();
	const [conditionRelations, setConditionRelations] = useState([]);

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let conditionRelationService = new ConditionRelationService();

	const conditionRelationTypeTerms = useControlledVocabularyService('condition_relation');

	const mutation = useMutation({
		mutationFn: (updatedRelation) => {
			if (!conditionRelationService) {
				conditionRelationService = new ConditionRelationService();
			}
			return conditionRelationService.saveConditionRelation(updatedRelation);
		},
	});

	const handleNewRelationOpen = () => {
		newRelationDispatch({ type: 'OPEN_DIALOG' });
	};

	const columns = useMemo(
		() => [
			{
				field: 'handle',
				header: 'Handle',
				sortable: true,
				body: (rowData) => rowData.handle,
				filterConfig: FILTER_CONFIGS.conditionRelationHandleFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="handle" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'singleReference',
				columnKey: 'singleReference.primaryCrossReferenceCurie',
				header: 'Reference',
				sortable: true,
				filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
				editor: (editorOptions) => (
					<SingleReferenceTableEditor
						editorOptions={editorOptions}
						field="singleReference"
						errorMessagesRef={errorMessagesRef}
					/>
				),
				body: (rowData) => <SingleReferenceTemplate singleReference={rowData.singleReference} />,
			},
			{
				field: 'conditionRelationType',
				columnKey: 'conditionRelationType.name',
				header: 'Relation',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.conditionRelationType?.name} />,
				filterConfig: FILTER_CONFIGS.conditionRelationTypeFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="conditionRelationType"
						options={conditionRelationTypeTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={false}
					/>
				),
			},
			{
				field: 'conditions',
				columnKey: 'conditions.conditionSummary',
				header: 'Experimental Conditions',
				sortable: true,
				body: (rowData) => (
					<ObjectListTemplate
						list={rowData.conditions}
						sortMethod={conditionsSort}
						stringTemplate={(item) => item.conditionSummary}
						showBullets={true}
					/>
				),
				filterConfig: FILTER_CONFIGS.experimentalConditionFilterConfig,
				editor: (editorOptions) => (
					<ConditionsTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[conditionRelationTypeTerms]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Annotation.CONDITION_RELATION;

	const initialTableState = useMemo(
		() => getDefaultTableState('Experiments', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setConditionRelations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Condition Relation" icon="pi pi-plus" onClick={handleNewRelationOpen} disabled={disabled} />
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
				tableName="Experiments"
				entities={conditionRelations}
				setEntities={setConditionRelations}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				curieFields={['singleReference']}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				headerButtons={headerButtons}
				deletionEnabled={true}
				deletionMethod={conditionRelationService.deleteConditionRelation}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewRelationForm
				newRelationState={newRelationState}
				newRelationDispatch={newRelationDispatch}
				searchService={searchService}
				conditionRelationService={conditionRelationService}
				conditionRelationTypeTerms={conditionRelationTypeTerms}
				setNewConditionRelation={(newConditionRelation, queryClient) =>
					setNewEntity(tableState, setConditionRelations, newConditionRelation, queryClient)
				}
			/>
		</div>
	);
};
