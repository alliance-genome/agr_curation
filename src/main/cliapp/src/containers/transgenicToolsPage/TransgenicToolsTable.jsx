import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { GenomicComponentsDialog } from '../constructsPage/GenomicComponentsDialog';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { transgenicToolDisplayValue } from '../cassettesPage/utils';
import { TransgenicToolUsesDialog } from './TransgenicToolUsesDialog';
import { associatedToolText, toolAssociationsFindParams, useTermTexts } from './utils';

// A tool has a dozen associations at most, so the pop-up fetches them in one page.
const ASSOCIATIONS_LIMIT = 100;

export const TransgenicToolsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [symbolData, setSymbolData] = useState({ dialog: false });
	const [fullNameData, setFullNameData] = useState({ dialog: false });
	const [synonymsData, setSynonymsData] = useState({ dialog: false });
	const [usesData, setUsesData] = useState({ dialog: false });
	const [associationsData, setAssociationsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	const [relatedNotesData, setRelatedNotesData] = useState({
		originalRelatedNotes: [],
		dialog: false,
		isInEdit: false,
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [transgenicTools, setTransgenicTools] = useState([]);

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const handleSymbolOpen = (symbol) => setSymbolData({ originalSymbols: [symbol], dialog: true });
	const handleFullNameOpen = (fullName) => setFullNameData({ originalFullNames: [fullName], dialog: true });
	const handleSynonymsOpen = (synonyms) => setSynonymsData({ originalSynonyms: synonyms, dialog: true });
	const handleUsesOpen = (uses) => setUsesData({ originalUses: uses, dialog: true });
	// The search results carry the associated tools reduced to relation, symbol and identifier; the
	// associations themselves, with both tools and their audit fields, are fetched when the pop-up opens.
	const handleAssociationsOpen = (transgenicTool) =>
		searchService
			.find(
				Endpoints.Entity.TRANSGENIC_TOOL_TRANSGENIC_TOOL_ASSOCIATION,
				ASSOCIATIONS_LIMIT,
				0,
				toolAssociationsFindParams(transgenicTool.id)
			)
			.then((response) => setAssociationsData({ originalComponents: response?.results || [], dialog: true }))
			.catch((error) =>
				toast_topright.current?.show({
					severity: 'error',
					summary: 'Transgenic Tool Associations',
					detail: error?.message || 'The associations could not be loaded',
				})
			);
	const handleRelatedNotesOpen = (relatedNotes) =>
		setRelatedNotesData({ originalRelatedNotes: relatedNotes, dialog: true, isInEdit: false });

	const columns = useMemo(
		() => [
			{
				field: 'uniqueId',
				header: 'Unique ID',
				sortable: { isInEditMode },
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			},
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
				field: 'transgenicToolFullName.displayText',
				header: 'Full Name',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.transgenicToolFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.transgenicToolFullName?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolFullNameFilterConfig,
			},
			{
				field: 'transgenicToolSymbol.displayText',
				header: 'Symbol',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.transgenicToolSymbol}
						handleOpen={handleSymbolOpen}
						text={rowData.transgenicToolSymbol?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolSymbolFilterConfig,
			},
			{
				field: 'transgenicToolSynonyms.displayText',
				header: 'Synonyms',
				sortable: true,
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.transgenicToolSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolSynonymsFilterConfig,
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
				field: 'transgenicToolUses.uses.name',
				header: 'Transgenic Tool Uses',
				sortable: { isInEditMode },
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.transgenicToolUses}
						handleOpen={handleUsesOpen}
						getTextField={(use) => useTermTexts(use).join(', ')}
						underline={true}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolUsesFilterConfig,
			},
			{
				field:
					'transgenicToolTransgenicToolAssociations.transgenicToolTransgenicToolAssociationObject.transgenicToolSymbol.displayText',
				header: 'Transgenic Tool Associations',
				sortable: { isInEditMode },
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.associatedTransgenicTools}
						handleOpen={() => handleAssociationsOpen(rowData)}
						getTextField={associatedToolText}
						underline={true}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolAssociationsFilterConfig,
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
				filterConfig: FILTER_CONFIGS.transgenicToolDataProviderFilterConfig,
			},
			{
				field: 'placeholder',
				header: 'Placeholder',
				body: (rowData) => <BooleanTemplate value={rowData.placeholder} />,
				filterConfig: FILTER_CONFIGS.placeholderFilterConfig,
				sortable: { isInEditMode },
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
	const SEARCH_ENDPOINT = Endpoints.Entity.TRANSGENIC_TOOL;

	const initialTableState = useMemo(
		() => getDefaultTableState('TransgenicTools', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setTransgenicTools,
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
					tableName="Transgenic Tools"
					entities={transgenicTools}
					setEntities={setTransgenicTools}
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
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<TransgenicToolUsesDialog usesData={usesData} setUsesData={setUsesData} />
			<GenomicComponentsDialog
				originalComponentsData={associationsData}
				setOriginalComponentsData={setAssociationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				objectField="transgenicToolTransgenicToolAssociationObject"
				title="Transgenic Tool Associations"
				getComponentLabel={transgenicToolDisplayValue}
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
