import React, { useRef, useState, useMemo } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { InputTextEditor } from '../../components/InputTextEditor';
import { StringListEditor } from '../../components/Editors/StringListEditor';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

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

	const { data: organizationsData } = useQuery({
		queryKey: ['organizations'],
		queryFn: () => searchService.find('organization', 100, 0, {}),
		refetchOnWindowFocus: false,
	});

	const organizations = useMemo(() => organizationsData?.results || [], [organizationsData]);

	const DataProviderDropdownEditor = ({ rowProps }) => {
		const [selectedValue, setSelectedValue] = useState(rowProps.rowData.dataProvider);

		const onShow = () => {
			setSelectedValue(rowProps.rowData.dataProvider);
		};

		const onChange = (e) => {
			setSelectedValue(e.value);
			let updatedEntities = [...rowProps.props.value];
			updatedEntities[rowProps.rowIndex].dataProvider = e.value;
		};

		return (
			<>
				<Dropdown
					ariaLabel="dataProvider"
					value={selectedValue}
					options={organizations}
					optionLabel="abbreviation"
					dataKey="id"
					onShow={onShow}
					onChange={onChange}
					showClear={false}
					placeholder={rowProps.rowData.dataProvider?.abbreviation}
					style={{ width: '100%' }}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField="dataProvider" />
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
				editor: (props) => <DataProviderDropdownEditor rowProps={props} />,
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
		[]
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
