import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { ResourceDescriptorService } from '../../service/ResourceDescriptorService';
import { Endpoints } from '../../constants/Endpoints';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { InputTextEditor } from '../../components/InputTextEditor';
import { StringListTextAreaEditor } from '../../components/Editors/StringListTextAreaEditor';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';

export const ResourceDescriptorsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptors, setResourceDescriptors] = useState();

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let resourceDescriptorService = new ResourceDescriptorService();

	const mutation = useMutation({
		mutationFn: (updatedResourceDescriptor) => {
			if (!resourceDescriptorService) {
				resourceDescriptorService = new ResourceDescriptorService();
			}
			return resourceDescriptorService.saveResourceDescriptor(updatedResourceDescriptor);
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

	const columns = useMemo(
		() => [
			{
				field: 'prefix',
				header: 'Prefix',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.prefix} />,
				filterConfig: FILTER_CONFIGS.prefixFilterConfig,
				editor: (props) => stringEditor(props, 'prefix'),
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (props) => stringEditor(props, 'name'),
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				body: (rowData) => <CommaSeparatedArrayTemplate array={rowData.synonyms} />,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
				editor: (props) => (
					<>
						<StringListTextAreaEditor rowProps={props} fieldName="synonyms" rows={5} />
						<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="synonyms" />
					</>
				),
			},
			{
				field: 'idPattern',
				header: 'ID Pattern',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.idPattern} />,
				filterConfig: FILTER_CONFIGS.idPatternFilterConfig,
				editor: (props) => stringEditor(props, 'idPattern'),
			},
			{
				field: 'idExample',
				header: 'ID Example',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.idExample} />,
				filterConfig: FILTER_CONFIGS.idExampleFilterConfig,
				editor: (props) => stringEditor(props, 'idExample'),
			},
			{
				field: 'defaultUrlTemplate',
				header: 'Default URL Template',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.defaultUrlTemplate} />,
				filterConfig: FILTER_CONFIGS.defaultUrlTemplateFilterConfig,
				editor: (props) => stringEditor(props, 'defaultUrlTemplate'),
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
	const SEARCH_ENDPOINT = Endpoints.Resource.DESCRIPTOR;

	const initialTableState = useMemo(
		() => getDefaultTableState('ResourceDescriptors', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setResourceDescriptors,
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
				tableName="Resource Descriptors"
				entities={resourceDescriptors}
				setEntities={setResourceDescriptors}
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
	);
};
