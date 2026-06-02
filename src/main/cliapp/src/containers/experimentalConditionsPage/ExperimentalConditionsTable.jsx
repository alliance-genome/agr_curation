import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { ConditionClassTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionClassTableEditor';
import { ConditionIdTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionIdTableEditor';
import { ConditionGeneOntologyTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionGeneOntologyTableEditor';
import { ConditionChemicalTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionChemicalTableEditor';
import { ConditionAnatomyTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionAnatomyTableEditor';
import { ConditionTaxonTableEditor } from '../../components/Editors/autocomplete/ontology/ConditionTaxonTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { ontologyTermAutocompleteFields } from '../../components/Editors/autocomplete/ontology/utils';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Button } from 'primereact/button';
import { NewConditionForm } from './NewConditionForm';
import { useNewConditionReducer } from './useNewConditionReducer';
import { setNewEntity } from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const ExperimentalConditionsTable = () => {
	const [errorMessages, setErrorMessages] = useState({});
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newConditionState, newConditionDispatch } = useNewConditionReducer();
	const [experimentalConditions, setExperimentalConditions] = useState([]);

	const searchService = new SearchService();
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let experimentalConditionService = new ExperimentalConditionService();

	const sortMapping = {
		'conditionGeneOntology.name': ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace'],
	};

	const mutation = useMutation({
		mutationFn: (updatedCondition) => {
			if (!experimentalConditionService) {
				experimentalConditionService = new ExperimentalConditionService();
			}
			return experimentalConditionService.saveExperimentalCondition(updatedCondition);
		},
	});

	const handleNewConditionOpen = () => {
		newConditionDispatch({ type: 'OPEN_DIALOG' });
	};

	const columns = useMemo(
		() => [
			{
				field: 'uniqueId',
				header: 'Unique ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			},
			{
				field: 'conditionSummary',
				header: 'Summary',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.conditionSummary} />,
				filterConfig: FILTER_CONFIGS.conditionRelationSummaryFilterConfig,
			},
			{
				field: 'conditionClass',
				columnKey: 'conditionClass.name',
				header: 'Class',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionClass} />,
				filterConfig: FILTER_CONFIGS.conditionClassFilterConfig,
				editor: (editorOptions) => (
					<ConditionClassTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionId',
				columnKey: 'conditionId.name',
				header: 'Condition Term',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionId} />,
				filterConfig: FILTER_CONFIGS.conditionIdFilterConfig,
				editor: (editorOptions) => (
					<ConditionIdTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionGeneOntology',
				columnKey: 'conditionGeneOntology.name',
				header: 'Gene Ontology',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionGeneOntology} />,
				filterConfig: FILTER_CONFIGS.conditionGeneOntologyFilterConfig,
				editor: (editorOptions) => (
					<ConditionGeneOntologyTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionChemical',
				columnKey: 'conditionChemical.name',
				header: 'Chemical',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionChemical} />,
				filterConfig: FILTER_CONFIGS.conditionChemicalFilterConfig,
				editor: (editorOptions) => (
					<ConditionChemicalTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionAnatomy',
				columnKey: 'conditionAnatomy.name',
				header: 'Anatomy',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionAnatomy} />,
				filterConfig: FILTER_CONFIGS.conditionAnatomyFilterConfig,
				editor: (editorOptions) => (
					<ConditionAnatomyTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionTaxon',
				columnKey: 'conditionTaxon.name',
				header: 'Condition Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.conditionTaxon} />,
				filterConfig: FILTER_CONFIGS.conditionTaxonFilterConfig,
				editor: (editorOptions) => (
					<ConditionTaxonTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionQuantity',
				header: 'Quantity',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.conditionQuantity} />,
				filterConfig: FILTER_CONFIGS.conditionQuantityFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="conditionQuantity"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'conditionFreeText',
				header: 'Free Text',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.conditionFreeText} />,
				filterConfig: FILTER_CONFIGS.conditionFreeTextFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="conditionFreeText"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'internal',
				header: 'Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				sortable: true,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} field="internal" />
				),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Annotation.EXPERIMENTAL_CONDITION;

	const initialTableState = useMemo(
		() => getDefaultTableState('ExperimentalConditions', columns, DEFAULT_COLUMN_WIDTH),
		[columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		sortMapping,
		setIsInEditMode,
		setEntities: setExperimentalConditions,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Condition" icon="pi pi-plus" onClick={handleNewConditionOpen} disabled={disabled} />
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
				tableName="Experimental Conditions"
				entities={experimentalConditions}
				setEntities={setExperimentalConditions}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				curieFields={[
					'conditionClass',
					'conditionId',
					'conditionAnatomy',
					'conditionTaxon',
					'conditionGeneOntology',
					'conditionChemical',
				]}
				sortMapping={sortMapping}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				headerButtons={headerButtons}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={true}
				deletionMethod={experimentalConditionService.deleteExperimentalCondition}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewConditionForm
				newConditionState={newConditionState}
				newConditionDispatch={newConditionDispatch}
				searchService={searchService}
				mutation={mutation}
				setNewExperimentalCondition={(newExCon, queryClient) =>
					setNewEntity(tableState, setExperimentalConditions, newExCon, queryClient)
				}
				ontologyTermAutocompleteFields={ontologyTermAutocompleteFields}
			/>
		</div>
	);
};
