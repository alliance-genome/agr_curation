import React, { useState, useRef, useMemo } from 'react';
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

import { SearchService } from '../../service/SearchService';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { SecondaryIdsDialog } from '../allelesPage/secondaryIds/SecondaryIdsDialog';

export const AffectedGenomicModelTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [agms, setAgms] = useState([]);

	const searchService = new SearchService();

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
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (agmSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = agmSynonyms;
		_synonymsData['dialog'] = true;
		setSynonymsData(() => ({
			..._synonymsData,
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
				filterConfig: FILTER_CONFIGS.primaryexternalidFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
			},
			{
				field: 'agmFullName.displayText',
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
				filterConfig: FILTER_CONFIGS.agmNameFilterConfig,
			},
			{
				field: 'agmSynonyms.displayText',
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
				filterConfig: FILTER_CONFIGS.agmSynonymsFilterConfig,
			},
			{
				field: 'agmSecondaryIds.secondaryId',
				header: 'Secondary IDs',
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.agmSecondaryIds}
						handleOpen={handleSecondaryIdsOpen}
						getTextField={(entity) => entity?.secondaryId}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmSecondaryIdsFilterConfig,
			},
			{
				field: 'subtype.name',
				header: 'Sub Type',
				body: (rowData) => <StringTemplate string={rowData.subtype?.name} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.subtypeFilterConfig,
			},
			{
				field: 'taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
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
				filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
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
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 100 / columns.length;
	const SEARCH_ENDPOINT = 'agm';

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
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameDialog
				name="AGM Name"
				field="agmFullName"
				endpoint="agmfullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
			/>
			<SynonymsDialog
				name="AGM Synonym"
				field="agmSynonyms"
				endpoint="agmsynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
			/>
			<SecondaryIdsDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
