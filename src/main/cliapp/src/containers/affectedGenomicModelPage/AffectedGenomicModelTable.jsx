import React, { useState, useRef, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';

import { SearchService } from '../../service/SearchService';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import { Endpoints } from '../../constants/Endpoints';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { FullNameEditDialog } from '../nameSlotAnnotations/dialogs/FullNameEditDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SynonymsEditDialog } from '../nameSlotAnnotations/dialogs/SynonymsEditDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
import { SecondaryIdsEditDialog } from '../allelesPage/secondaryIds/SecondaryIdsEditDialog';
import { SecondaryIdsReadOnlyDialog } from '../allelesPage/secondaryIds/SecondaryIdsReadOnlyDialog';
import { DialogTriggerEditor } from '../../components/Editors/dialog/DialogTriggerEditor';
import { TaxonTableEditor } from '../../components/Editors/autocomplete/taxon/TaxonTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';

export const AffectedGenomicModelTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;
	const [totalRecords, setTotalRecords] = useState(0);
	const [agms, setAgms] = useState([]);

	const searchService = new SearchService();
	let agmService = new AffectedGenomicModelService();

	const mutation = useMutation({
		mutationFn: (updatedAgm) => {
			if (!agmService) {
				agmService = new AffectedGenomicModelService();
			}
			return agmService.saveAGM(updatedAgm);
		},
	});

	const subtypeTerms = useControlledVocabularyService('agm_subtype');

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [secondaryIdsData, setSecondaryIdsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [synonymsData, setSynonymsData] = useState({
		dialog: false,
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false,
	});

	const handleFullNameOpen = (agmFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [agmFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = false;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleFullNameOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [editorOptions.rowData.agmFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = isInEdit;
		_fullNameData['rowIndex'] = rowIndex;
		_fullNameData['mainRowProps'] = editorOptions;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (agmSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = agmSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = false;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSynonymsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = editorOptions.rowData.agmSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = isInEdit;
		_synonymsData['rowIndex'] = rowIndex;
		_synonymsData['mainRowProps'] = editorOptions;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSecondaryIdsOpen = (agmSecondaryIds) => {
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = agmSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = false;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const handleSecondaryIdsOpenInEdit = (event, editorOptions, isInEdit) => {
		const { rowIndex } = editorOptions;
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = editorOptions.rowData.agmSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = isInEdit;
		_secondaryIdsData['rowIndex'] = rowIndex;
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
				field: 'agmFullName',
				columnKey: 'agmFullName.displayText',
				header: 'Name',
				sortable: true,
				filter: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.agmFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.agmFullName?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => (
					<DialogTriggerEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						onOpenInEdit={handleFullNameOpenInEdit}
						errorField="agmFullName"
						displayHtml={editorOptions.rowData.agmFullName?.displayText}
						addText="Add Full Name"
						tooltipObject="AGM"
					/>
				),
				filterConfig: FILTER_CONFIGS.agmNameFilterConfig,
			},
			{
				field: 'agmSynonyms',
				columnKey: 'agmSynonyms.displayText',
				header: 'Synonyms',
				sortable: true,
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.agmSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.agmSynonyms?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSynonymsOpenInEdit}
							errorField="agmSynonyms"
							displayText={count ? `Synonyms(${count}) ` : null}
							addText="Add Synonym"
							tooltipObject="AGM"
						/>
					);
				},
				filterConfig: FILTER_CONFIGS.agmSynonymsFilterConfig,
			},
			{
				field: 'agmSecondaryIds',
				columnKey: 'agmSecondaryIds.secondaryId',
				header: 'Secondary IDs',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.agmSecondaryIds}
						handleOpen={handleSecondaryIdsOpen}
						getTextField={(entity) => entity?.secondaryId}
					/>
				),
				editor: (editorOptions) => {
					const count = editorOptions.rowData.agmSecondaryIds?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleSecondaryIdsOpenInEdit}
							errorField="agmSecondaryIds"
							displayText={count ? `Secondary IDs(${count}) ` : null}
							addText="Add Secondary ID"
							tooltipObject="AGM"
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmSecondaryIdsFilterConfig,
			},
			{
				field: 'subtype',
				columnKey: 'subtype.name',
				header: 'Sub Type',
				body: (rowData) => <StringTemplate string={rowData.subtype?.name} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.subtypeFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="subtype"
						options={subtypeTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={false}
					/>
				),
			},
			{
				field: 'taxon',
				columnKey: 'taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
				editor: (editorOptions) => <TaxonTableEditor editorOptions={editorOptions} />,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmDataProviderFilterConfig,
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
		[errorMessagesRef, subtypeTerms]
	);

	const DEFAULT_COLUMN_WIDTH = 100 / columns.length;
	const SEARCH_ENDPOINT = Endpoints.Entity.AGM;

	const initialTableState = useMemo(
		() => getDefaultTableState('AffectedGenomicModels', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setAgms,
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
					tableName="Affected Genomic Models"
					entities={agms}
					setEntities={setAgms}
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
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameEditDialog
				name="AGM Name"
				field="agmFullName"
				endpoint={Endpoints.SlotAnnotation.AGM_FULL_NAME}
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SynonymsEditDialog
				name="AGM Synonym"
				field="agmSynonyms"
				endpoint={Endpoints.SlotAnnotation.AGM_SYNONYM}
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<SecondaryIdsEditDialog
				field="agmSecondaryIds"
				endpoint={Endpoints.SlotAnnotation.AGM_SECONDARY_ID}
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SecondaryIdsReadOnlyDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
			/>
		</>
	);
};
