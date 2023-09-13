import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dropdown } from 'primereact/dropdown';
import { FMSService } from '../service/FMSService';

export const FMSComponent = () => {

	const [dataFiles, setDataFiles] = useState(null);
	const [snapShotDate, setSnapShotDate] = useState(0);
	const [first, setFirst] = useState(0);
	const [rows, setRows] = useState(20);
	const [releases, setReleases] = useState(null);
	const [selectedRelease, setSelectedRelease] = useState({releaseVersion: '0'});

	useEffect(() => {
		const fmsService = new FMSService();
		
		if (selectedRelease.releaseVersion !== '0') {
			fmsService.getReleases().then(results => {
				setReleases(results.sort(function(a,b){
					return new Date(b.releaseDate) - new Date(a.releaseDate);
				  }));
				for(let idx of results) {
					if(idx.releaseVersion === selectedRelease.releaseVersion) {
						setSelectedRelease(idx);
						break;
					}
				}
			});
		} else {
			fmsService.getNextRelease().then(result => {
				setSelectedRelease(result);
			});
		}

		if (selectedRelease.releaseVersion !== '0') {
			fmsService.getSnapshot(selectedRelease.releaseVersion).then(snapShot => {
				if (typeof snapShot !== 'undefined') {
					setDataFiles(snapShot.dataFiles);
					setSnapShotDate(snapShot.snapShotDate);
				} else {
					setDataFiles([]);
					setSnapShotDate('');
				}
			});
		}

	}, [selectedRelease.releaseVersion]);



	const symbolTemplate = (rowData) => {
		return <a href={rowData.s3Url}>Download</a>
	};

	const uploadTemplate = (rowData) => {
		return <div>{new Date(rowData.uploadDate).toGMTString()}</div>
	};

	const customPage = (event) => {
		setRows(event.rows);
		setFirst(event.first);
	};

	const onReleaseChange = (event) => {
		setSelectedRelease(event.value);
	};

	return (
		<div>
			<div className="card">
				<h2>FMS Data</h2>
				Release Version: <Dropdown value={selectedRelease} options={releases} optionLabel="releaseVersion" placeholder="Choose Release Version" onChange={onReleaseChange} />
				<br />Snapshot Date: {snapShotDate === '' ? 'No snapshot available' : new Date(snapShotDate).toGMTString()}
				<DataTable value={dataFiles} className="p-datatable-sm"
					paginator onPage={customPage} first={first}
					filterDisplay="row"
					sortMode="multiple" removableSort
					paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
					currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
				>

					<Column showFilterMenu={false} field="md5Sum" header="MD5" sortable filter></Column>
					<Column showFilterMenu={false} field="uploadDate" header="Uploaded" body={uploadTemplate} sortable filter></Column>
					<Column showFilterMenu={false} field="dataType.name" header="Data Type" sortable filter></Column>
					<Column showFilterMenu={false} field="dataSubType.name" header="Data Sub Type" sortable filter></Column>
					<Column showFilterMenu={false} field="s3Url" header="Download" body={symbolTemplate}></Column>
				</DataTable>
			</div>
		</div>
	)
}
