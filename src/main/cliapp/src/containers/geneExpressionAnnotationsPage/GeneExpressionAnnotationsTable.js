import React, { useRef, useState, useMemo } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';

export const GeneExpressionAnnotationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [geneExpressionAnnotations, setGeneExpressionAnnotations] = useState([]);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const searchService = new SearchService();

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {};

	const conditionRelationsTemplate = (rowData) => {
		if (rowData?.conditionRelations) {
			const withoutHandle = rowData.conditionRelations.filter((cr) => !cr.handle);
			if (withoutHandle.length > 0) {
				return <StringTemplate string={`Conditions (${withoutHandle.length})`} />;
			}
		}
	};

	const experimentsTemplate = (rowData) => {
		if (rowData?.conditionRelations) {
			const withHandle = rowData.conditionRelations.filter((cr) => cr.handle);
			if (withHandle.length > 0) {
				return <StringTemplate string={withHandle[0].handle} />;
			}
		}
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return <StringTemplate string={`Notes (${rowData.relatedNotes.length})`} />;
		}
	};

	const columns = useMemo(
		() => [
			{
				field: 'expressionExperiment.singleReference.primaryCrossReferenceCurie',
				header: 'Reference',
				body: (rowData) => <SingleReferenceTemplate singleReference={rowData.expressionExperiment?.singleReference} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaExperimentSingleReferenceFilterConfig,
			},
			{
				field: 'expressionExperiment.curie',
				header: 'Experiment Curie',
				body: (rowData) => <IdTemplate id={rowData.expressionExperiment?.curie} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.uniqueId',
				header: 'Experiment Unique ID',
				body: (rowData) => <IdTemplate id={rowData.expressionExperiment?.uniqueId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaExperimentUniqueidFilterConfig,
			},
			{
				field: 'expressionExperiment.primaryExternalId',
				header: 'MOD Experiment ID',
				body: (rowData) => <StringTemplate string={rowData.expressionExperiment?.primaryExternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaExperimentPrimaryExternalIdFilterConfig,
			},
			{
				field: 'expressionAnnotationSubject.geneSymbol.displayText',
				header: 'Subject',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.expressionAnnotationSubject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaSubjectFilterConfig,
			},
			{
				field: 'expressionAssayUsed.name',
				header: 'Assay Used',
				body: (rowData) => <OntologyTermTemplate term={rowData.expressionAssayUsed} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaAssayUsedFilterConfig,
			},
			{
				field: 'conditionRelations.handle',
				header: 'Experiments',
				body: experimentsTemplate,
				sortable: true,
				filterConfig: FILTER_CONFIGS.daConditionRelationsHandleFilterConfig,
			},
			{
				field: 'conditionRelations.uniqueId',
				header: 'Experimental Conditions',
				body: conditionRelationsTemplate,
				sortable: true,
				filterConfig: FILTER_CONFIGS.daConditionRelationsSummaryFilterConfig,
			},
			{
				field: 'expressionExperiment.crossReferences.displayName',
				header: 'Experiment Cross Refs',
				body: (rowData) => <CrossReferencesTemplate list={rowData.expressionExperiment?.crossReferences} />,
				sortable: false,
				filterConfig: FILTER_CONFIGS.geaExperimentCrossRefsFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				body: (rowData) => <StringTemplate string={rowData.dataProvider?.abbreviation} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaDataProviderFilterConfig,
			},
			{
				field: 'curie',
				header: 'Annotation Curie',
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'uniqueId',
				header: 'Annotation Unique ID',
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			},
			{
				field: 'relation.name',
				header: 'Expression Relation',
				body: (rowData) => <StringTemplate string={rowData.relation?.name} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaRelationFilterConfig,
			},
			{
				field: 'whereExpressedStatement',
				header: 'Where Expressed',
				body: (rowData) => <StringTemplate string={rowData.whereExpressedStatement} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaWhereExpressedFilterConfig,
			},
			{
				field: 'whenExpressedStageName',
				header: 'When Expressed',
				body: (rowData) => <StringTemplate string={rowData.whenExpressedStageName} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geaWhenExpressedFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Related Notes',
				body: relatedNotesTemplate,
				sortable: true,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Annotation Cross Refs',
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				sortable: false,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			},
			{
				field: 'expressionExperiment.createdBy.uniqueId',
				header: 'Experiment Created By',
				body: (rowData) => <StringTemplate string={rowData.expressionExperiment?.createdBy?.uniqueId} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.dateCreated',
				header: 'Experiment Date Created',
				body: (rowData) => <StringTemplate string={rowData.expressionExperiment?.dateCreated} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.updatedBy.uniqueId',
				header: 'Experiment Updated By',
				body: (rowData) => <StringTemplate string={rowData.expressionExperiment?.updatedBy?.uniqueId} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.dateUpdated',
				header: 'Experiment Date Updated',
				body: (rowData) => <StringTemplate string={rowData.expressionExperiment?.dateUpdated} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.internal',
				header: 'Experiment Internal',
				body: (rowData) => <BooleanTemplate value={rowData.expressionExperiment?.internal} />,
				sortable: false,
			},
			{
				field: 'expressionExperiment.obsolete',
				header: 'Experiment Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.expressionExperiment?.obsolete} />,
				sortable: false,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Annotation Created By',
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Annotation Date Created',
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Annotation Updated By',
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Annotation Date Updated',
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Annotation Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Annotation Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'gene-expression-annotation';
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = useMemo(
		() => getDefaultTableState('GeneExpressionAnnotations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters),
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setGeneExpressionAnnotations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Gene Expression Annotations"
					entities={geneExpressionAnnotations}
					setEntities={setGeneExpressionAnnotations}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					toasts={{ toast_topleft, toast_topright }}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					sortMapping={sortMapping}
					errorObject={{ errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages }}
					deletionEnabled={false}
					deprecateOption={false}
					modReset={false}
					duplicationEnabled={false}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
					defaultFilters={defaultFilters}
				/>
			</div>
		</>
	);
};
