import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { NameTemplate } from './NameTemplate';
import { TabView, TabPanel } from 'primereact/tabview';
import { DefinitionTemplate } from './DefinitionTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenericDataTree } from '../../components/GenericDataTree';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { Tooltip } from 'primereact/tooltip';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';

export const GeneralOntologyComponent = ({name, endpoint, showNamespace, showAbbreviation, hideDefinition}) => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [ontologies, setOntologies] = useState([]);

	const [activeTabIndex, setActiveTabIndex] = useState(0);

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [];

	const synonymsTemplate = (rowData) => {
		if (rowData?.synonyms && rowData.synonyms.length > 0) {
			const listTemplate = (synonym) => {
				return (
					<EllipsisTableCell>
						<div dangerouslySetInnerHTML={{__html: synonym}}/>
					</EllipsisTableCell>
				)
			};
			return (
				<>
					<div className={`syn${rowData.curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={rowData.synonyms.map(a => a.name).sort()}/>
					</div>
					<Tooltip target={`.syn${rowData.curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={rowData.synonyms.map(a => a.name).sort()}/>
					</Tooltip>
				</>
			);
		}
	};

	const secondaryIdsTemplate = (rowData) => {
		if (rowData?.secondaryIdentifiers && rowData.secondaryIdentifiers.length > 0) {
			const sortedIds = rowData.secondaryIdentifiers.sort();
			const listTemplate = (secondaryId) => {
				return (
					<EllipsisTableCell>
						<div dangerouslySetInnerHTML={{__html: secondaryId}}/>
					</EllipsisTableCell>
				)
			};
			return (
				<>
					<div className={`sid${rowData.curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={sortedIds}/>
					</div>
					<Tooltip target={`.sid${rowData.curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedIds}/>
					</Tooltip>
				</>
			);
		}
	};

	columns.push(
		{
			field: "curie",
			header: "Curie",
			sortable: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig
		}
	);
	columns.push(
		{
			field: "name",
			header: "Name",
			sortable: true,
			body: (rowData) => <NameTemplate rowData={rowData}/>,
			filterConfig: FILTER_CONFIGS.nameFilterConfig
		},
	);
	if(!hideDefinition) {
		columns.push(
			{
				field: "definition",
				header: "Definition",
				sortable: true,
				body: (rowData) => <DefinitionTemplate rowData={rowData} />,
				filterConfig: FILTER_CONFIGS.definitionFilterConfig
			},
		);
	}
	if(showAbbreviation) {
		columns.push(
			{
				field: "abbreviation",
				header: "Abbreviation",
				sortable: true,
				filterConfig: FILTER_CONFIGS.abbreviationFilterConfig
			}
		);
	}
	if(showNamespace) {
		columns.push(
			{
				field: "namespace",
				header: "Name Space",
				sortable: true,
				filterConfig: FILTER_CONFIGS.namespaceFilterConfig
			}
		);
	}
	columns.push(
		{
			field: "synonyms.name",
			header: "Synonyms",
			body: synonymsTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.ontologySynonymsFilterConfig
		}
	);
	columns.push(
		{
			field: "secondaryIdentifiers",
			header: "Secondary IDs",
			sortable: true,
			body: secondaryIdsTemplate,
			filterConfig: FILTER_CONFIGS.secondaryIdsFilterConfig
		}
	);
	columns.push(
		{
			field: "obsolete",
			header: "Obsolete",
			sortable: true,
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig
		}
	);

	const DEFAULT_COLUMN_WIDTH = 17;
	const defaultFilters = {obsoleteFilter: {obsolete: {queryString: "false"}}};

	const initialTableState = getDefaultTableState(name, columns, DEFAULT_COLUMN_WIDTH, defaultFilters);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(initialTableState.tableSettingsKeyName, initialTableState);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: endpoint,
		setIsInEditMode,
		setEntities: setOntologies,
		setTotalRecords,
		toast_topleft,
		searchService
	});

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<TabView activeIndex={activeTabIndex} onTabChange={(e) => setActiveTabIndex(e.index)}>
				<TabPanel header="Table View">
					<GenericDataTable
						endpoint={endpoint}
						tableName={name}
						entities={ontologies}
						setEntities={setOntologies}
						totalRecords={totalRecords}
						setTotalRecords={setTotalRecords}
						tableState={tableState}
						setTableState={setTableState}
						columns={columns}
						dataKey="curie"
						isEditable={false}
						isInEditMode={isInEditMode}
						setIsInEditMode={setIsInEditMode}
						toasts={{toast_topleft, toast_topright }}
						errorObject = {{errorMessages, setErrorMessages}}
						defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
						fetching={isFetching || isLoading}
						defaultFilters = {defaultFilters}
					/>
			    </TabPanel>
			    <TabPanel header="Tree View">
			        <GenericDataTree
						endpoint={endpoint}
						treeName={name}
						toasts={{toast_topleft, toast_topright }}
						errorObject = {{errorMessages, setErrorMessages}}
					/>
			    </TabPanel>
			</TabView>
		</>
	)
}
