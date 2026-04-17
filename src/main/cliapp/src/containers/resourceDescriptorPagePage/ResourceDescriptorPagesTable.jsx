import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { ResourceDescriptorPageService } from '../../service/ResourceDescriptorPageService';
import { Endpoints } from '../../constants/Endpoints';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { buildAutocompleteFilter, autocompleteSearch, defaultAutocompleteOnChange } from '../../utils/utils';

export const ResourceDescriptorPagesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptorPages, setResourceDescriptorPages] = useState();

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let resourceDescriptorPageService = new ResourceDescriptorPageService();

	const mutation = useMutation({
		mutationFn: (updatedResourceDescriptorPage) => {
			if (!resourceDescriptorPageService) {
				resourceDescriptorPageService = new ResourceDescriptorPageService();
			}
			return resourceDescriptorPageService.saveResourceDescriptorPage(updatedResourceDescriptorPage);
		},
	});

	const resourceDescriptorSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['prefix', 'name'];
		const endpoint = Endpoints.Resource.DESCRIPTOR;
		const filterName = 'resourceDescriptorFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const onResourceDescriptorValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'resourceDescriptor', setFieldValue, 'prefix');
	};

	const resourceDescriptorEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={resourceDescriptorSearch}
					initialValue={props.rowData.resourceDescriptor?.prefix}
					editorOptions={props}
					fieldName="resourceDescriptor"
					subField="prefix"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<div>
							{item.prefix} ({item.name})
						</div>
					)}
					onValueChangeHandler={onResourceDescriptorValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField="resourceDescriptor"
				/>
			</>
		);
	};

	const columns = useMemo(
		() => [
			{
				field: 'resourceDescriptor',
				columnKey: 'resourceDescriptor.prefix',
				header: 'Resource Descriptor',
				sortable: true,
				body: (rowData) => (
					<StringTemplate string={`${rowData.resourceDescriptor?.prefix} (${rowData.resourceDescriptor?.name})`} />
				),
				filterConfig: FILTER_CONFIGS.resourceDescriptorFilterConfig,
				editor: (props) => resourceDescriptorEditorTemplate(props),
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'urlTemplate',
				header: 'URL Template',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.urlTemplate} />,
				filterConfig: FILTER_CONFIGS.urlTemplateFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor editorOptions={editorOptions} field="urlTemplate" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'pageDescription',
				header: 'Page Description',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.pageDescription} />,
				filterConfig: FILTER_CONFIGS.pageDescriptionFilterConfig,
				editor: (editorOptions) => (
					<InputTextTableEditor
						editorOptions={editorOptions}
						field="pageDescription"
						errorMessagesRef={errorMessagesRef}
					/>
				),
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
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = Endpoints.Resource.DESCRIPTOR_PAGE;

	const initialTableState = useMemo(
		() => getDefaultTableState('ResourceDescriptorPages', columns, DEFAULT_COLUMN_WIDTH),
		[columns]
	);

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
	);
};
