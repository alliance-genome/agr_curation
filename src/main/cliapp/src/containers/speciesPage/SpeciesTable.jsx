import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { StringListTableEditor } from '../../components/Editors/text/StringListTableEditor';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { useOrganizationService } from '../../service/useOrganizationService';
import { useGenomeAssemblyService } from '../../service/useGenomeAssemblyService';

import { SearchService } from '../../service/SearchService';
import { SpeciesService } from '../../service/SpeciesService';
import { Endpoints } from '../../constants/Endpoints';

export const SpeciesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [species, setSpecies] = useState([]);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const searchService = new SearchService();

	let speciesService = new SpeciesService();

	const organizations = useOrganizationService();
	const genomeAssemblies = useGenomeAssemblyService();

	const mutation = useMutation({
		mutationFn: (updatedSpecies) => {
			if (!speciesService) {
				speciesService = new SpeciesService();
			}
			return speciesService.saveSpecies(updatedSpecies);
		},
	});

	const columns = useMemo(
		() => [
			{
				field: 'taxon.curie',
				header: 'Taxon',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesTaxonCurieFilterConfig,
			},
			{
				field: 'fullName',
				header: 'Full Name',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesFullNameFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="fullName" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'displayName',
				header: 'Display Name',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesDisplayNameFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="displayName" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'abbreviation',
				header: 'Abbreviation',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesAbbreviationFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="abbreviation"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'commonNames',
				header: 'Common Names',
				sortable: false,
				filter: true,
				body: (rowData) => <CommaSeparatedArrayTemplate array={rowData.commonNames} />,
				filterConfig: FILTER_CONFIGS.speciesCommonNameFilterConfig,
				editor: (editorOptions) => (
					<StringListTableEditor
						editorOptions={editorOptions}
						field="commonNames"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'dataProvider',
				columnKey: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.dataProvider?.abbreviation} />,
				filterConfig: FILTER_CONFIGS.speciesDataProviderFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="dataProvider"
						options={organizations}
						errorMessagesRef={errorMessagesRef}
						showClear={false}
						dataKey="id"
						placeholderField="abbreviation"
					/>
				),
			},
			{
				field: 'phylogeneticOrder',
				header: 'Phylogenetic Order',
				sortable: true,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="phylogeneticOrder"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'genomeAssembly',
				columnKey: 'genomeAssembly.primaryExternalId',
				header: 'Assembly',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesAssemblyFilterConfig,
				body: (rowData) => <IdTemplate id={rowData.genomeAssembly?.primaryExternalId} />,
				editor: (editorOptions) => {
					const taxonCurie = editorOptions.rowData?.taxon?.curie;
					const filteredAssemblies = genomeAssemblies.filter(
						(assembly) => !taxonCurie || assembly.taxon?.curie === taxonCurie
					);
					return (
						<ControlledVocabularyTableEditor
							editorOptions={editorOptions}
							field="genomeAssembly"
							options={filteredAssemblies}
							errorMessagesRef={errorMessagesRef}
							showClear={true}
							dataKey="id"
							placeholderField="primaryExternalId"
						/>
					);
				},
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[organizations, genomeAssemblies]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Entity.SPECIES;

	const initialTableState = useMemo(() => getDefaultTableState('Species', columns, DEFAULT_COLUMN_WIDTH), [columns]);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setSpecies,
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
					tableName="Species"
					entities={species}
					setEntities={setSpecies}
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
		</>
	);
};
