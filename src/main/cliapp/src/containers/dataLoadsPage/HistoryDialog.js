import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import Moment from 'react-moment';
import moment from 'moment';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ScrollPanel } from 'primereact/scrollpanel';
import { useGetUserSettings } from '../../service/useGetUserSettings';

export const HistoryDialog = ({ historyDialog, setHistoryDialog, history, dataLoadService }) => {
	const [expandedRows, setExpandedRows] = useState(null);
	const [fullHistory, setFullHistory] = useState({});
	const [historyExceptions, setHistoryExceptions] = useState([]);
	const [totalRecords, setTotalRecords] = useState();

	let initialTableState = {
		page: 0,
		first: 0,
		rows: 10,
		isFirst: false,
		tableKeyName: 'FileHistoryException'.replace(/\s+/g, ''), //remove whitespace from tableName
		tableSettingsKeyName: 'FileHistoryException'.replace(/\s+/g, '') + 'TableSettings',
	};
	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	useQuery(['bulkLoadFullHistory', history], () => dataLoadService.getFileHistoryFile(history.id), {
		onSuccess: (res) => {
			if (res.data.entity) {
				setFullHistory(res.data.entity);
			}
		},
		onError: (error) => {
			console.log(error);
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false,
	});

	useQuery(
		['bulkLoadHistoryExceptions', [history, tableState]],
		() => dataLoadService.getHistoryExceptions(history.id, tableState.rows, tableState.page),
		{
			onSuccess: (res) => {
				if (res.data.results) {
					setHistoryExceptions(res.data.results);
				} else {
					setHistoryExceptions([]);
				}
				setTotalRecords(res.data.totalResults);
			},
			onError: (error) => {
				console.log(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);

	const messageTemplate = (row) => {
		let messages;
		if (row.exception.messages) {
			messages = (
				<div>
					{row.exception.messages.map((line) => (
						<div>{line}</div>
					))}
				</div>
			);
		}
		let message;
		if (row.exception.message) {
			message = <div>{row.exception.message}</div>;
		}
		return (
			<div>
				{message}
				{messages}
			</div>
		);
	};

	const hideDialog = () => {
		setHistoryDialog(false);
	};

	const jsonObjectTemplate = (rowData) => {
		if (rowData.exception.jsonObject) {
			return (
				<div className="card">
					<h2>JSON Object</h2>
					<ScrollPanel style={{ width: '100%' }}>
						<pre>{JSON.stringify(JSON.parse(rowData.exception.jsonObject), null, 2)}</pre>
					</ScrollPanel>
				</div>
			);
		}
	};

	const onLazyLoad = (event) => {
		let _tableState = {
			...tableState,

			rows: event.rows,
			page: event.page,
			first: event.first,
		};

		setTableState(_tableState);
	};

	return (
		<div>
			<Dialog
				visible={historyDialog}
				style={{ width: '70vw' }}
				header="History Information"
				modal
				className="p-fluid"
				onHide={hideDialog}
			>
				<div className="grid">
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Duration</span>
								<div className="text-900 font-medium text-xl">
									<Moment format="HH:mm:ss" duration={fullHistory.loadStarted} date={fullHistory.loadFinished} />
								</div>
							</div>
							<span className="text-500">How long the load took</span>
						</div>
					</div>
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Rate</span>
								<div className="text-900 font-medium text-xl">
									{Math.round(
										(fullHistory.completedRecords /
											(moment(fullHistory.loadFinished) - moment(fullHistory.loadStarted))) *
											10000
									) / 10}{' '}
									r/s
								</div>
							</div>
							<span className="text-500">How many records per second to the database</span>
						</div>
					</div>
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Completed</span>
								<div className="text-900 font-medium text-xl">
									{fullHistory.completedRecords} of {fullHistory.totalRecords} ={' '}
									{Math.round((fullHistory.completedRecords / fullHistory.totalRecords) * 1000) / 10}%
								</div>
							</div>
							<span className="text-500">How much of the load was successful</span>
						</div>
					</div>

					<div className="card col-12">
						<DataTable
							key="FileHistoryException"
							value={historyExceptions}
							responsiveLayout="scroll"
							expandedRows={expandedRows}
							onRowToggle={(e) => setExpandedRows(e.data)}
							rowExpansionTemplate={jsonObjectTemplate}
							dataKey="id"
							paginator={true}
							paginatorPosition="top"
							totalRecords={totalRecords}
							onPage={onLazyLoad}
							lazy={true}
							first={tableState.first}
							paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
							currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
							rows={tableState.rows}
							rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
						>
							<Column expander style={{ width: '3em' }} />
							<Column body={messageTemplate} header={`Exception Messages (${totalRecords})`} />
						</DataTable>
					</div>
				</div>
			</Dialog>
		</div>
	);
};
