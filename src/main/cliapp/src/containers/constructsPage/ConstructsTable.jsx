import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { CassettesDialog } from './CassettesDialog';
import { CassettesEditDialog } from './CassettesEditDialog';
import { cassetteLabel } from './utils';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { SymbolEditDialog } from '../nameSlotAnnotations/dialogs/SymbolEditDialog';
import { FullNameEditDialog } from '../nameSlotAnnotations/dialogs/FullNameEditDialog';
import { SynonymsEditDialog } from '../nameSlotAnnotations/dialogs/SynonymsEditDialog';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { DialogTriggerTableEditor } from '../../components/Editors/dialog/DialogTriggerTableEditor';
import { StringListTableEditor } from '../../components/Editors/text/StringListTableEditor';
import { ReferencesTableEditor } from '../../components/Editors/autocomplete/references/ReferencesTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
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
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';
import { ConstructService } from '../../service/ConstructService';
import { Endpoints } from '../../constants/Endpoints';

export const ConstructsTable = () => {
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

	const [relatedNotesData, setRelatedNotesData] = useState({
		dialog: false,
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [constructs, setConstructs] = useState([]);

	const searchService = new SearchService();
	let constructService = new ConstructService();

	const mutation = useMutation({
		mutationFn: (updatedConstruct) => {
			if (!constructService) {
				constructService = new ConstructService();
			}
			return constructService.saveConstruct(updatedConstruct);
		},
	});

	const [cassettesData, setCassettesData] = useState({
		dialog: false,
	});

	const handleCassettesOpen = (constructCassetteAssociations) => {
		setCassettesData(() => ({
			originalCassettes: constructCassetteAssociations,
			dialog: true,
		}));
	};

	const handleFullNameOpen = (constructFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [constructFullName];
		_fullNameData['dialog'] = true;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (constructSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = constructSynonyms;
		_synonymsData['dialog'] = true;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSymbolOpen = (constructSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [constructSymbol];
		_symbolData['dialog'] = true;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
		setRelatedNotesData({ originalRelatedNotes: relatedNotes, dialog: true, isInEdit: false });
	};

	const openInEdit = (setData, originalKey, original) => (event, editorOptions, isInEdit) => {
		setData({
			[originalKey]: original(editorOptions.rowData),
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		});
	};

	const handleSymbolOpenInEdit = openInEdit(setSymbolData, 'originalSymbols', (row) => [row.constructSymbol]);
	const handleFullNameOpenInEdit = openInEdit(setFullNameData, 'originalFullNames', (row) => [row.constructFullName]);
	const handleSynonymsOpenInEdit = openInEdit(setSynonymsData, 'originalSynonyms', (row) => row.constructSynonyms);
	const handleCassettesOpenInEdit = openInEdit(
		setCassettesData,
		'originalCassettes',
		(row) => row.constructCassetteAssociations
	);
	const handleRelatedNotesOpenInEdit = openInEdit(
		setRelatedNotesData,
		'originalRelatedNotes',
		(row) => row.relatedNotes
	);

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
				field: 'uniqueId',
				header: 'Unique ID',
				sortable: { isInEditMode },
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
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
				field: 'constructSymbol',
				columnKey: 'constructSymbol.displayText',
				header: 'Symbol',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.constructSymbol}
						handleOpen={handleSymbolOpen}
						text={rowData.constructSymbol?.displayText}
						underline={false}
					/>
				),
				filter: true,
				filterConfig: FILTER_CONFIGS.constructSymbolFilterConfig,
				editor: (editorOptions) => (
					<DialogTriggerTableEditor
						editorOptions={editorOptions}
						onOpenInEdit={handleSymbolOpenInEdit}
						errorField="constructSymbol"
						displayHtml={editorOptions.rowData.constructSymbol?.displayText}
						addText="Add Symbol"
						tooltipObject="construct"
					/>
				),
			},
			{
				field: 'constructFullName',
				columnKey: 'constructFullName.displayText',
				header: 'Name',
				sortable: true,
				filter: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.constructFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.constructFullName?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.constructNameFilterConfig,
				editor: (editorOptions) => (
					<DialogTriggerTableEditor
						editorOptions={editorOptions}
						onOpenInEdit={handleFullNameOpenInEdit}
						errorField="constructFullName"
						displayHtml={editorOptions.rowData.constructFullName?.displayText}
						addText="Add Full Name"
						tooltipObject="construct"
					/>
				),
			},
			{
				field: 'constructSynonyms',
				columnKey: 'constructSynonyms.displayText',
				header: 'Synonyms',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.constructSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.constructSynonymsFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.constructSynonyms?.length;
					return (
						<DialogTriggerTableEditor
							editorOptions={editorOptions}
							onOpenInEdit={handleSynonymsOpenInEdit}
							errorField="constructSynonyms"
							displayText={count ? `Synonyms(${count}) ` : null}
							addText="Add Synonym"
							tooltipObject="construct"
						/>
					);
				},
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
				editor: (editorOptions) => <StringListTableEditor editorOptions={editorOptions} field="secondaryIdentifiers" />,
			},
			{
				field: 'constructCassetteAssociations',
				columnKey: 'constructCassetteAssociations.constructCassetteAssociationObject.cassetteSymbol.displayText',
				header: 'Cassettes',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.constructCassetteAssociations}
						handleOpen={handleCassettesOpen}
						getTextField={(association) => cassetteLabel(association?.constructCassetteAssociationObject)}
						underline={true}
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.constructCassettesFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.constructCassetteAssociations?.length;
					return (
						<DialogTriggerTableEditor
							editorOptions={editorOptions}
							onOpenInEdit={handleCassettesOpenInEdit}
							errorField="constructCassetteAssociations"
							displayText={count ? `Cassettes(${count}) ` : null}
							addText="Add Cassette"
							tooltipObject="construct"
						/>
					);
				},
			},
			{
				field: 'relatedNotes',
				columnKey: 'relatedNotes.freeText',
				header: 'Related Notes',
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.relatedNotes?.length;
					return (
						<DialogTriggerTableEditor
							editorOptions={editorOptions}
							onOpenInEdit={handleRelatedNotesOpenInEdit}
							errorField="relatedNotes"
							displayText={count ? `Notes(${count}) ` : null}
							addText="Add Note"
							tooltipObject="construct"
						/>
					);
				},
			},
			{
				field: 'references',
				columnKey: 'references.primaryCrossReferenceCurie',
				header: 'References',
				body: (rowData) => (
					<TruncatedReferencesTemplate
						references={rowData.references}
						identifier={rowData.primaryExternalId}
						detailPage="Construct"
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
				editor: (editorOptions) => <ReferencesTableEditor editorOptions={editorOptions} />,
			},
			{
				field: 'placeholder',
				header: 'Placeholder',
				body: (rowData) => <BooleanTemplate value={rowData.placeholder} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.placeholderFilterConfig,
				sortable: { isInEditMode },
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field={'placeholder'} />,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.constructDataProviderFilterConfig,
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
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: { isInEditMode },
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: { isInEditMode },
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
				sortable: { isInEditMode },
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field={'internal'} />,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: { isInEditMode },
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field={'obsolete'} />,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.CONSTRUCT;

	const initialTableState = useMemo(() => getDefaultTableState('Constructs', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setConstructs,
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
					tableName="Constructs"
					entities={constructs}
					setEntities={setConstructs}
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
			<FullNameEditDialog
				field="constructFullName"
				endpoint={Endpoints.SlotAnnotation.CONSTRUCT_FULL_NAME}
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SymbolEditDialog
				field="constructSymbol"
				endpoint={Endpoints.SlotAnnotation.CONSTRUCT_SYMBOL}
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<SynonymsEditDialog
				field="constructSynonyms"
				endpoint={Endpoints.SlotAnnotation.CONSTRUCT_SYNONYM}
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<CassettesEditDialog
				originalCassettesData={cassettesData}
				setOriginalCassettesData={setCassettesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<CassettesDialog originalCassettesData={cassettesData} setOriginalCassettesData={setCassettesData} />
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="construct_note_type"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
