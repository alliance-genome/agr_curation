import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { VariantService } from '../../service/VariantService';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { DialogTriggerEditor } from '../../components/Editors/dialog/DialogTriggerEditor';
import { TaxonTableEditor } from '../../components/Editors/autocomplete/taxon/TaxonTableEditor';
import { VariantTypeTableEditor } from '../../components/Editors/autocomplete/variantType/VariantTypeTableEditor';
import { SourceGeneralConsequenceTableEditor } from '../../components/Editors/autocomplete/sourceGeneralConsequence/SourceGeneralConsequenceTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';

import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';

import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';

import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';

import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const VariantsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [totalRecords, setTotalRecords] = useState(0);
	const [variants, setVariants] = useState([]);

	const searchService = new SearchService();

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let variantService = new VariantService();

	const mutation = useMutation({
		mutationFn: (updatedVariant) => {
			if (!variantService) {
				variantService = new VariantService();
			}
			return variantService.saveVariant(updatedVariant);
		},
	});

	const variantStatusTerms = useControlledVocabularyService('variant_status');

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
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
				filterConfig: FILTER_CONFIGS.primaryexternalidFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
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
				field: 'variantType',
				columnKey: 'variantType.name',
				header: 'Variant Type',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.variantType} />,
				filterConfig: FILTER_CONFIGS.variantTypeFilterConfig,
				editor: (editorOptions) => (
					<VariantTypeTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'variantStatus',
				columnKey: 'variantStatus.name',
				header: 'Variant Status',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.variantStatus?.name} />,
				filterConfig: FILTER_CONFIGS.variantStatusFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="variantStatus"
						options={variantStatusTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={true}
					/>
				),
			},
			{
				field: 'relatedNotes',
				columnKey: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: true,
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
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
							tooltipObject="variant"
						/>
					);
				},
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'References',
				sortable: true,
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
				body: (rowData) => (
					<TruncatedReferencesTemplate references={rowData.references} identifier={rowData.primaryExternalId} />
				),
			},
			{
				field: 'sourceGeneralConsequence',
				columnKey: 'sourceGeneralConsequence.name',
				header: 'Source General Consequence',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.sourceGeneralConsequence} />,
				filterConfig: FILTER_CONFIGS.sourceGeneralConsequenceFilterConfig,
				editor: (editorOptions) => (
					<SourceGeneralConsequenceTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				sortable: true,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
				body: (rowData) => <StringListTemplate list={rowData.synonyms} />,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.dataProvider?.abbreviation} />,
				filterConfig: FILTER_CONFIGS.variantDataProviderFilterConfig,
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
				filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				filter: true,
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				sortable: true,
				editor: (editorOptions) => (
					<BooleanTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						field={'internal'}
						showClear={false}
					/>
				),
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				filter: true,
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: true,
				editor: (editorOptions) => (
					<BooleanTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						field={'obsolete'}
						showClear={false}
					/>
				),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[errorMessagesRef, variantStatusTerms]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.VARIANT;

	const initialTableState = useMemo(() => getDefaultTableState('Variants', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setVariants,
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
					tableName="Variants"
					entities={variants}
					setEntities={setVariants}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					hasDetails={false}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="variant_note_type"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
