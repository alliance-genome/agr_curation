import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { ResourceDescriptorService } from '../../service/ResourceDescriptorService';
import { Endpoints } from '../../constants/Endpoints';
import { setNewEntity } from '../../utils/utils';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { InputTextTableEditor } from '../../components/Editors/text/InputTextTableEditor';
import { StringListTextAreaTableEditor } from '../../components/Editors/text/StringListTextAreaTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';

import { NewResourceDescriptorForm } from './NewResourceDescriptorForm';

export const ResourceDescriptorsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptors, setResourceDescriptors] = useState();
	const [newResourceDescriptorDialog, setNewResourceDescriptorDialog] = useState(false);

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

	const columns = useMemo(
		() => [
			{
				field: 'prefix',
				header: 'Prefix',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.prefix} />,
				filterConfig: FILTER_CONFIGS.prefixFilterConfig,
				editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="prefix" />,
			},
			{
				field: 'name',
				header: 'Name',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
				editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="name" />,
			},
			{
				field: 'synonyms',
				header: 'Synonyms',
				body: (rowData) => <CommaSeparatedArrayTemplate array={rowData.synonyms} />,
				filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
				editor: (editorOptions) => (
					<StringListTextAreaTableEditor editorOptions={editorOptions} field="synonyms" rows={5} />
				),
			},
			{
				field: 'idPattern',
				header: 'ID Pattern',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.idPattern} />,
				filterConfig: FILTER_CONFIGS.idPatternFilterConfig,
				editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="idPattern" />,
			},
			{
				field: 'idExample',
				header: 'ID Example',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.idExample} />,
				filterConfig: FILTER_CONFIGS.idExampleFilterConfig,
				editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="idExample" />,
			},
			{
				field: 'defaultUrlTemplate',
				header: 'Default URL Template',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.defaultUrlTemplate} />,
				filterConfig: FILTER_CONFIGS.defaultUrlTemplateFilterConfig,
				editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="defaultUrlTemplate" />,
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
				filter: true,
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
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
			{
				field: 'internal',
				header: 'Internal',
				sortable: true,
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} field="internal" />
				),
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				sortable: true,
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} field="obsolete" />
				),
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

	const handleOpenNewResourceDescriptor = () => {
		setNewResourceDescriptorDialog(true);
	};

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button
					label="New Resource Descriptor"
					icon="pi pi-plus"
					onClick={handleOpenNewResourceDescriptor}
					disabled={disabled}
				/>
				&nbsp;&nbsp;
			</>
		);
	};

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
				headerButtons={headerButtons}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={true}
				deletionMethod={resourceDescriptorService.deleteResourceDescriptor}
				deprecateOption={true}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewResourceDescriptorForm
				newResourceDescriptorDialog={newResourceDescriptorDialog}
				setNewResourceDescriptorDialog={setNewResourceDescriptorDialog}
				setNewResourceDescriptor={(newResourceDescriptor, queryClient) =>
					setNewEntity(tableState, setResourceDescriptors, newResourceDescriptor, queryClient)
				}
			/>
		</div>
	);
};
