import React, { useReducer, useRef, useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { useOktaAuth } from '@okta/okta-react';
import { SearchService } from '../../service/SearchService';
import { DataLoadService } from '../../service/DataLoadService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { NewBulkLoadForm } from './NewBulkLoadForm';
import { NewBulkLoadGroupForm } from './NewBulkLoadGroupForm';
import { HistoryDialog } from './HistoryDialog';
import { useQueryClient } from 'react-query';

export const DataLoadsComponent = () => {

	const { authState } = useOktaAuth();

	const bulkLoadReducer = (state, action) => {
		switch (action.type) {
			case 'EDIT':
				return { ...action.editBulkLoad };
			case 'RESET':
				return { name: "" };
			default:
				return { ...state, [action.field]: action.value };
		}
	};

	const [groups, setGroups] = useState({});
	const [history, setHistory] = useState({id: 0});
	const [bulkLoadGroupDialog, setBulkLoadGroupDialog] = useState(false);
	const [historyDialog, setHistoryDialog] = useState(false);
	const [bulkLoadDialog, setBulkLoadDialog] = useState(false);
	const [expandedGroupRows, setExpandedGroupRows] = useState(null);
	const [expandedLoadRows, setExpandedLoadRows] = useState(null);
	const [expandedFileRows, setExpandedFileRows] = useState(null);
	const [disableFormFields, setDisableFormFields] = useState(false);
	const errorMessage = useRef(null);
	const searchService = new SearchService();

	const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, {});

	const queryClient = useQueryClient();

	let dataLoadService = null;

	const handleNewBulkLoadGroupOpen = (event) => {
		setBulkLoadGroupDialog(true);
	};

	const handleNewBulkLoadOpen = (event) => {
		bulkLoadDispatch({ type: "RESET" });
		setBulkLoadDialog(true);
	};

	useQuery(['bulkloadtable'],
		() => searchService.find('bulkloadgroup', 100, 0, {}), {
		onSuccess: (data) => {
			if(data.results) {
				for (let group of data.results) {
					if (group.loads) {
						for (let load of group.loads) {
							load.group = group.id;
						}
					}
				}
				setGroups(data.results);
			}
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false
	});

	const getService = () => {
		if(!dataLoadService) {
			dataLoadService = new DataLoadService(authState);
		}
		return dataLoadService;
	}

	const urlTemplate = (rowData) => {
		return <a href={rowData.s3Url}>Download</a>;
	};

	const refresh = () => {
		queryClient.invalidateQueries('bulkloadtable');
	};

	const runLoad = (rowData) => {
		getService().restartLoad(rowData.type, rowData.id).then(response => {
			queryClient.invalidateQueries('bulkloadtable');
		});
	};

	const runLoadFile = (rowData) => {
		getService().restartLoadFile(rowData.id).then(response => {
			queryClient.invalidateQueries('bulkloadtable');
		});
	};

	const editLoad = (rowData) => {
		bulkLoadDispatch({ type: "EDIT", editBulkLoad: rowData });
		setBulkLoadDialog(true);
		setDisableFormFields(true);
	};

	const deleteLoadFile = (rowData) => {
		getService().deleteLoadFile(rowData.id).then(response => {
			queryClient.invalidateQueries('bulkloadtable');
		});
	};

	const deleteLoad = (rowData) => {
		getService().deleteLoad(rowData.type, rowData.id).then(response => {
			queryClient.invalidateQueries('bulkloadtable');
		});
	};

	const deleteGroup = (rowData) => {
		getService().deleteGroup(rowData.id).then(response => {
			queryClient.invalidateQueries('bulkloadtable');
		});
	};

	const showHistory = (rowData) => {
		setHistory(rowData);
		setHistoryDialog(true);
	};


	const historyActionBodyTemplate = (rowData) => {
		return <Button icon="pi pi-search-plus" className="p-button-rounded p-button-info mr-2" onClick={() => showHistory(rowData)} />
	};

	const loadFileActionBodyTemplate = (rowData) => {
		let ret = [];

		if(!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
			ret.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoadFile(rowData)} />);
		}
		if(!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
			ret.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteLoadFile(rowData)} />);
		}

		return ret;

	};

	const loadActionBodyTemplate = (rowData) => {
		let ret = [];

		ret.push(<Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => editLoad(rowData)} />);

		if (!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
			ret.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoad(rowData)} />);
		}

		if (!rowData.loadFiles || rowData.loadFiles.length === 0) {
			ret.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteLoad(rowData)} />);
		}

		return ret;
	};

	const groupActionBodyTemplate = (rowData) => {
		if (!rowData.loads || rowData.loads.length === 0) {
			return (<Button icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteGroup(rowData)} />);
		}
	};

	const nameBodyTemplate = (rowData) => {
		return (
			<React.Fragment>
				{rowData.name}
			</React.Fragment>
		);
	};

	const bulkloadUrlBodyTemplate = (rowData) => {
		if (rowData.bulkloadUrl) {
			return <a href={rowData.bulkloadUrl}>Source Download URL</a>;
		}
	};

	const backendBulkLoadTypeTemplate = (rowData) => {
		if (rowData.backendBulkLoadType === 'ONTOLOGY') {
			return rowData.backendBulkLoadType + "(" + rowData.ontologyType + ")";
		} else {
			return rowData.backendBulkLoadType;
		}
	};

	const scheduleActiveTemplate = (rowData) => {
		return (
			<div>
				{rowData.scheduleActive ? "true" : "false"}
			</div>
		);
	};




	const dynamicColumns = (loads) => {

		let showFMSLoad = false;
		let showURLLoad = false;
		let showManualLoad = false;

		let ret = [];

		if (loads) {
			for (const load of loads) {
				if (load.type === "BulkFMSLoad") showFMSLoad = true;
				if (load.type === "BulkURLLoad") showURLLoad = true;
				if (load.type === "BulkManualLoad") showManualLoad = true;
			}
		}

		if (showFMSLoad || showURLLoad) {
			ret.push(<Column key="scheduleActive" field="scheduleActive" header="Schedule Active" body={scheduleActiveTemplate} />);
			ret.push(<Column key="cronSchedule" field="cronSchedule" header="Cron Schedule" />);
			ret.push(<Column key="nextRun" field="nextRun" header="Next Run" />);
			if (showFMSLoad) {
				ret.push(<Column key="fmsDataType" field="fmsDataType" header="FMS Data Type" />);
				ret.push(<Column key="fmsDataSubType" field="fmsDataSubType" header="FMS Data Sub Type" />);
			}
			if (showURLLoad) {
				ret.push(<Column key="bulkloadUrl" body={bulkloadUrlBodyTemplate} header="Data URL" />);
			}
		}
		if (showManualLoad) {
			ret.push(<Column key="fmsDataType2" field="fmsDataType" header="Load Data Type" />);
		}

		return ret;
	};

	const bulkloadStatusTemplate = (rowData) => {
		let styleClass = 'p-button-text p-button-plain';
		if (rowData.bulkloadStatus === 'FAILED') { styleClass = "p-button-danger"; }
		if (rowData.bulkloadStatus && (
				rowData.bulkloadStatus.endsWith('STARTED') ||
				rowData.bulkloadStatus.endsWith('RUNNING') ||
				rowData.bulkloadStatus.endsWith('PENDING')
			)) { styleClass = "p-button-success"; }

		return (
			<Button label={rowData.bulkloadStatus} tooltip={rowData.errorMessage} className={`p-button-rounded ${styleClass}`} />
		);
	};

	const historyTable = (file) => {
		return (
			<div className="card">
				<DataTable key="historyTable" value={file.history} responsiveLayout="scroll">

					<Column field="loadStarted" header="Load Started" />
					<Column field="loadFinished" header="Load Finished" />
					<Column field="completedRecords" header="Records Completed" />
					<Column field="failedRecords" header="Records Failed" />
					<Column field="totalRecords" header="Total Records" />
					<Column body={historyActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	const fileTable = (load) => {
		return (
			<div className="card">
				<DataTable key="fileTable" value={load.loadFiles} responsiveLayout="scroll"
					expandedRows={expandedFileRows} onRowToggle={(e) => setExpandedFileRows(e.data)}
					rowExpansionTemplate={historyTable} dataKey="id">
					<Column expander style={{ width: '3em' }} />
					<Column field="md5Sum" header="MD5 Sum" />
					<Column field="fileSize" header="Compressed File Size" />
					<Column field="recordCount" header="Record Count" />
					<Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
					<Column field="dateUpdated" header="Last Loaded" />
					<Column field="bulkloadStatus" body={bulkloadStatusTemplate} header="Status" />
					<Column body={loadFileActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	const loadTable = (group) => {
		return (
			<div className="card">
				<DataTable key="loadTable" value={group.loads} responsiveLayout="scroll"
					expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
					rowExpansionTemplate={fileTable} dataKey="id">
					<Column expander style={{ width: '3em' }} />
					<Column body={nameBodyTemplate} header="Load Name" />
					<Column field="type" header="Bulk Load Type" />
					<Column field="backendBulkLoadType" body={backendBulkLoadTypeTemplate} header="Backend Bulk Load Type" />
					{dynamicColumns(group.loads)}
					<Column field="status" body={bulkloadStatusTemplate} header="Status" />
					<Column key="loadAction" body={loadActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	return (
		<div className="card">
			<Button label="New Group" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewBulkLoadGroupOpen} />
			<Button label="New Bulk Load" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewBulkLoadOpen} />
			<Button label="Refresh Data" icon="pi pi-plus" className="p-button-success mr-2" onClick={refresh} />
			<h3>Data Loads Table</h3>
			<Messages ref={errorMessage} />
			<DataTable key="groupTable"
				value={groups} className="p-datatable-sm"
				expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
				rowExpansionTemplate={loadTable} dataKey="id">
				<Column expander style={{ width: '3em' }} />
				<Column body={nameBodyTemplate} header="Group Name" />
				<Column body={groupActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
			</DataTable>
			<NewBulkLoadForm
				bulkLoadDialog={bulkLoadDialog}
				setBulkLoadDialog={setBulkLoadDialog}
				newBulkLoad={newBulkLoad}
				bulkLoadDispatch={bulkLoadDispatch}
				groups={groups}
				disableFormFields={disableFormFields}
				setDisableFormFields={setDisableFormFields}
				dataLoadService={dataLoadService}
			/>
			<NewBulkLoadGroupForm
				bulkLoadGroupDialog={bulkLoadGroupDialog}
				setBulkLoadGroupDialog={setBulkLoadGroupDialog}
			/>
			<HistoryDialog
				historyDialog={historyDialog}
				setHistoryDialog={setHistoryDialog}
				dataLoadService={getService()}
				history={history}
			/>
		</div>
	);
};
