import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ComponentsDialog } from '../constructsPage/ComponentsDialog';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const TransgenicToolsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [synonymsData, setSynonymsData] = useState({
		dialog: false,
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false,
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false,
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [transgenicTools, setTransgenicTools] = useState([]);

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [usesData, setUsesData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [relatedNotesData, setRelatedNotesData] = useState({
		dialog: false,
	});

	const handleFullNameOpen = (transgenicToolFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [transgenicToolFullName];
		_fullNameData['dialog'] = true;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (transgenicToolSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = transgenicToolSynonyms;
		_synonymsData['dialog'] = true;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSymbolOpen = (transgenicToolSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [transgenicToolSymbol];
		_symbolData['dialog'] = true;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleUsesOpen = (transgenicToolUses) => {
		let _usesData = {};
		_usesData['originalComponents'] = transgenicToolUses;
		_usesData['dialog'] = true;
		setUsesData(() => ({
			..._usesData,
		}));
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = relatedNotes;
		_relatedNotesData['dialog'] = true;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const getUsesTextString = (item) => {
		let relationName = '';
		if (item?.relation?.name) {
			relationName = item.relation.name;
			if (relationName.indexOf(' (RO:') !== -1) {
				relationName = relationName.substring(0, relationName.indexOf(' (RO:'));
			}
		}
		return relationName + ': ' + item.componentSymbol;
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes && rowData.relatedNotes.length > 0) {
			return (
				<ListDialogTemplate
					entities={rowData.relatedNotes}
					handleOpen={handleRelatedNotesOpen}
					getTextField={() => `Notes (${rowData.relatedNotes.length})`}
					underline={true}
				/>
			);
		}
		return null;
	};

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'uniqueId',
				header: 'Unique ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			},
			{
				field: 'primaryExternalId',
				header: 'Primary External ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
				filterConfig: FILTER_CONFIGS.primaryExternalIdFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				filterConfig: FILTER_CONFIGS.modInternalIdFilterConfig,
			},
			{
				field: 'transgenicToolFullName.displayText',
				header: 'Full Name',
				sortable: true,
				filter: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.transgenicToolFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.transgenicToolFullName?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.transgenicToolNameFilterConfig,
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
				filter: true,
				filterConfig: FILTER_CONFIGS.transgenicToolSymbolFilterConfig,
			},
			{
				field: 'transgenicToolSynonyms.displayText',
				header: 'Synonyms',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.transgenicToolSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.transgenicToolSynonymsFilterConfig,
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: false,
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			},
			{
				field: 'transgenicToolUses.componentSymbol',
				header: 'Transgenic Tool Uses',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.transgenicToolUses}
						handleOpen={handleUsesOpen}
						getTextField={getUsesTextString}
						underline={true}
					/>
				),
				sortable: false,
				filterConfig: FILTER_CONFIGS.transgenicToolUsesFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: false,
				body: relatedNotesTemplate,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'References',
				body: (rowData) => <TruncatedReferencesTemplate references={rowData.references} />,
				sortable: false,
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.transgenicToolDataProviderFilterConfig,
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
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				sortable: true,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: true,
			},
			{
				field: 'placeholder',
				header: 'Placeholder',
				body: (rowData) => <BooleanTemplate value={rowData.placeholder} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.placeholderFilterConfig,
				sortable: true,
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
					tableName="TransgenicTools"
					entities={transgenicTools}
					setEntities={setTransgenicTools}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					deletionEnabled={false}
					deprecateOption={false}
					modReset={false}
					duplicationEnabled={false}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
			<ComponentsDialog
				originalComponentsData={usesData}
				setOriginalComponentsData={setUsesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
