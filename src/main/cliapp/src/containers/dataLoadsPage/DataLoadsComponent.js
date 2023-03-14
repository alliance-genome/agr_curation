import React, { useReducer, useRef, useState, useContext } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { FileUpload } from 'primereact/fileupload';
import { Toast } from "primereact/toast";
import { Dialog } from 'primereact/dialog';
import { ProgressBar } from 'primereact/progressbar';

import { useOktaAuth } from '@okta/okta-react';
import { SearchService } from '../../service/SearchService';
import { DataLoadService } from '../../service/DataLoadService';
import { DataSubmissionService } from '../../service/DataSubmissionService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { NewBulkLoadForm } from './NewBulkLoadForm';
import { NewBulkLoadGroupForm } from './NewBulkLoadGroupForm';
import { HistoryDialog } from './HistoryDialog';
import { useQueryClient } from 'react-query';
import { SiteContext } from '../layout/SiteContext';

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

	const { apiVersion } = useContext(SiteContext);

	const [groups, setGroups] = useState({});
	const [runningLoads, setRunningLoads] = useState({});
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
	const dataSubmissionService = new DataSubmissionService();
	const [uploadLoadType, setUploadLoadType] = useState(null);
	const [uploadSubType, setUploadSubType] = useState(null);
	const [uploadConfirmDialog, setUploadConfirmDialog] = useState(false);

	const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, {});

	const queryClient = useQueryClient();

	let dataLoadService = null;
	const toast = useRef(null);

	const handleNewBulkLoadGroupOpen = (event) => {
		setBulkLoadGroupDialog(true);
	};

	const handleNewBulkLoadOpen = (event) => {
		bulkLoadDispatch({ type: "RESET" });
		setBulkLoadDialog(true);
	};

	const loadTypeClasses = new Map([
		["FULL_INGEST", ["GeneDiseaseAnnotationDTO", "AlleleDiseaseAnnotationDTO", "AGMDiseaseAnnotationDTO", "GeneDTO", "AlleleDTO", "AffectedGenomicModelDTO"]],
		["DISEASE_ANNOTATION", ["GeneDiseaseAnnotationDTO", "AlleleDiseaseAnnotationDTO", "AGMDiseaseAnnotationDTO"]],
		["GENE_DISEASE_ANNOTATION", ["GeneDiseaseAnnotationDTO"]],
		["ALLELE_DISEASE_ANNOTATION", ["AlleleDiseaseAnnotationDTO"]],
		["AGM_DISEASE_ANNOTATION", ["AGMDiseaseAnnotationDTO"]],
		["GENE", ["GeneDTO"]],
		["ALLELE", ["AlleleDTO"]],
		["AGM", ["AffectedGenomicModelDTO"]]
		]);

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

			var loc = window.location, new_uri;
			if (loc.protocol === "https:") {
				new_uri = "wss:";
			} else {
				new_uri = "ws:";
			}
			if(process.env.NODE_ENV === 'production') {
				new_uri += "//" + loc.host;
			} else {
				new_uri += "//localhost:8080";
			}

			new_uri += loc.pathname + "processing_events";
			//console.log(new_uri);
			let ws = new WebSocket(new_uri);

			ws.onopen = () => console.log('ws opened');
			ws.onclose = () => console.log('ws closed');

			ws.onmessage = (e, runningLoads) => {
				let processingMessage = JSON.parse(e.data);
				setRunningLoads((prevState) => {
					//console.log(prevState);
					const newState = {...prevState};
					newState[processingMessage.message] = processingMessage;
					//console.log(newState);
					return newState;
				});
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
	};

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

	const showUploadConfirmDialog = (rowData) => {
		setUploadLoadType(rowData.backendBulkLoadType);
		setUploadSubType(rowData.dataType);
		setUploadConfirmDialog(true);
		//setUploadFile(event.files[0]);
	}

	const hideUploadConfirmDialog = () => {
		setUploadLoadType(null);
		setUploadSubType(null);
		setUploadConfirmDialog(false);
	}

	const uploadLoadFile = (event) => {
		let type = uploadLoadType + "_" + uploadSubType;
		let formData = new FormData();
		if(event.files.length > 0) {
			formData.append(type, event.files[0]);
		}
		dataSubmissionService.sendFile(formData);
		toast.current.show({severity: 'info', summary: 'Success', detail: 'File Uploaded'});
		setUploadLoadType(null);
		setUploadSubType(null);
		setUploadConfirmDialog(false);
	}

	const loadFileActionBodyTemplate = (rowData) => {
		let ret = [];
		if(!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
			if (fileWithinSchemaRange(rowData.linkMLSchemaVersion, rowData.loadType)) {
				ret.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoadFile(rowData)} />);
			}
		}
		if(!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
			ret.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteLoadFile(rowData)} />);
		}

		return ret;

	};

	const loadActionBodyTemplate = (rowData) => {
		let ret = [];

		ret.push(<Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => editLoad(rowData)} />);

		if (rowData.type !== 'BulkManualLoad') {
			if (!rowData.bulkloadStatus || rowData.bulkloadStatus === "FINISHED" || rowData.bulkloadStatus === "FAILED" || rowData.bulkloadStatus === "STOPPED") {
				ret.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoad(rowData)} />);
			}
		}else{
			ret.push(<Button key = "fileUpload" icon='pi pi-upload' label='Upload' className="p-button-rounded p-button-info mr-2" onClick={() => showUploadConfirmDialog(rowData)} />)
		}

		if (!rowData.loadFiles || rowData.loadFiles.length === 0) {
			ret.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteLoad(rowData)} />);
		}

		return <div style={{display: 'inline-flex'}}> {ret} </div>;
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

					<Column field="deletedRecords" header="Deletes Completed" />
					<Column field="deleteFailedRecords" header="Deletes Failed" />
					<Column field="totalDeleteRecords" header="Total Deletes" />
					<Column body={historyActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	const fileTable = (load) => {
		let lastLoadedDates = new Map();
		let filesWithoutDates = [];
		let sortedFiles = [];
		if (load.loadFiles) {
			load.loadFiles.forEach(file => {
				if (file.bulkloadStatus === "FINISHED" || file.bulkloadStatus === "STOPPED" || file.bulkloadStatus === "FAILED") {
					if (file.dateLastLoaded) {
						lastLoadedDates.set(file.dateLastLoaded, file);
					} else {
						filesWithoutDates.push(file);	
					}
				} else {
					lastLoadedDates.set(new Date().toISOString(), file);
				}
			});
			Array.from(lastLoadedDates.keys()).sort(function(a,b) {
				const start1 = new Date(a);
				const start2 = new Date(b);
				return start2 - start1;
			}).forEach(date => sortedFiles.push(lastLoadedDates.get(date)));
			
			if (filesWithoutDates.length > 0) {
				filesWithoutDates.forEach(fwd => {sortedFiles.push(fwd)});
			}
		}
		sortedFiles.forEach(file => {file.loadType = load.backendBulkLoadType});
		return (
			<div className="card">
				<DataTable key="fileTable" value={sortedFiles} responsiveLayout="scroll"
					expandedRows={expandedFileRows} onRowToggle={(e) => setExpandedFileRows(e.data)}
					rowExpansionTemplate={historyTable} dataKey="id">
					<Column expander style={{ width: '3em' }} />
					<Column field="md5Sum" header="MD5 Sum" />
					<Column field="fileSize" header="Compressed File Size" />
					<Column field="recordCount" header="Record Count" />
					<Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
					<Column field="linkMLSchemaVersion" header="LinkML Schema Version" />
					<Column field="dateLastLoaded" header="Last Loaded" />
					<Column field="bulkloadStatus" body={bulkloadStatusTemplate} header="Status" />
					<Column body={loadFileActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	const loadTable = (group) => {
		let lastFileLoaded = new Map();
		let loadsWithoutFiles = [];
		group.loads.forEach(load => {
			let runsStarted = [];
			if (load.loadFiles) {
				load.loadFiles.forEach(file => {
					if (file.bulkloadStatus === "FINISHED" || file.bulkloadStatus === "STOPPED" || file.bulkloadStatus === "FAILED") {
						if (file.dateLastLoaded) {
							runsStarted.push(file.dateLastLoaded);
						} else {
							if (file.history) {
								file.history.forEach(history => {
									runsStarted.push(history.loadStarted);
								});
							}
						}
					} else {
						runsStarted.push(new Date().toISOString());
					}
				});
				if (runsStarted.length > 0) {
					let sortedRunsStarted = runsStarted.sort(function(a,b) {
						const start1 = new Date(a);
						const start2 = new Date(b);
						return start2 - start1;
					});
					lastFileLoaded.set(sortedRunsStarted[0], load);
				} else {
					loadsWithoutFiles.push(load);
				}
			} else {
				loadsWithoutFiles.push(load);
			}
		});
		let sortedLoads = [];
		Array.from(lastFileLoaded.keys()).sort(function(a,b) {
			const start1 = new Date(a);
			const start2 = new Date(b);
			return start2 - start1;
		}).forEach(date => sortedLoads.push(lastFileLoaded.get(date)));
		
		if (loadsWithoutFiles.length > 0) {
			loadsWithoutFiles.sort((a, b) => (a.backendBulkLoadType > b.backendBulkLoadType) ? 1 : -1).forEach(lwf => {sortedLoads.push(lwf)});
		}
		return (
			<div className="card">
				<DataTable key="loadTable" value={sortedLoads} responsiveLayout="scroll"
					expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
					rowExpansionTemplate={fileTable} dataKey="id">
					<Column expander style={{ width: '3em' }} />
					<Column body={nameBodyTemplate} header="Load Name" />
					<Column field="type" header="Bulk Load Type" />
					<Column field="backendBulkLoadType" body={backendBulkLoadTypeTemplate} header="Backend Bulk Load Type" />
					{dynamicColumns(sortedLoads)}
					<Column field="status" body={bulkloadStatusTemplate} header="Status" />
					<Column key="loadAction" body={loadActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};

	const getSchemaVersionArray = (map) => {
		if(map) {
			const array = [];
			for(let item in map) {
				array.push({ className: item, schemaVersion: map[item]});
			}
			return array;
		} else {
			return [];
		}
	}

	const fileWithinSchemaRange = (fileVersion, loadType) => {
		if (!fileVersion) return false;
		const classVersions = apiVersion?.agrCurationSchemaVersions;
		if (!classVersions) return false;

		const fileVersionParts = parseVersionString(fileVersion);

		let loadedClasses = [];
		if (loadTypeClasses.has(loadType)) {
			loadedClasses = loadTypeClasses.get(loadType);
		} else {
			console.error("Unrecognized load type " + loadType);
		}

		for (const loadedClass of loadedClasses) {
			const classVersionRange = classVersions[loadedClass];
			if (!classVersionRange) return false;

			let minMaxVersions = classVersionRange.includes("-") ? classVersionRange.split(" - ") : [classVersionRange, classVersionRange];
			if (minMaxVersions.length === 0 || minMaxVersions.length > 2) return false;
			let minMaxVersionParts = [];
			minMaxVersions.forEach((version, ix) => {minMaxVersionParts[ix] = parseVersionString(version)});

			const minVersionParts = minMaxVersionParts[0];
			if (minMaxVersions.length === 1) {
				if (minVersionParts[0] !== fileVersionParts[0] || minVersionParts[1] !== fileVersionParts[1] || minVersionParts[2] !== fileVersionParts[2]) {
					return false;
				}
			}
			const maxVersionParts = minMaxVersionParts[1];
			// check not lower than min version
			if (fileVersionParts[0] < minVersionParts[0]) return false;
			if (fileVersionParts[0] === minVersionParts[0]) {
				if (fileVersionParts[1] < minVersionParts[1]) return false;
				if (fileVersionParts[1] === minVersionParts[1]) {
					if (fileVersionParts[2] < minVersionParts[2]) return false;
				}
			}
			// check not higher than max version
			if (fileVersionParts[0] > maxVersionParts[0]) return false;
			if (fileVersionParts[0] === maxVersionParts[0]) {
				if (fileVersionParts[1] > maxVersionParts[1]) return false;
				if (fileVersionParts[1] === maxVersionParts[1]) {
					if (fileVersionParts[2] > maxVersionParts[2]) return false;
				}
			}
		}
		return true;
	};

	const parseVersionString = (version) => {
		const regexp = /(?<major>\d+)\.(?<minor>\d+)\.?(?<patch>\d*)/;
		const versionParts = version.match(regexp).groups;
		const majorVersion = versionParts.major ? parseInt(versionParts.major) : 0;
		const minorVersion = versionParts.minor ? parseInt(versionParts.minor) : 0;
		const patchVersion = versionParts.patch ? parseInt(versionParts.patch) : 0;

		return [majorVersion, minorVersion, patchVersion];
	}

	const uploadConfirmDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideUploadConfirmDialog} />
				<FileUpload key="uploadConfirm" mode="basic" auto chooseOptions={{icon:'pi pi-check', label: 'Confirm', className:"p-button-text"}}
						accept="*" customUpload uploadHandler={e => uploadLoadFile(e)} maxFileSize={1000000000000000} />
			</React.Fragment>
		);
	}

	const ProgressIndicator = ({ load }) => {
        if(load.currentCount && load.totalSize) {
        	return <ProgressBar value={(parseInt((load.currentCount / load.totalSize) * 10000) / 100)} />
        } else if(load.currentCount && load.lastCount && load.lastTime && load.nowTime) {
        	let rate = Math.ceil(((load.currentCount - load.lastCount) / (load.nowTime - load.lastTime)) * 1000);
			return (
	            <ProgressBar
	            	value={rate}
	            	displayValueTemplate={(value) => {
	            		return <>{value}r/s -- {load.currentCount}</>
	            	}}
	            />
	        );
        } else {
        	return <ProgressBar value={0} />
        }
    }

	const processingLoadsComponents = () => {

		let ret = [];
		//console.log(runningLoads);
		for(let key in runningLoads) {
			//console.log(key);
			if(runningLoads[key]) {
				ret.push(
					<div className="col-2" key={key}>
						<div className="card">
							<h3>{ key }</h3>
							<ProgressIndicator load={runningLoads[key]} />
						</div>
					</div>
				);
			}
		}
		return ret;
	}

	return (
		<>
			<Toast ref={toast}></Toast>
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
				<Dialog visible={uploadConfirmDialog} style={{ width: '450px' }} header="Confirm Upload" modal footer={uploadConfirmDialogFooter} onHide={hideUploadConfirmDialog}>
					<div className="upload-confirmation-content">
						<i className="pi pi-exclamation-triangle mr-3" style={{ fontSize: '2rem'}} />
						{<span>Please confirm that you are submitting a file with LoadType “{uploadLoadType}” and SubType “{uploadSubType}”.</span>}
					</div>
				</Dialog>
			</div>
			<div className="card">
				<h3>Schema Version Table</h3>
				<DataTable key="schemaTable" value={ getSchemaVersionArray(apiVersion?.submittedClassSchemaVersions) } className="p-datatable-sm" dataKey="id">
					<Column header="Class Name" field="className" />
					<Column header="Curation Schema (LinkML) Version" field="schemaVersion"></Column>
				</DataTable>
			</div>
			<div className="card">
				<h3>Data Processing Info Table</h3>
				<div className="grid">{ processingLoadsComponents() }</div>
			</div>
		</>
	);
};
