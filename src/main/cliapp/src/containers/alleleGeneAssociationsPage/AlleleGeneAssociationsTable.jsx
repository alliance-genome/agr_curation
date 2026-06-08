import React, { useRef, useState, useMemo } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const AlleleGeneAssociationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const [alleleGeneAssociations, setAlleleGeneAssociations] = useState([]);
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
		'alleleAssociationSubject.alleleSymbol.displayText': [
			'alleleAssociationSubject.alleleSymbol.formatText',
			'alleleAssociationSubject.primaryExternalId',
		],
		'alleleGeneAssociationObject.geneSymbol.displayText': [
			'alleleGeneAssociationObject.geneSymbol.formatText',
			'alleleGeneAssociationObject.primaryExternalId',
		],
	};

	const columns = useMemo(
		() => [
			{
				field: 'alleleAssociationSubject.alleleSymbol.displayText',
				header: 'Allele',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.alleleAssociationSubject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleAssociationSubjectFilterConfig,
			},
			{
				field: 'relation.name',
				header: 'Relation',
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleGeneRelationFilterConfig,
			},
			{
				field: 'alleleGeneAssociationObject.geneSymbol.displayText',
				header: 'Gene',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.alleleGeneAssociationObject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleGeneAssociationObjectFilterConfig,
			},
			{
				field: 'evidenceCode.curie',
				header: 'Evidence Code',
				sortable: true,
				filterConfig: FILTER_CONFIGS.evidenceCodeFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.alleleGeneDataProviderFilterConfig,
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
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: true,
				filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.ALLELE_GENE_ASSOCIATION;
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = useMemo(
		() => getDefaultTableState('AlleleGeneAssociations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters),
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
		setEntities: setAlleleGeneAssociations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<div className="card">
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Allele Gene Associations"
				entities={alleleGeneAssociations}
				setEntities={setAlleleGeneAssociations}
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
