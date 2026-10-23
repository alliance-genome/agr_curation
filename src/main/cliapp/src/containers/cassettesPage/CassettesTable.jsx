import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ComponentsDialog } from '../constructsPage/ComponentsDialog';
import { GenomicComponentsDialog } from '../constructsPage/GenomicComponentsDialog';
import { StrComponentsDialog } from './StrComponentsDialog';
import { SymbolReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SymbolReadOnlyDialog';
import { FullNameReadOnlyDialog } from '../nameSlotAnnotations/dialogs/FullNameReadOnlyDialog';
import { SynonymsReadOnlyDialog } from '../nameSlotAnnotations/dialogs/SynonymsReadOnlyDialog';
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
import { Endpoints } from '../../constants/Endpoints';
import {
	relationName,
	genomicEntityLabel,
	transgenicToolLabel,
	transgenicToolDisplayValue,
	useStrings,
	strAssociationText,
} from './utils';

// tableName is the title, and without spaces the key the table's settings are saved under, so a
// table embedded elsewhere (the cassettes of one construct) keeps its own columns, filters and page.
// fixedFilters restrict every search, see useGetTableData.
export const CassettesTable = ({ tableName = 'Cassettes', fixedFilters }) => {
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

	const [componentsData, setComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [genomicEntityComponentsData, setGenomicEntityComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [transgenicToolComponentsData, setTransgenicToolComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [strComponentsData, setStrComponentsData] = useState({ dialog: false });

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [cassettes, setCassettes] = useState([]);

	const searchService = new SearchService();

	const handleFullNameOpen = (cassetteFullName) => {
		setFullNameData({ originalFullNames: [cassetteFullName], dialog: true });
	};

	const handleSynonymsOpen = (cassetteSynonyms) => {
		setSynonymsData({ originalSynonyms: cassetteSynonyms, dialog: true });
	};

	const handleSymbolOpen = (cassetteSymbol) => {
		setSymbolData({ originalSymbols: [cassetteSymbol], dialog: true });
	};

	const handleComponentsOpen = (cassetteComponents) => {
		setComponentsData({ originalComponents: cassetteComponents, dialog: true });
	};

	const handleGenomicEntityComponentsOpen = (cassetteGenomicEntityAssociations) => {
		setGenomicEntityComponentsData({ originalComponents: cassetteGenomicEntityAssociations, dialog: true });
	};

	const handleStrComponentsOpen = (cassetteStrAssociations) => {
		setStrComponentsData({ originalComponents: cassetteStrAssociations, dialog: true });
	};

	const handleTransgenicToolComponentsOpen = (cassetteTransgenicToolAssociations) => {
		setTransgenicToolComponentsData({ originalComponents: cassetteTransgenicToolAssociations, dialog: true });
	};

	const getComponentsTextString = (item) => relationName(item) + ': ' + item.componentSymbol;

	const getTransgenicToolComponentsTextString = (association) =>
		relationName(association) + ': ' + transgenicToolLabel(association?.cassetteTransgenicToolAssociationObject);

	const getGenomicEntityComponentsTextString = (association) =>
		relationName(association) + ': ' + genomicEntityLabel(association?.cassetteGenomicEntityAssociationObject);

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
				field: 'cassetteSymbol.displayText',
				header: 'Symbol',
				sortable: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.cassetteSymbol}
						handleOpen={handleSymbolOpen}
						text={rowData.cassetteSymbol?.displayText}
						underline={false}
					/>
				),
				filter: true,
				filterConfig: FILTER_CONFIGS.cassetteSymbolFilterConfig,
			},
			{
				field: 'cassetteFullName.displayText',
				header: 'Name',
				sortable: true,
				filter: true,
				body: (rowData) => (
					<TextDialogTemplate
						entity={rowData.cassetteFullName}
						handleOpen={handleFullNameOpen}
						text={rowData.cassetteFullName?.displayText}
						underline={false}
					/>
				),
				filterConfig: FILTER_CONFIGS.cassetteNameFilterConfig,
			},
			{
				field: 'cassetteSynonyms.displayText',
				header: 'Synonyms',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.cassetteSynonyms}
						handleOpen={handleSynonymsOpen}
						getTextField={(entity) => entity?.displayText}
						underline={false}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.cassetteSynonymsFilterConfig,
			},
			{
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: true,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
			},
			{
				field: 'cassetteComponents.componentSymbol',
				header: 'Free Text Components',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.cassetteComponents}
						handleOpen={handleComponentsOpen}
						getTextField={getComponentsTextString}
						underline={true}
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteComponentsFilterConfig,
			},
			{
				field: 'cassetteGenomicEntityAssociations.cassetteGenomicEntityAssociationObject.symbol',
				header: 'Genomic Entity Components',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.cassetteGenomicEntityAssociations}
						handleOpen={handleGenomicEntityComponentsOpen}
						getTextField={getGenomicEntityComponentsTextString}
						underline={true}
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteGenomicEntityAssociationsFilterConfig,
			},
			{
				field: 'cassetteTransgenicToolAssociations.cassetteTransgenicToolAssociationObject.primaryExternalId',
				header: 'Transgenic Tool Components',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.cassetteTransgenicToolAssociations}
						handleOpen={handleTransgenicToolComponentsOpen}
						getTextField={getTransgenicToolComponentsTextString}
						underline={true}
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteTransgenicToolAssociationsFilterConfig,
			},
			{
				field: 'cassetteStrAssociations.cassetteStrAssociationObject.primaryExternalId',
				header: 'Sequence Targeting Reagent Components',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.cassetteStrAssociations}
						handleOpen={handleStrComponentsOpen}
						getTextField={strAssociationText}
						underline={true}
					/>
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteStrAssociationsFilterConfig,
			},
			{
				field: 'cassetteUses.uses.name',
				header: 'Uses',
				body: (rowData) => <StringListTemplate list={useStrings(rowData.cassetteUses)} />,
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteUsesFilterConfig,
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'References',
				body: (rowData) => (
					<TruncatedReferencesTemplate references={rowData.references} identifier={rowData.primaryExternalId} />
				),
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			},
			{
				field: 'placeholder',
				header: 'Placeholder',
				body: (rowData) => <BooleanTemplate value={rowData.placeholder} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.placeholderFilterConfig,
				sortable: { isInEditMode },
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: { isInEditMode },
				filterConfig: FILTER_CONFIGS.cassetteDataProviderFilterConfig,
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
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filter: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				sortable: { isInEditMode },
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.CASSETTE;

	const initialTableState = useMemo(
		() => getDefaultTableState(tableName.replace(/\s+/g, ''), columns, DEFAULT_COLUMN_WIDTH),
		[tableName, columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		fixedFilters,
		setIsInEditMode,
		setEntities: setCassettes,
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
					tableName={tableName}
					entities={cassettes}
					setEntities={setCassettes}
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
			<FullNameReadOnlyDialog originalFullNameData={fullNameData} setOriginalFullNameData={setFullNameData} />
			<SymbolReadOnlyDialog originalSymbolData={symbolData} setOriginalSymbolData={setSymbolData} />
			<SynonymsReadOnlyDialog originalSynonymsData={synonymsData} setOriginalSynonymsData={setSynonymsData} />
			<ComponentsDialog
				originalComponentsData={componentsData}
				setOriginalComponentsData={setComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<GenomicComponentsDialog
				originalComponentsData={genomicEntityComponentsData}
				setOriginalComponentsData={setGenomicEntityComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				objectField="cassetteGenomicEntityAssociationObject"
				title="Genomic Entity Components"
			/>
			<GenomicComponentsDialog
				originalComponentsData={transgenicToolComponentsData}
				setOriginalComponentsData={setTransgenicToolComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				objectField="cassetteTransgenicToolAssociationObject"
				title="Transgenic Tool Components"
				getComponentLabel={transgenicToolDisplayValue}
			/>
			<StrComponentsDialog strComponentsData={strComponentsData} setStrComponentsData={setStrComponentsData} />
		</>
	);
};
