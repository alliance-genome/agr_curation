import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { InputTextEditor } from '../../components/InputTextEditor';
import { StringListEditor } from '../../components/Editors/StringListEditor';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { useOrganizationService } from '../../service/useOrganizationService';

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

	const mutation = useMutation({
		mutationFn: (updatedSpecies) => {
			if (!speciesService) {
				speciesService = new SpeciesService();
			}
			return speciesService.saveSpecies(updatedSpecies);
		},
	});

	const stringEditor = (props, field) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={field} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={field} />
			</>
		);
	};

	const onDataProviderEditorValueChange = (props, event) => {
		let updatedEntities = [...props.props.value];
		updatedEntities[props.rowIndex].dataProvider = event.value;
	};

	const dataProviderEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="dataProvider"
					options={organizations}
					editorChange={onDataProviderEditorValueChange}
					props={props}
					showClear={false}
					dataKey="id"
					placeholderText={props.rowData.dataProvider?.abbreviation}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="dataProvider" />
			</>
		);
	};

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
				editor: (props) => stringEditor(props, 'fullName'),
			},
			{
				field: 'displayName',
				header: 'Display Name',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesDisplayNameFilterConfig,
				editor: (props) => stringEditor(props, 'displayName'),
			},
			{
				field: 'abbreviation',
				header: 'Abbreviation',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesAbbreviationFilterConfig,
				editor: (props) => stringEditor(props, 'abbreviation'),
			},
			{
				field: 'commonNames',
				header: 'Common Names',
				sortable: false,
				filter: true,
				body: (rowData) => <CommaSeparatedArrayTemplate array={rowData.commonNames} />,
				filterConfig: FILTER_CONFIGS.speciesCommonNameFilterConfig,
				editor: (props) => (
					<>
						<StringListEditor rowProps={props} fieldName="commonNames" />
						<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="commonNames" />
					</>
				),
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filter: true,
				filterConfig: FILTER_CONFIGS.speciesDataProviderFilterConfig,
				editor: (props) => dataProviderEditor(props),
			},
			{
				field: 'phylogeneticOrder',
				header: 'Phylogenetic Order',
				sortable: true,
				editor: (props) => stringEditor(props, 'phylogeneticOrder'),
			},
			{
				field: 'assembly_curie',
				header: 'Assembly',
				sortable: false,
				//filterConfig: FILTER_CONFIGS.speciesAssemblyFilterConfig
				editor: (props) => stringEditor(props, 'assembly_curie'),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[organizations]
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
