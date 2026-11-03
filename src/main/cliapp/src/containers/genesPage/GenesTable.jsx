import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { SecondaryIdsEditDialog } from '../allelesPage/secondaryIds/SecondaryIdsEditDialog';
import { SecondaryIdsReadOnlyDialog } from '../allelesPage/secondaryIds/SecondaryIdsReadOnlyDialog';
import { SynonymsEditDialog } from '../nameSlotAnnotations/dialogs/SynonymsEditDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { SymbolEditDialog } from '../nameSlotAnnotations/dialogs/SymbolEditDialog';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameEditDialog } from '../nameSlotAnnotations/dialogs/FullNameEditDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SystematicNameDialog } from './SystematicNameDialog';
import { CrossReferencesEditDialog } from '../allelesPage/crossReferences/CrossReferencesEditDialog';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { CrossReferenceTemplate } from '../../components/Templates/CrossReferenceTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { DialogTriggerEditor } from '../../components/Editors/dialog/DialogTriggerEditor';
import { GeneTypeTableEditor } from '../../components/Editors/autocomplete/geneType/GeneTypeTableEditor';
import { TaxonTableEditor } from '../../components/Editors/autocomplete/taxon/TaxonTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';

import { SearchService } from '../../service/SearchService';
import { GeneService } from '../../service/GeneService';
import { Endpoints } from '../../constants/Endpoints';

export const GenesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [genes, setGenes] = useState([]);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const searchService = new SearchService();
	let geneService = new GeneService();

	const mutation = useMutation({
		mutationFn: (updatedGene) => {
			if (!geneService) {
				geneService = new GeneService();
			}
			return geneService.saveGene(updatedGene);
		},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [synonymsData, setSynonymsData] = useState({
		dialog: false,
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		dialog: false,
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false,
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false,
	});

	const [crossReferencesData, setCrossReferencesData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [gcrpCrossReferenceData, setGcrpCrossReferenceData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [systematicNameData, setSystematicNameData] = useState({
		dialog: false,
	});

	const handleFullNameOpen = (geneFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [geneFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = false;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (geneSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = geneSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = false;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSymbolOpen = (geneSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [geneSymbol];
		_symbolData['dialog'] = true;
		_symbolData['isInEdit'] = false;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleSecondaryIdsOpen = (geneSecondaryIds) => {
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = geneSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = false;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const handleSystematicNameOpen = (geneSystematicName) => {
		let _systematicNameData = {};
		_systematicNameData['originalSystematicNames'] = [geneSystematicName];
		_systematicNameData['dialog'] = true;
		_systematicNameData['isInEdit'] = false;
		setSystematicNameData(() => ({
			..._systematicNameData,
		}));
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = false;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleRelatedNotesOpenInEdit = (event, editorOptions, isInEdit) => {
		setRelatedNotesData(() => ({
			originalRelatedNotes: editorOptions.rowData.relatedNotes,
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleFullNameOpenInEdit = (event, editorOptions, isInEdit) => {
		setFullNameData(() => ({
			originalFullNames: [editorOptions.rowData.geneFullName],
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleSymbolOpenInEdit = (event, editorOptions, isInEdit) => {
		setSymbolData(() => ({
			originalSymbols: [editorOptions.rowData.geneSymbol],
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleSynonymsOpenInEdit = (event, editorOptions, isInEdit) => {
		setSynonymsData(() => ({
			originalSynonyms: editorOptions.rowData.geneSynonyms,
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleSecondaryIdsOpenInEdit = (event, editorOptions, isInEdit) => {
		setSecondaryIdsData(() => ({
			originalSecondaryIds: editorOptions.rowData.geneSecondaryIds,
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleSystematicNameOpenInEdit = (event, editorOptions, isInEdit) => {
		setSystematicNameData(() => ({
			originalFullNames: [editorOptions.rowData.geneSystematicName],
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleCrossReferencesOpenInEdit = (event, editorOptions, isInEdit) => {
		setCrossReferencesData(() => ({
			originalCrossReferences: editorOptions.rowData.crossReferences,
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const handleGcrpCrossReferenceOpenInEdit = (event, editorOptions, isInEdit) => {
		setGcrpCrossReferenceData(() => ({
			originalCrossReferences: editorOptions.rowData.gcrpCrossReference
				? [editorOptions.rowData.gcrpCrossReference]
				: [],
			dialog: true,
			isInEdit: isInEdit,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: editorOptions,
		}));
	};

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				sortable: true,
				filter: true,
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
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
				field: 'geneFullName',
				columnKey: 'geneFullName.displayText',
				header: 'Name',
				sortable: true,
				filter: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.geneFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.geneFullName?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleFullNameOpenInEdit}
						errorField="geneFullName"
						displayHtml={editorOptions.rowData.geneFullName?.displayText}
						addText="Add Full Name"
						tooltipObject="gene"
					/>
				),
				filterConfig: FILTER_CONFIGS.geneNameFilterConfig,
			},
			{
				field: 'geneSymbol',
				columnKey: 'geneSymbol.displayText',
				header: 'Symbol',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.geneSymbol}
						handleOpen={handleSymbolOpen}
						text={rowData.geneSymbol?.displayText}
						underline={false}
					/>
				),
				filter: true,
				filterConfig: FILTER_CONFIGS.geneSymbolFilterConfig,
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleSymbolOpenInEdit}
						errorField="geneSymbol"
						displayHtml={editorOptions.rowData.geneSymbol?.displayText}
						addText="Add Symbol"
						tooltipObject="gene"
					/>
				),
			},
			{
				field: 'geneSynonyms',
				columnKey: 'geneSynonyms.displayText',
				header: 'Synonyms',
				sortable: true,
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.geneSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.geneSynonyms?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSynonymsOpenInEdit}
							errorField="geneSynonyms"
							displayText={count ? `Synonyms(${count}) ` : null}
							addText="Add Synonym"
							tooltipObject="gene"
						/>
					);
				},
				filterConfig: FILTER_CONFIGS.geneSynonymsFilterConfig,
			},
			{
				field: 'geneSecondaryIds',
				columnKey: 'geneSecondaryIds.secondaryId',
				header: 'Secondary IDs',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.geneSecondaryIds}
						handleOpen={handleSecondaryIdsOpen}
						getTextField={(entity) => entity?.secondaryId}
						underline={false}
					/>
				),
				sortable: true,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.geneSecondaryIds?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSecondaryIdsOpenInEdit}
							errorField="geneSecondaryIds"
							displayText={count ? `Secondary IDs(${count}) ` : null}
							addText="Add Secondary ID"
							tooltipObject="gene"
						/>
					);
				},
				filterConfig: FILTER_CONFIGS.geneSecondaryIdsFilterConfig,
			},
			{
				field: 'geneSystematicName',
				columnKey: 'geneSystematicName.displayText',
				header: 'Systematic Name',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.geneSystematicName}
						handleOpen={handleSystematicNameOpen}
						text={rowData.geneSystematicName?.displayText}
						underline={false}
					/>
				),
				filter: true,
				filterConfig: FILTER_CONFIGS.geneSystematicNameFilterConfig,
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleSystematicNameOpenInEdit}
						errorField="geneSystematicName"
						displayHtml={editorOptions.rowData.geneSystematicName?.displayText}
						addText="Add Systematic Name"
						tooltipObject="gene"
					/>
				),
			},
			{
				field: 'geneType',
				columnKey: 'geneType.name',
				header: 'Gene Type',
				body: (rowData) => <OntologyTermTemplate term={rowData.geneType} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneTypeFilterConfig,
				editor: (editorOptions) => <GeneTypeTableEditor editorOptions={editorOptions} />,
			},
			{
				field: 'taxon',
				columnKey: 'taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
				editor: (editorOptions) => <TaxonTableEditor editorOptions={editorOptions} />,
			},
			{
				field: 'relatedNotes',
				columnKey: 'relatedNotes.freeText',
				header: 'Related Notes',
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.relatedNotes?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleRelatedNotesOpenInEdit}
							errorField="relatedNotes"
							displayText={count ? `Notes(${count}) ` : null}
							addText="Add Note"
							tooltipObject="gene"
						/>
					);
				},
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneDataProviderFilterConfig,
			},
			{
				field: 'crossReferences',
				columnKey: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: true,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.crossReferences?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleCrossReferencesOpenInEdit}
							errorField="crossReferences"
							displayText={count ? `Cross References(${count}) ` : null}
							addText="Add Cross Reference"
							tooltipObject="gene"
						/>
					);
				},
			},
			{
				field: 'gcrpCrossReference',
				columnKey: 'gcrpCrossReference.displayName',
				header: 'GCRP Cross Reference',
				sortable: true,
				filterConfig: FILTER_CONFIGS.gcrpCrossReferenceFilterConfig,
				body: (rowData) => <CrossReferenceTemplate xref={rowData.gcrpCrossReference} />,
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleGcrpCrossReferenceOpenInEdit}
						errorField="gcrpCrossReference"
						displayText={editorOptions.rowData.gcrpCrossReference?.displayName}
						addText="Add GCRP Cross Reference"
						tooltipObject="gene"
					/>
				),
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
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field={'internal'} />,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: true,
				editor: (editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field={'obsolete'} />,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[errorMessagesRef]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.GENE;

	const initialTableState = useMemo(() => getDefaultTableState('Genes', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setGenes,
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
					endpoint={SEARCH_ENDPOINT}
					tableName="Genes"
					entities={genes}
					setEntities={setGenes}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameEditDialog
				field="geneFullName"
				endpoint={Endpoints.SlotAnnotation.GENE_FULL_NAME}
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SymbolEditDialog
				field="geneSymbol"
				endpoint={Endpoints.SlotAnnotation.GENE_SYMBOL}
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<SynonymsEditDialog
				field="geneSynonyms"
				endpoint={Endpoints.SlotAnnotation.GENE_SYNONYM}
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<SecondaryIdsEditDialog
				field="geneSecondaryIds"
				endpoint={Endpoints.SlotAnnotation.GENE_SECONDARY_ID}
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SecondaryIdsReadOnlyDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
			/>
			<FullNameEditDialog
				field="geneSystematicName"
				endpoint={Endpoints.SlotAnnotation.GENE_SYSTEMATIC_NAME}
				title="Systematic Name"
				nameTypeVocabularyTermSet="systematic_name_type"
				originalFullNameData={systematicNameData}
				setOriginalFullNameData={setSystematicNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SystematicNameDialog
				originalSystematicNameData={systematicNameData}
				setOriginalSystematicNameData={setSystematicNameData}
			/>
			<CrossReferencesEditDialog
				originalCrossReferencesData={crossReferencesData}
				setOriginalCrossReferencesData={setCrossReferencesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<CrossReferencesEditDialog
				field="gcrpCrossReference"
				title="GCRP Cross Reference"
				single
				originalCrossReferencesData={gcrpCrossReferenceData}
				setOriginalCrossReferencesData={setGcrpCrossReferenceData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="gene_note_type"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
