import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
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
import { SymbolEditDialog } from '../nameSlotAnnotations/dialogs/SymbolEditDialog';
import { FullNameEditDialog } from '../nameSlotAnnotations/dialogs/FullNameEditDialog';
import { SynonymsEditDialog } from '../nameSlotAnnotations/dialogs/SynonymsEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { CrossReferencesEditDialog } from '../../components/crossReferences/CrossReferencesEditDialog';
import { GenomicComponentsDialog } from '../constructsPage/GenomicComponentsDialog';
import { UsesEditDialog } from '../cassettesPage/uses/UsesEditDialog';
import { DialogTriggerTableEditor } from '../../components/Editors/dialog/DialogTriggerTableEditor';
import { StringListTableEditor } from '../../components/Editors/text/StringListTableEditor';
import { ReferencesTableEditor } from '../../components/Editors/autocomplete/references/ReferencesTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { SearchService } from '../../service/SearchService';
import { TransgenicToolService } from '../../service/TransgenicToolService';
import { Endpoints } from '../../constants/Endpoints';
import { transgenicToolDisplayValue } from '../cassettesPage/utils';
import { TransgenicToolUsesDialog } from './TransgenicToolUsesDialog';
import { TransgenicToolAssociationsEditDialog } from './TransgenicToolAssociationsEditDialog';
import { buildTransgenicToolPayload } from './detail/utils';
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
	const [crossReferencesData, setCrossReferencesData] = useState({ dialog: false });
	// SCRUM-6654: the associations are edited in their own pop-up and saved through their own endpoint
	const [associationsEditData, setAssociationsEditData] = useState({ dialog: false });

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [transgenicTools, setTransgenicTools] = useState([]);

	const searchService = new SearchService();
	const transgenicToolService = useMemo(() => new TransgenicToolService(), []);

	// SCRUM-6654: a row edit saves the tool's own fields, sent as the detail page sends them. Its associations
	// are saved by their pop-up; PUT /transgenic-tool keeps them as stored.
	const mutation = useMutation({
		mutationFn: (updatedTransgenicTool) =>
			transgenicToolService.saveTransgenicTool(buildTransgenicToolPayload(updatedTransgenicTool)),
	});

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

	// The pop-ups opened from a row in edit mode; they write back through the row's editorCallback
	const openInEdit = (setData, originalKey, original) => (event, editorOptions, isInEdit) => {
		setData({
			[originalKey]: original(editorOptions.rowData),
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		});
	};
	const handleSymbolOpenInEdit = openInEdit(setSymbolData, 'originalSymbols', (row) => [row.transgenicToolSymbol]);
	const handleFullNameOpenInEdit = openInEdit(setFullNameData, 'originalFullNames', (row) => [
		row.transgenicToolFullName,
	]);
	const handleSynonymsOpenInEdit = openInEdit(setSynonymsData, 'originalSynonyms', (row) => row.transgenicToolSynonyms);
	const handleUsesOpenInEdit = openInEdit(setUsesData, 'originalUses', (row) => row.transgenicToolUses);
	const handleRelatedNotesOpenInEdit = openInEdit(
		setRelatedNotesData,
		'originalRelatedNotes',
		(row) => row.relatedNotes
	);
	const handleCrossReferencesOpenInEdit = openInEdit(
		setCrossReferencesData,
		'originalCrossReferences',
		(row) => row.crossReferences
	);
	const handleAssociationsOpenInEdit = (event, editorOptions) => {
		event.preventDefault();
		setAssociationsEditData({ dialog: true, transgenicToolId: editorOptions.rowData?.id, mainRowProps: editorOptions });
	};

	// The trigger, in the row editor, for a pop-up editing a list
	const listEditor = (field, onOpenInEdit, label, addText) => (editorOptions) => {
		const count = editorOptions.rowData?.[field]?.length;
		return (
			<DialogTriggerTableEditor
				editorOptions={editorOptions}
				onOpenInEdit={onOpenInEdit}
				errorField={field}
				displayText={count ? `${label}(${count}) ` : null}
				addText={addText}
				tooltipObject="transgenic tool"
			/>
		);
	};

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
			// An editable column's field names the object or list its editor replaces, as editorCallback writes
			// through a dotted path rather than replacing it; columnKey keeps the key the saved sort, filter,
			// width and order settings are stored under.
			{
				field: 'transgenicToolFullName',
				columnKey: 'transgenicToolFullName.displayText',
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
				editor: (editorOptions) => (
					<DialogTriggerTableEditor
						editorOptions={editorOptions}
						onOpenInEdit={handleFullNameOpenInEdit}
						errorField="transgenicToolFullName"
						displayHtml={editorOptions.rowData.transgenicToolFullName?.displayText}
						addText="Add Full Name"
						tooltipObject="transgenic tool"
					/>
				),
			},
			{
				field: 'transgenicToolSymbol',
				columnKey: 'transgenicToolSymbol.displayText',
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
				editor: (editorOptions) => (
					<DialogTriggerTableEditor
						editorOptions={editorOptions}
						onOpenInEdit={handleSymbolOpenInEdit}
						errorField="transgenicToolSymbol"
						displayHtml={editorOptions.rowData.transgenicToolSymbol?.displayText}
						addText="Add Symbol"
						tooltipObject="transgenic tool"
					/>
				),
			},
			{
				field: 'transgenicToolSynonyms',
				columnKey: 'transgenicToolSynonyms.displayText',
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
				editor: listEditor('transgenicToolSynonyms', handleSynonymsOpenInEdit, 'Synonyms', 'Add Synonym'),
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
				editor: (editorOptions) => <StringListTableEditor editorOptions={editorOptions} field="secondaryIdentifiers" />,
			},
			{
				field: 'crossReferences',
				columnKey: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: { isInEditMode },
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
				editor: listEditor(
					'crossReferences',
					handleCrossReferencesOpenInEdit,
					'Cross References',
					'Add Cross Reference'
				),
			},
			{
				field: 'transgenicToolUses',
				columnKey: 'transgenicToolUses.uses.name',
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
				editor: listEditor('transgenicToolUses', handleUsesOpenInEdit, 'Uses', 'Add Use'),
			},
			{
				// The row holds the associations as summarised, which their pop-up replaces once it has saved them
				field: 'associatedTransgenicTools',
				columnKey:
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
				editor: listEditor(
					'associatedTransgenicTools',
					handleAssociationsOpenInEdit,
					'Associations',
					'Add Association'
				),
			},
			{
				field: 'relatedNotes',
				columnKey: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
				editor: listEditor('relatedNotes', handleRelatedNotesOpenInEdit, 'Notes', 'Add Note'),
			},
			{
				field: 'references',
				columnKey: 'references.primaryCrossReferenceCurie',
				header: 'References',
				sortable: { isInEditMode },
				body: (rowData) => (
					<TruncatedReferencesTemplate references={rowData.references} identifier={rowData.primaryExternalId} />
				),
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
				editor: (editorOptions) => <ReferencesTableEditor editorOptions={editorOptions} />,
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
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="placeholder" />,
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
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: { isInEditMode },
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="obsolete" />,
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
					isEditable={true}
					mutation={mutation}
					hasDetails={true}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<SymbolEditDialog
				field="transgenicToolSymbol"
				endpoint={Endpoints.SlotAnnotation.TRANSGENIC_TOOL_SYMBOL}
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<FullNameEditDialog
				field="transgenicToolFullName"
				endpoint={Endpoints.SlotAnnotation.TRANSGENIC_TOOL_FULL_NAME}
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SynonymsEditDialog
				field="transgenicToolSynonyms"
				endpoint={Endpoints.SlotAnnotation.TRANSGENIC_TOOL_SYNONYM}
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<UsesEditDialog
				field="transgenicToolUses"
				endpoint={Endpoints.SlotAnnotation.TRANSGENIC_TOOL_USE}
				originalUsesData={usesData}
				setOriginalUsesData={setUsesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<TransgenicToolUsesDialog usesData={usesData} setUsesData={setUsesData} />
			<CrossReferencesEditDialog
				originalCrossReferencesData={crossReferencesData}
				setOriginalCrossReferencesData={setCrossReferencesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<TransgenicToolAssociationsEditDialog data={associationsEditData} setData={setAssociationsEditData} />
			<GenomicComponentsDialog
				originalComponentsData={associationsData}
				setOriginalComponentsData={setAssociationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				objectField="transgenicToolTransgenicToolAssociationObject"
				title="Transgenic Tool Associations"
				getComponentLabel={transgenicToolDisplayValue}
				showNotesAndEvidence={false}
			/>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="transgenic_tool_note_type"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
