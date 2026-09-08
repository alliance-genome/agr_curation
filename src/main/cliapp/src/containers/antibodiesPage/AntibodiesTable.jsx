import React, { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { GenomicEntityListTemplate } from '../../components/Templates/genomicEntity/GenomicEntityListTemplate';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';

import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

export const AntibodiesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});

	const [totalRecords, setTotalRecords] = useState(0);
	const [antibodies, setAntibodies] = useState([]);
	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes && rowData.relatedNotes.length > 0) {
			return <StringTemplate string={`Notes (${rowData.relatedNotes.length})`} />;
		}
		return null;
	};

	const columns = useMemo(
		() => [
			{
				field: 'curie',
				header: 'Curie',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.curie} />,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'uniqueId',
				header: 'Unique ID',
				sortable: true,
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			},
			{
				field: 'primaryExternalId',
				header: 'MOD Antibody ID',
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
				field: 'secondaryIdentifiers',
				header: 'Secondary IDs',
				sortable: false,
				body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
				filterConfig: FILTER_CONFIGS.secondaryIdentifiersFilterConfig,
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
			},
			{
				field: 'taxon.name',
				columnKey: 'taxon.name',
				header: 'Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			},
			{
				field: 'antigenTaxon.name',
				columnKey: 'antigenTaxon.name',
				header: 'Antigen Taxon',
				sortable: true,
				body: (rowData) => <OntologyTermTemplate term={rowData.antigenTaxon} />,
				filterConfig: FILTER_CONFIGS.antigenTaxonFilterConfig,
			},
			{
				field: 'clonality.name',
				columnKey: 'clonality.name',
				header: 'Clonality',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.clonality?.name} />,
				filterConfig: FILTER_CONFIGS.clonalityFilterConfig,
			},
			{
				field: 'heavyChainIsotype.name',
				columnKey: 'heavyChainIsotype.name',
				header: 'Heavy Chain Isotype',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.heavyChainIsotype?.name} />,
				filterConfig: FILTER_CONFIGS.heavyChainIsotypeFilterConfig,
			},
			{
				field: 'lightChainIsotype.name',
				columnKey: 'lightChainIsotype.name',
				header: 'Light Chain Isotype',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.lightChainIsotype?.name} />,
				filterConfig: FILTER_CONFIGS.lightChainIsotypeFilterConfig,
			},
			{
				field: 'antibodyTargetGenes.geneSymbol.displayText',
				header: 'Antibody Target Genes',
				sortable: false,
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.antibodyTargetGenes} />,
				filterConfig: FILTER_CONFIGS.antibodyTargetGenesFilterConfig,
			},
			{
				field: 'originalReference.curie',
				header: 'Original Reference',
				sortable: true,
				body: (rowData) => <SingleReferenceTemplate singleReference={rowData.originalReference} />,
				filterConfig: FILTER_CONFIGS.originalReferenceFilterConfig,
			},
			{
				field: 'references.curie',
				header: 'References',
				sortable: false,
				body: (rowData) => <TruncatedReferencesTemplate references={rowData.references} />,
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Related Notes',
				sortable: false,
				body: relatedNotesTemplate,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			},
			{
				field: 'crossReferences.displayName',
				header: 'Cross References',
				sortable: false,
				body: (rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />,
				filterConfig: FILTER_CONFIGS.antibodyCrossReferencesFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				columnKey: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.antibodyDataProviderFilterConfig,
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
				sortable: true,
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				sortable: true,
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 12;
	const SEARCH_ENDPOINT = Endpoints.Entity.ANTIBODY;

	const initialTableState = useMemo(() => getDefaultTableState('Antibodies', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setAntibodies,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Antibodies"
				entities={antibodies}
				setEntities={setAntibodies}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				dataKey="curie"
				isEditable={false}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={false}
				deprecateOption={false}
				modReset={false}
				duplicationEnabled={false}
				fetching={isFetching || isLoading}
			/>
		</div>
	);
};
