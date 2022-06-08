import React, { useReducer, useRef, useState } from 'react';
import { useQuery } from 'react-query';

import { SearchService } from '../../service/SearchService';
import { ReportService } from '../../service/ReportService';
import { Messages } from 'primereact/messages';
import { NewReportForm } from './NewReportForm';
import { NewReportGroupForm } from './NewReportGroupForm';
import { ReportDialog } from './ReportDialog/ReportDialog';
import { useQueryClient } from 'react-query';
import { GroupTable } from './GroupTable';
import { ReportTable } from './ReportTable';
import { TopButtons } from './TopButtons';
import { Toast } from 'primereact/toast';


export const ReportsComponent = () => {

	const reportReducer = (state, action) => {
		switch (action.type) {
			case 'EDIT':
				return { ...action.editReport };
			case 'RESET':
				return { name: "", cronSchedule: "" };
			default:
				return { ...state, [action.field]: action.value };
		}
	};

	const [groups, setGroups] = useState({});
	const [report, setReport] = useState({ id: 0 });
	const [reportGroupDialog, setReportGroupDialog] = useState(false);
	const [reportDialog, setReportDialog] = useState(false);
	const [newReportDialog, setNewReportDialog] = useState(false);

	const toast = useRef(null);
	const errorMessage = useRef(null);
	const searchService = new SearchService();

	const [newReport, reportDispatch] = useReducer(reportReducer, {});

	const queryClient = useQueryClient();

	let reportService = null;

	useQuery(['reporttable'],
		() => searchService.find('curationreportgroup', 100, 0, {}), {
		onSuccess: (data) => {
			if (data.results) {
				for (let group of data.results) {
					if (group.curationReports) {
						for (let report of group.curationReports) {
							report.curationReportGroup = group.id;
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
		if (!reportService) {
			reportService = new ReportService();
		}
		return reportService;
	}

	const reportTable = (group) => {
		return (
			<ReportTable 
				curationReports={group.curationReports} 
				getService={getService} 
				setReport={setReport} 
				setReportDialog={setReportDialog} 
				reportDispatch={reportDispatch} 
				setNewReportDialog={setNewReportDialog} 
				queryClient={queryClient}
				toast={toast}
			/>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast} position="top-right" />
			<TopButtons 
				reportDispatch={reportDispatch}
				setNewReportDialog={setNewReportDialog}
				setReportGroupDialog={setReportGroupDialog}
				queryClient={queryClient}
			/>
			<h3>Reports Table</h3>
			<Messages ref={errorMessage} />
			<GroupTable getService={getService} queryClient={queryClient} groups={groups} reportTable={reportTable}/>
			<NewReportForm
				newReportDialog={newReportDialog}
				setNewReportDialog={setNewReportDialog}
				newReport={newReport}
				reportDispatch={reportDispatch}
				groups={groups}
				reportService={reportService}
			/>
			<NewReportGroupForm
				reportGroupDialog={reportGroupDialog}
				setReportGroupDialog={setReportGroupDialog}
			/>
		 <ReportDialog
				reportDialog={reportDialog}
				setReportDialog={setReportDialog}
				service={getService}
				report={report}
			/> 
		</div>
	);
};
