import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { AlleleService } from '../../service/AlleleService';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { NewAlleleButton } from './NewAlleleButton';
import { MutationTypesEditDialog } from './mutationTypes/MutationTypesEditDialog';
import { MutationTypesReadOnlyDialog } from './mutationTypes/MutationTypesReadOnlyDialog';
import { FunctionalImpactsEditDialog } from './functionalImpacts/FunctionalImpactsEditDialog';
import { FunctionalImpactsReadOnlyDialog } from './functionalImpacts/FunctionalImpactsReadOnlyDialog';
import { InheritanceModesEditDialog } from './inheritanceModes/InheritanceModesEditDialog';
import { InheritanceModesReadOnlyDialog } from './inheritanceModes/InheritanceModesReadOnlyDialog';
import { NomenclatureEventsEditDialog } from './nomenclatureEvents/NomenclatureEventsEditDialog';
import { NomenclatureEventsReadOnlyDialog } from './nomenclatureEvents/NomenclatureEventsReadOnlyDialog';
import { GermlineTransmissionStatusEditDialog } from './germlineTransmissionStatus/GermlineTransmissionStatusEditDialog';
import { GermlineTransmissionStatusReadOnlyDialog } from './germlineTransmissionStatus/GermlineTransmissionStatusReadOnlyDialog';
import { DatabaseStatusEditDialog } from './databaseStatus/DatabaseStatusEditDialog';
import { DatabaseStatusReadOnlyDialog } from './databaseStatus/DatabaseStatusReadOnlyDialog';
import { SymbolEditDialog } from '../nameSlotAnnotations/dialogs/SymbolEditDialog';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameEditDialog } from '../nameSlotAnnotations/dialogs/FullNameEditDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SecondaryIdsEditDialog } from './secondaryIds/SecondaryIdsEditDialog';
import { SecondaryIdsReadOnlyDialog } from './secondaryIds/SecondaryIdsReadOnlyDialog';
import { SynonymsEditDialog } from '../nameSlotAnnotations/dialogs/SynonymsEditDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { DialogTriggerEditor } from '../../components/Editors/dialog/DialogTriggerEditor';
import { TaxonTableEditor } from '../../components/Editors/autocomplete/taxon/TaxonTableEditor';
import { InCollectionTableEditor } from '../../components/Editors/autocomplete/inCollection/InCollectionTableEditor';
import { ReferencesTableEditor } from '../../components/Editors/autocomplete/references/ReferencesTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';

import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { NestedListDialogTemplate } from '../../components/Templates/dialog/NestedListDialogTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';

import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const AllelesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [totalRecords, setTotalRecords] = useState(0);
	const [alleles, setAlleles] = useState([]);

	const searchService = new SearchService();

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [symbolData, setSymbolData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [fullNameData, setFullNameData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [mutationTypesData, setMutationTypesData] = useState({
		mutationTypes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [functionalImpactsData, setFunctionalImpactsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [germlineTransmissionStatusData, setGermlineTransmissionStatusData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [databaseStatusData, setDatabaseStatusData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [inheritanceModesData, setInheritanceModesData] = useState({
		inheritanceModes: {},
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [nomenclatureEventsData, setNomenclatureEventsData] = useState({
		nomenclatureEvents: {},
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [synonymsData, setSynonymsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let alleleService = new AlleleService();

	const mutation = useMutation({
		mutationFn: (updatedAllele) => {
			if (!alleleService) {
				alleleService = new AlleleService();
			}
			return alleleService.saveAllele(updatedAllele);
		},
	});

	const handleRelatedNotesOpen = (relatedNotes) => {
		console.log('relatedNotes', relatedNotes);
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = false;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleRelatedNotesOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = editorOptions.rowData.relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = isInEdit;
		_relatedNotesData['rowIndex'] = index;
		_relatedNotesData['mainRowProps'] = editorOptions;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleSymbolOpen = (alleleSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [alleleSymbol];
		_symbolData['dialog'] = true;
		_symbolData['isInEdit'] = false;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleSymbolOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _symbolData = {};
		_symbolData['originalSymbols'] = [editorOptions.rowData.alleleSymbol];
		_symbolData['dialog'] = true;
		_symbolData['isInEdit'] = isInEdit;
		_symbolData['rowIndex'] = index;
		_symbolData['mainRowProps'] = editorOptions;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleFullNameOpen = (alleleFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [alleleFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = false;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleFullNameOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [editorOptions.rowData.alleleFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = isInEdit;
		_fullNameData['rowIndex'] = index;
		_fullNameData['mainRowProps'] = editorOptions;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (alleleSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = alleleSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = false;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSynonymsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = editorOptions.rowData.alleleSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = isInEdit;
		_synonymsData['rowIndex'] = index;
		_synonymsData['mainRowProps'] = editorOptions;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleInheritanceModesOpen = (alleleInheritanceModes) => {
		let _inheritanceModesData = {};
		_inheritanceModesData['originalInheritanceModes'] = alleleInheritanceModes;
		_inheritanceModesData['dialog'] = true;
		_inheritanceModesData['isInEdit'] = false;
		setInheritanceModesData(() => ({
			..._inheritanceModesData,
		}));
	};

	const handleInheritanceModesOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _inheritanceModesData = {};
		_inheritanceModesData['originalInheritanceModes'] = editorOptions.rowData.alleleInheritanceModes;
		_inheritanceModesData['dialog'] = true;
		_inheritanceModesData['isInEdit'] = isInEdit;
		_inheritanceModesData['rowIndex'] = index;
		_inheritanceModesData['mainRowProps'] = editorOptions;
		setInheritanceModesData(() => ({
			..._inheritanceModesData,
		}));
	};

	const handleGermlineTransmissionStatusOpen = (alleleGermlineTransmissionStatus) => {
		let _germlineTransmissionStatusData = {};
		_germlineTransmissionStatusData['originalGermlineTransmissionStatuses'] = [alleleGermlineTransmissionStatus];
		_germlineTransmissionStatusData['dialog'] = true;
		_germlineTransmissionStatusData['isInEdit'] = false;
		setGermlineTransmissionStatusData(() => ({
			..._germlineTransmissionStatusData,
		}));
	};

	const handleGermlineTransmissionStatusOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _germlineTransmissionStatusData = {};
		_germlineTransmissionStatusData['originalGermlineTransmissionStatuses'] = [
			editorOptions.rowData.alleleGermlineTransmissionStatus,
		];
		_germlineTransmissionStatusData['dialog'] = true;
		_germlineTransmissionStatusData['isInEdit'] = isInEdit;
		_germlineTransmissionStatusData['rowIndex'] = index;
		_germlineTransmissionStatusData['mainRowProps'] = editorOptions;
		setGermlineTransmissionStatusData(() => ({
			..._germlineTransmissionStatusData,
		}));
	};

	const handleNomenclatureEventsOpen = (alleleNomenclatureEvents) => {
		let _nomenclatureEventsData = {};
		_nomenclatureEventsData['originalNomenclatureEvents'] = alleleNomenclatureEvents;
		_nomenclatureEventsData['dialog'] = true;
		_nomenclatureEventsData['isInEdit'] = false;
		setNomenclatureEventsData(() => ({
			..._nomenclatureEventsData,
		}));
	};

	const handleNomenclatureEventsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _nomenclatureEventsData = {};
		_nomenclatureEventsData['originalNomenclatureEvents'] = editorOptions.rowData.alleleNomenclatureEvents;
		_nomenclatureEventsData['dialog'] = true;
		_nomenclatureEventsData['isInEdit'] = isInEdit;
		_nomenclatureEventsData['rowIndex'] = index;
		_nomenclatureEventsData['mainRowProps'] = editorOptions;
		setNomenclatureEventsData(() => ({
			..._nomenclatureEventsData,
		}));
	};

	const handleDatabaseStatusOpen = (alleleDatabaseStatus) => {
		let _databaseStatusData = {};
		_databaseStatusData['originalDatabaseStatuses'] = [alleleDatabaseStatus];
		_databaseStatusData['dialog'] = true;
		_databaseStatusData['isInEdit'] = false;
		setDatabaseStatusData(() => ({
			..._databaseStatusData,
		}));
	};

	const handleDatabaseStatusOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _databaseStatusData = {};
		_databaseStatusData['originalDatabaseStatuses'] = [editorOptions.rowData.alleleDatabaseStatus];
		_databaseStatusData['dialog'] = true;
		_databaseStatusData['isInEdit'] = isInEdit;
		_databaseStatusData['rowIndex'] = index;
		_databaseStatusData['mainRowProps'] = editorOptions;
		setDatabaseStatusData(() => ({
			..._databaseStatusData,
		}));
	};

	const handleMutationTypesOpen = (alleleMutationTypes) => {
		let _mutationTypesData = {};
		_mutationTypesData['originalMutationTypes'] = alleleMutationTypes;
		_mutationTypesData['dialog'] = true;
		_mutationTypesData['isInEdit'] = false;
		setMutationTypesData(() => ({
			..._mutationTypesData,
		}));
	};

	const handleMutationTypesOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _mutationTypesData = {};
		_mutationTypesData['originalMutationTypes'] = editorOptions.rowData.alleleMutationTypes;
		_mutationTypesData['dialog'] = true;
		_mutationTypesData['isInEdit'] = isInEdit;
		_mutationTypesData['rowIndex'] = index;
		_mutationTypesData['mainRowProps'] = editorOptions;
		setMutationTypesData(() => ({
			..._mutationTypesData,
		}));
	};

	const handleFunctionalImpactsOpen = (alleleFunctionalImpacts) => {
		let _functionalImpactsData = {};
		_functionalImpactsData['originalFunctionalImpacts'] = alleleFunctionalImpacts;
		_functionalImpactsData['dialog'] = true;
		_functionalImpactsData['isInEdit'] = false;
		setFunctionalImpactsData(() => ({
			..._functionalImpactsData,
		}));
	};

	const handleFunctionalImpactsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _functionalImpactsData = {};
		_functionalImpactsData['originalFunctionalImpacts'] = editorOptions.rowData.alleleFunctionalImpacts;
		_functionalImpactsData['dialog'] = true;
		_functionalImpactsData['isInEdit'] = isInEdit;
		_functionalImpactsData['rowIndex'] = index;
		_functionalImpactsData['mainRowProps'] = editorOptions;
		setFunctionalImpactsData(() => ({
			..._functionalImpactsData,
		}));
	};

	const handleSecondaryIdsOpen = (alleleSecondaryIds) => {
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = alleleSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = false;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const handleSecondaryIdsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		const index = rowIndex;
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = editorOptions.rowData.alleleSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = isInEdit;
		_secondaryIdsData['rowIndex'] = index;
		_secondaryIdsData['mainRowProps'] = editorOptions;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'primaryExternalId',
				header: 'Primary External ID',
				body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.primaryExternalIdFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.modInternalIdFilterConfig,
			},
			{
				field: 'alleleFullName',
				columnKey: 'alleleFullName.displayText',
				header: 'Name',
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.alleleFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.alleleFullName?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleFullNameOpenInEdit}
						errorField="alleleFullName"
						displayHtml={editorOptions.rowData.alleleFullName?.displayText}
						addText="Add Full Name"
						tooltipObject="allele"
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleNameFilterConfig,
			},
			{
				field: 'alleleSymbol',
				columnKey: 'alleleSymbol.displayText',
				header: 'Symbol',
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.alleleSymbol}
						handleOpen={handleSymbolOpen}
						text={rowData.alleleSymbol?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleSymbolOpenInEdit}
						errorField="alleleSymbol"
						displayHtml={editorOptions.rowData.alleleSymbol?.displayText}
						addText="Add Symbol"
						tooltipObject="allele"
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleSymbolFilterConfig,
			},
			{
				field: 'alleleSynonyms',
				columnKey: 'alleleSynonyms.displayText',
				header: 'Synonyms',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.alleleSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleSynonyms?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSynonymsOpenInEdit}
							errorField="alleleSynonyms"
							displayText={count ? `Synonyms(${count}) ` : null}
							addText="Add Synonym"
							tooltipObject="allele"
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleSynonymsFilterConfig,
			},
			{
				field: 'alleleSecondaryIds',
				columnKey: 'alleleSecondaryIds.secondaryId',
				header: 'Secondary IDs',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.alleleSecondaryIds}
						handleOpen={handleSecondaryIdsOpen}
						getTextField={(entity) => entity?.secondaryId}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleSecondaryIds?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSecondaryIdsOpenInEdit}
							errorField="alleleSecondaryIds"
							displayText={count ? `Secondary IDs(${count}) ` : null}
							addText="Add Secondary ID"
							tooltipObject="allele"
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleSecondaryIdsFilterConfig,
			},
			{
				field: 'alleleNomenclatureEvents',
				columnKey: 'alleleNomenclatureEvents.nomenclatureEvent.name',
				header: 'Nomenclature Events',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.alleleNomenclatureEvents}
						handleOpen={handleNomenclatureEventsOpen}
						getTextField={(entity) => entity?.nomenclatureEvent?.name}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleNomenclatureEventsFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleNomenclatureEvents?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleNomenclatureEventsOpenInEdit}
							errorField="alleleNomenclatureEvents"
							displayText={count ? `Nomenclature Events(${count}) ` : null}
							addText="Add Nomenclature Event"
							tooltipObject="allele"
						/>
					);
				},
			},
			{
				field: 'taxon',
				columnKey: 'taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
				editor: (editorOptions) => (
					<TaxonTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'alleleMutationTypes',
				columnKey: 'alleleMutationTypes.mutationTypes.name',
				header: 'Mutation Types',
				body: (rowData) => (
					<NestedListDialogTemplate
						entities={rowData.alleleMutationTypes}
						subType={'mutationTypes'}
						handleOpen={handleMutationTypesOpen}
						getTextString={(item) => `${item.name} (${item.curie})`}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleMutationTypes?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleMutationTypesOpenInEdit}
							errorField="alleleMutationTypes"
							displayText={count ? `Mutation Types(${count}) ` : null}
							addText="Add Mutation Type"
							tooltipObject="allele"
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleMutationFilterConfig,
			},
			{
				field: 'alleleFunctionalImpacts',
				columnKey: 'alleleFunctionalImpacts.functionalImpacts.name',
				header: 'Functional Impacts',
				body: (rowData) => (
					<NestedListDialogTemplate
						entities={rowData.alleleFunctionalImpacts}
						subType={'functionalImpacts'}
						handleOpen={handleFunctionalImpactsOpen}
						getTextString={(item) => item.name}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleFunctionalImpacts?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleFunctionalImpactsOpenInEdit}
							errorField="alleleFunctionalImpacts"
							displayText={count ? `Functional Impacts(${count}) ` : null}
							addText="Add Functional Impact"
							tooltipObject="allele"
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleFunctionalImpactsFilterConfig,
			},
			{
				field: 'alleleGermlineTransmissionStatus',
				columnKey: 'alleleGermlineTransmissionStatus.germlineTransmissionStatus.name',
				header: 'Germline Transmission Status',
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.alleleGermlineTransmissionStatus}
						handleOpen={handleGermlineTransmissionStatusOpen}
						text={rowData.alleleGermlineTransmissionStatus?.germlineTransmissionStatus?.name}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleGermlineTransmissionStatusOpenInEdit}
						errorField="alleleGermlineTransmissionStatus"
						displayText={editorOptions.rowData.alleleGermlineTransmissionStatus?.germlineTransmissionStatus?.name}
						addText="Add Germline Transmission Status"
						tooltipObject="allele"
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleGermlineTransmissionStatusFilterConfig,
			},
			{
				field: 'alleleDatabaseStatus',
				columnKey: 'alleleDatabaseStatus.databaseStatus.name',
				header: 'Database Status',
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.alleleDatabaseStatus}
						handleOpen={handleDatabaseStatusOpen}
						text={rowData.alleleDatabaseStatus?.databaseStatus?.name}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleDatabaseStatusOpenInEdit}
						errorField="alleleDatabaseStatus"
						displayText={editorOptions.rowData.alleleDatabaseStatus?.databaseStatus?.name}
						addText="Add Database Status"
						tooltipObject="allele"
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleDatabaseStatusFilterConfig,
			},
			{
				field: 'references',
				columnKey: 'references.primaryCrossReferenceCurie',
				header: 'References',
				body: (rowData) => (
					<TruncatedReferencesTemplate
						references={rowData.references}
						identifier={rowData.primaryExternalId}
						detailPage="Allele"
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
				editor: (editorOptions) => (
					<ReferencesTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'alleleInheritanceModes',
				columnKey: 'alleleInheritanceModes.inheritanceMode.name',
				header: 'Inheritance Modes',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.alleleInheritanceModes}
						handleOpen={handleInheritanceModesOpen}
						getTextField={(entity) => entity?.inheritanceMode?.name}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleInheritanceModesFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.alleleInheritanceModes?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleInheritanceModesOpenInEdit}
							errorField="alleleInheritanceModes"
							displayText={count ? `Inheritance Modes(${count}) ` : null}
							addText="Add Inheritance Mode"
							tooltipObject="allele"
						/>
					);
				},
			},
			{
				field: 'inCollection',
				columnKey: 'inCollection.name',
				header: 'In Collection',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.inCollection?.name} />,
				filterConfig: FILTER_CONFIGS.inCollectionFilterConfig,
				editor: (editorOptions) => (
					<InCollectionTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'isExtinct',
				header: 'Is Extinct',
				body: (rowData) => <BooleanTemplate value={rowData.isExtinct} />,
				filterConfig: FILTER_CONFIGS.isExtinctFilterConfig,
				sortable: true,
				editor: (editorOptions) => (
					<BooleanTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						field={'isExtinct'}
						showClear={true}
					/>
				),
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
							tooltipObject="allele"
						/>
					);
				},
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleDataProviderFilterConfig,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: true,
				filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
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
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} field={'internal'} />
				),
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: true,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} field={'obsolete'} />
				),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[errorMessagesRef]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.ALLELE;

	const initialTableState = useMemo(() => getDefaultTableState('Alleles', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isPending } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setAlleles,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<NewAlleleButton disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Alleles"
					headerButtons={headerButtons}
					entities={alleles}
					setEntities={setAlleles}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					hasDetails={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isPending}
				/>
			</div>
			<SymbolEditDialog
				name="Allele Symbol"
				field="alleleSymbol"
				endpoint="allelesymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<FullNameEditDialog
				name="Allele Name"
				field="alleleFullName"
				endpoint="allelefullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SynonymsEditDialog
				name="Allele Synonym"
				field="alleleSynonyms"
				endpoint="allelesynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<NomenclatureEventsEditDialog
				originalNomenclatureEventsData={nomenclatureEventsData}
				setOriginalNomenclatureEventsData={setNomenclatureEventsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<NomenclatureEventsReadOnlyDialog
				originalNomenclatureEventsData={nomenclatureEventsData}
				setOriginalNomenclatureEventsData={setNomenclatureEventsData}
			/>
			<MutationTypesEditDialog
				originalMutationTypesData={mutationTypesData}
				setOriginalMutationTypesData={setMutationTypesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<MutationTypesReadOnlyDialog
				originalMutationTypesData={mutationTypesData}
				setOriginalMutationTypesData={setMutationTypesData}
			/>
			<InheritanceModesEditDialog
				originalInheritanceModesData={inheritanceModesData}
				setOriginalInheritanceModesData={setInheritanceModesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<InheritanceModesReadOnlyDialog
				originalInheritanceModesData={inheritanceModesData}
				setOriginalInheritanceModesData={setInheritanceModesData}
			/>
			<SecondaryIdsEditDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SecondaryIdsReadOnlyDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
			/>
			<FunctionalImpactsEditDialog
				originalFunctionalImpactsData={functionalImpactsData}
				setOriginalFunctionalImpactsData={setFunctionalImpactsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FunctionalImpactsReadOnlyDialog
				originalFunctionalImpactsData={functionalImpactsData}
				setOriginalFunctionalImpactsData={setFunctionalImpactsData}
			/>
			<GermlineTransmissionStatusEditDialog
				originalGermlineTransmissionStatusData={germlineTransmissionStatusData}
				setOriginalGermlineTransmissionStatusData={setGermlineTransmissionStatusData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<GermlineTransmissionStatusReadOnlyDialog
				originalGermlineTransmissionStatusData={germlineTransmissionStatusData}
				setOriginalGermlineTransmissionStatusData={setGermlineTransmissionStatusData}
			/>
			<DatabaseStatusEditDialog
				originalDatabaseStatusData={databaseStatusData}
				setOriginalDatabaseStatusData={setDatabaseStatusData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<DatabaseStatusReadOnlyDialog
				originalDatabaseStatusData={databaseStatusData}
				setOriginalDatabaseStatusData={setDatabaseStatusData}
			/>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="allele_note_type"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
