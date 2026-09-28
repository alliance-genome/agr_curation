import React, { useRef, useState, useMemo } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const AgmAlleleAssociationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const [agmAlleleAssociations, setAgmAlleleAssociations] = useState([]);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {
		'agmAssociationSubject.agmFullName.displayText': [
			'agmAssociationSubject.agmFullName.formatText',
			'agmAssociationSubject.primaryExternalId',
		],
		'agmAlleleAssociationObject.alleleSymbol.displayText': [
			'agmAlleleAssociationObject.alleleSymbol.formatText',
			'agmAlleleAssociationObject.primaryExternalId',
		],
		'agmAssociationSubject.taxon.name': ['agmAssociationSubject.primaryExternalId'],
		'agmAssociationSubject.dataProvider.abbreviation': ['agmAssociationSubject.primaryExternalId'],
	};

	const columns = useMemo(
		() => [
			{
				field: 'agmAssociationSubject.taxon.name',
				columnKey: 'agmAssociationSubject.taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.agmAssociationSubject?.taxon} />,
				filterConfig: FILTER_CONFIGS.agmAssociationSubjectTaxonFilterConfig,
			},
			{
				field: 'agmAssociationSubject.agmFullName.displayText',
				header: 'Affected Genomic Model',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.agmAssociationSubject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmAssociationSubjectFilterConfig,
			},
			{
				field: 'relation.name',
				columnKey: 'relation.name',
				header: 'Relation',
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmAlleleRelationFilterConfig,
			},
			{
				field: 'agmAlleleAssociationObject.alleleSymbol.displayText',
				header: 'Allele',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.agmAlleleAssociationObject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmAlleleAssociationObjectFilterConfig,
			},
			{
				field: 'zygosity.name',
				columnKey: 'zygosity.name',
				header: 'Zygosity',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.zygosity} />,
				filterConfig: FILTER_CONFIGS.zygosityFilterConfig,
			},
			{
				field: 'agmAssociationSubject.dataProvider.abbreviation',
				columnKey: 'agmAssociationSubject.dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.agmAlleleDataProviderFilterConfig,
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
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.AGM_ALLELE_ASSOCIATION;
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = useMemo(
		() => getDefaultTableState('AgmAlleleAssociations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters),
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
		setEntities: setAgmAlleleAssociations,
		setTotalRecords,
		toast_topleft,
		searchService,
		sortMapping,
	});

	return (
		<div className="card">
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="AGM Allele Associations"
				entities={agmAlleleAssociations}
				setEntities={setAgmAlleleAssociations}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				toasts={{ toast_topleft, toast_topright }}
				isEditable={false}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				sortMapping={sortMapping}
				errorObject={{ errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages }}
				deletionEnabled={false}
				deprecateOption={false}
				modReset={false}
				duplicationEnabled={false}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
				defaultFilters={defaultFilters}
			/>
		</div>
	);
};
