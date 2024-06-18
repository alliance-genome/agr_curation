import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';

export const ResourceDescriptorPagesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptorPages, setResourceDescriptorPages] = useState();

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const resourceDescriptorBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>
					{rowData.resourceDescriptor.prefix} ({rowData.resourceDescriptor.name})
				</EllipsisTableCell>
				<Tooltip
					target={`.a${rowData.id}`}
					content={`${rowData.resourceDescriptor.prefix} (${rowData.resourceDescriptor.name})`}
					style={{ width: '450px', maxWidth: '450px' }}
				/>
			</>
		);
	};

	const nameBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.name}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.name} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		);
	};

	const urlTemplateBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${rowData.id}`}>{rowData.urlTemplate}</EllipsisTableCell>
				<Tooltip
					target={`.c${rowData.id}`}
					content={rowData.urlTemplate}
					style={{ width: '450px', maxWidth: '450px' }}
				/>
			</>
		);
	};

	const pageDescriptionBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`d${rowData.id}`}>{rowData.pageDescription}</EllipsisTableCell>
				<Tooltip
					target={`.d${rowData.id}`}
					content={rowData.pageDescription}
					style={{ width: '450px', maxWidth: '450px' }}
				/>
			</>
		);
	};

	const columns = [
		{
			field: 'resourceDescriptor.prefix',
			header: 'Resource Descriptor',
			sortable: true,
			body: resourceDescriptorBodyTemplate,
			filterConfig: FILTER_CONFIGS.resourceDescriptorFilterConfig,
		},
		{
			field: 'name',
			header: 'Name',
			sortable: true,
			body: nameBodyTemplate,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
		},
		{
			field: 'urlTemplate',
			header: 'URL Template',
			sortable: true,
			body: urlTemplateBodyTemplate,
			filterConfig: FILTER_CONFIGS.urlTemplateFilterConfig,
		},
		{
			field: 'pageDescription',
			header: 'Page Description',
			sortable: true,
			body: pageDescriptionBodyTemplate,
			filterConfig: FILTER_CONFIGS.pageDescriptionFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = 'resourcedescriptorpage';

	const initialTableState = getDefaultTableState('ResourceDescriptorPages', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setResourceDescriptorPages,
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
				entities={resourceDescriptorPages}
				setEntities={setResourceDescriptorPages}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				tableName="Resource Descriptor Pages"
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
	);
};
