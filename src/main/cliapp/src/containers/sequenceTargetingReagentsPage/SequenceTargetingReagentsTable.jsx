import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { TargetedGenesDialog } from './TargetedGenesDialog';
import { strCells } from './strCells';

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

	// The cells are shared with the Cassettes table's STR Components pop-up
	const cells = strCells({ handleTargetedGenesOpen, handleRelatedNotesOpen });

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				sortable: { isInEditMode },
				body: cells.curie,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'primaryExternalId',
				header: 'Primary External ID',
				sortable: { isInEditMode },
				body: cells.primaryExternalId,
				filterConfig: FILTER_CONFIGS.primaryExternalIdFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				sortable: { isInEditMode },
				body: cells.modInternalId,
				filterConfig: FILTER_CONFIGS.modInternalIdFilterConfig,
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: cells.name,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				sortable: true,
				body: cells.synonyms,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				body: cells.secondaryIdentifiers,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: { isInEditMode },
				body: cells.crossReferences,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			},
			{
				field: 'taxon.name',
				header: 'Taxon',
				sortable: { isInEditMode },
				body: cells.taxon,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			},
			{
				field:
					'sequenceTargetingReagentGeneAssociations.sequenceTargetingReagentGeneAssociationObject.geneSymbol.displayText',
				header: 'Targeted Genes',
				sortable: { isInEditMode },
				body: cells.targetedGenes,
				filterConfig: FILTER_CONFIGS.sequenceTargetingReagentTargetedGenesFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: false,
				body: cells.relatedNotes,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'References',
				sortable: { isInEditMode },
				body: cells.references,
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
				body: cells.updatedBy,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Date Updated',
				sortable: { isInEditMode },
				body: cells.dateUpdated,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: { isInEditMode },
				body: cells.createdBy,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: { isInEditMode },
				body: cells.dateCreated,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				body: cells.internal,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				sortable: { isInEditMode },
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: cells.obsolete,
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
