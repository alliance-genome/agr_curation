import React, { useState } from 'react';
import { useQuery } from 'react-query';
import Moment from 'react-moment';
import moment from 'moment';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ScrollPanel } from 'primereact/scrollpanel';

export const HistoryDialog = ({ historyDialog, setHistoryDialog, history, dataLoadService }) => {

	const [expandedRows, setExpandedRows] = useState(null);
	const [fullHistory, setFullHistory] = useState({});

	useQuery(['bulkLoadFullHistory', history],
			() => dataLoadService.getFileHistoryFile(history.id), {
			onSuccess: (res) => {
				if(res.data.entity) {
					setFullHistory(res.data.entity);
				}
			},
			onError: (error) => {
				console.log(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false
		}
	);

	const hideDialog = () => {
		setHistoryDialog(false);
		//setFullHistory({});
	};

	const jsonObjectTemplate = (rowData) => {
		if (rowData.exception.jsonObject) {
			return (
				<div className="card">
					<h2>JSON Object</h2>
					<ScrollPanel style={{width: '100%'}}>
						<pre>{JSON.stringify(JSON.parse(rowData.exception.jsonObject), null, 2) }</pre>
					</ScrollPanel>
				</div>
			);
		}
	};

	return (
		<div>
			<Dialog visible={historyDialog} style={{ width: '70vw' }} header="History Information" modal className="p-fluid" onHide={hideDialog}>
				<div className="grid">
					
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Duration</span>
								<div className="text-900 font-medium text-xl"><Moment format="HH:mm:ss" duration={fullHistory.loadStarted} date={fullHistory.loadFinished} /></div>
							</div>
							<span className="text-500">How long the load took</span>
						</div>
					</div>
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Rate</span>
								<div className="text-900 font-medium text-xl">{Math.round(fullHistory.completedRecords / (moment(fullHistory.loadFinished) - moment(fullHistory.loadStarted)) * 10000) / 10} r/s</div>
							</div>
							<span className="text-500">How many records per second to the database</span>
						</div>
					</div>
					<div className="col-12 lg:col-6 xl:col-4">
						<div className="card mb-0">
							<div>
								<span className="block text-500 font-medium mb-3">Completed</span>
								<div className="text-900 font-medium text-xl">{fullHistory.completedRecords} of {fullHistory.totalRecords} = {Math.round(fullHistory.completedRecords / fullHistory.totalRecords * 1000) / 10}%</div>
							</div>
							<span className="text-500">How much of the load was successful</span>
						</div>
					</div>
						
					<div className="card col-12">
						<DataTable key="exceptionTable" value={fullHistory.exceptions} responsiveLayout="scroll"
							expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)} rowExpansionTemplate={jsonObjectTemplate} dataKey="id">
							<Column expander style={{ width: '3em' }} />
							<Column field="exception.message" header={`Exception Messages (${fullHistory.exceptions ? fullHistory.exceptions.length : 0})`} />
						</DataTable>
					</div>

				</div>
			</Dialog>
		</div>
	);
};

