import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Card } from 'primereact/card';
import { Column } from 'primereact/column';
import { FMSService } from '../service/FMSService';
import { NavLink } from 'react-router-dom';
import { Dropdown } from 'primereact/dropdown';
import { FMSDataFilesComponent } from '../components/FMSDataFilesComponent';
import { Splitter, SplitterPanel } from 'primereact/splitter';

export const FMSDataTypesComponent = (props) => {
	const { dataType } = useParams();
	const [dataTypes, setDataTypes] = useState([]);
	const [dataFiles, setDataFiles] = useState([]);
	const [first, setFirst] = useState(0);
	const [rows, setRows] = useState(20);
	const [latest, setLatest] = useState(false);

	useEffect(() => {
		const fmsService = new FMSService();
		fmsService.getDataTypes().then((results) => {
			setDataTypes(results);
		});
	}, []);

	useEffect(() => {
		if (dataType) {
			const fmsService = new FMSService();
			fmsService.getDataFiles(dataType, latest).then((results) => {
				var _releases = [];
				var lookup = {};

				for (var dataFile of results) {
					for (var releaseVersion of dataFile.releaseVersions) {
						if (!(releaseVersion.releaseVersion in lookup)) {
							lookup[releaseVersion.releaseVersion] = { ...releaseVersion, dataFiles: [] };
						}
						lookup[releaseVersion.releaseVersion].dataFiles.push(dataFile);
					}
				}
				for (var release in lookup) {
					_releases.push(lookup[release]);
				}
				setDataFiles(_releases);
			});
		}
	}, [dataType, latest]);

	const customPage = (event) => {
		setRows(event.rows);
		setFirst(event.first);
	};

	const nameTemplate = (data) => {
		return (
			<NavLink key={data.name} to={'/fmsdatatypes/' + data.name}>
				{data.name}
			</NavLink>
		);
	};

	return (
		<div>
			<Card>
				<h2>FMS Data Types:</h2>
				Latest:{' '}
				<Dropdown
					value={latest}
					options={[
						{ label: 'false', value: false },
						{ label: 'true', value: true },
					]}
					onChange={(e) => setLatest(e.value)}
				/>
				<Splitter style={{ height: '300px' }} className="p-mb-5">
					<SplitterPanel className="p-d-flex p-ai-center p-jc-center">
						<Card title="Data Types">
							<DataTable
								value={dataTypes}
								className="p-datatable-sm"
								paginator
								onPage={customPage}
								first={first}
								filterDisplay="row"
								sortMode="multiple"
								removableSort
								paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
								currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
								rows={rows}
								rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
							>
								<Column
									showFilterMenu={false}
									field="name"
									header="Data Type"
									body={nameTemplate}
									sortable
									filter
								></Column>
								<Column showFilterMenu={false} field="description" header="Description" sortable filter></Column>
								<Column showFilterMenu={false} field="fileExtension" header="File Extension" sortable filter></Column>
								<Column
									showFilterMenu={false}
									field="dataSubTypeRequired"
									header="SubType Required"
									sortable
									filter
								></Column>
								<Column
									showFilterMenu={false}
									field="validationRequired"
									header="Validation Required"
									sortable
									filter
								></Column>
							</DataTable>
						</Card>
					</SplitterPanel>
					<SplitterPanel className="p-d-flex p-ai-center p-jc-center">
						<Card title={'Data Files: ' + (dataType ? dataType : '')}>
							<FMSDataFilesComponent dataFiles={dataFiles} />
						</Card>
					</SplitterPanel>
				</Splitter>
			</Card>
		</div>
	);
};
