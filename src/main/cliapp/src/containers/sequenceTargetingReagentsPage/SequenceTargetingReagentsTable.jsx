import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { TargetedGenesTemplate } from './TargetedGenesTemplate';
import { TargetedGenesDialog } from './TargetedGenesDialog';

export const SequenceTargetingReagentsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [targetedGenesData, setTargetedGenesData] = useState({ dialog: false });

	const [relatedNotesData, setRelatedNotesData] = useState({
		originalRelatedNotes: [],
		dialog: false,
		isInEdit: false,
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [sequenceTargetingReagents, setSequenceTargetingReagents] = useState([]);

	const searchService = new SearchService();

	const handleRelatedNotesOpen = (relatedNotes) => {
		setRelatedNotesData({ originalRelatedNotes: relatedNotes, dialog: true, isInEdit: false });
	};

	const handleTargetedGenesOpen = (sequenceTargetingReagent) => {
		setTargetedGenesData({ sequenceTargetingReagent, dialog: true });
	};

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				sortable: { isInEditMode },
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'primaryExternalId',
				header: 'Primary External ID',
				sortable: { isInEditMode },
				body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
				filterConfig: FILTER_CONFIGS.primaryExternalIdFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				sortable: { isInEditMode },
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				filterConfig: FILTER_CONFIGS.modInternalIdFilterConfig,
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				sortable: true,
				body: (rowData) => <StringListTemplate list={rowData.synonyms} />,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: { isInEditMode },
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			},
			{
				field: 'taxon.name',
				header: 'Taxon',
				sortable: { isInEditMode },
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			},
			{
				field:
					'sequenceTargetingReagentGeneAssociations.sequenceTargetingReagentGeneAssociationObject.geneSymbol.displayText',
				header: 'Targeted Genes',
				sortable: { isInEditMode },
				body: (rowData) => (
					<TargetedGenesTemplate sequenceTargetingReagent={rowData} handleOpen={handleTargetedGenesOpen} />
				),
				filterConfig: FILTER_CONFIGS.sequenceTargetingReagentTargetedGenesFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'References',
				sortable: { isInEditMode },
				body: (rowData) => (
					<TruncatedReferencesTemplate references={rowData.references} identifier={rowData.primaryExternalId} />
				),
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.sequenceTargetingReagentDataProviderFilterConfig,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Updated By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Date Updated',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				sortable: { isInEditMode },
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: { isInEditMode },
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.SEQUENCE_TARGETING_REAGENT;

	const initialTableState = useMemo(
		() => getDefaultTableState('SequenceTargetingReagents', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setSequenceTargetingReagents,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="id"
					endpoint={SEARCH_ENDPOINT}
					tableName="Sequence Targeting Reagents"
					entities={sequenceTargetingReagents}
					setEntities={setSequenceTargetingReagents}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					hasDetails={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<TargetedGenesDialog targetedGenesData={targetedGenesData} setTargetedGenesData={setTargetedGenesData} />
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
