import React from 'react';
import { CronFields } from '../../components/CronFields';
import { Dropdown } from "primereact/dropdown";
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { useMutation, useQueryClient } from 'react-query';
import { ReportService } from '../../service/ReportService';
import { InputText } from 'primereact/inputtext';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

export const NewReportForm = ({ newReportDialog, setNewReportDialog, groups, newReport, reportDispatch, reportService }) => {

	const queryClient = useQueryClient();

	const mutation = useMutation(report => {
		if (report.id) {
			return getService().updateReport(report);
		} else {
			return getService().createReport(report);
		}
	});

	const onChange = (e) => {
		reportDispatch({
			field: e.target.name,
			value: e.target.value
		});
	};

	const getService = () => {
		if(!reportService) {
			reportService = new ReportService();
		}
		return reportService;
	}

	const hideDialog = () => {
		reportDispatch({ type: "RESET" });
		setNewReportDialog(false);
	};

	const handleSubmit = (event) => {
		event.preventDefault();

		mutation.mutate(newReport, {
			onSuccess: () => {
				queryClient.invalidateQueries('reporttable');
				reportDispatch({ type: "RESET" });
				setNewReportDialog(false);
			},
			onError: () => {
				// lookup group and set 
			}
		});
	};

	const newReportDialogFooter = (
		<>
			<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
			<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
		</>
	);


	return (
		<Dialog visible={newReportDialog} style={{ width: '450px' }} header="Add Report" modal className="p-fluid" footer={newReportDialogFooter} onHide={hideDialog} resizeable >
			<ErrorBoundary>
				<div className='p-justify-center'>
					<form>
						<div className="field">
							<label htmlFor="group">Group Name</label>
							<Dropdown
								id="group"
								options={groups}
								value={newReport.curationReportGroup}
								onChange={onChange}
								placeholder={"Select Group"}
								className='p-col-12'
								name='curationReportGroup'
								optionLabel='name'
								optionValue='id'
							/>
						</div>
						<div className="field">
							<label htmlFor="name">Name</label>
							<InputText
								id="name"
								name="name"
								placeholder={"Name"}
								value={newReport.name}
								onChange={onChange}
							/>
						</div>

						<div className="field">
							<label htmlFor="birtReportFilePath">BIRT Report File Path</label>
							<InputText
								id="birtReportFilePath"
								name="birtReportFilePath"
								placeholder={"BIRT Report File Path (sample_report.rptdesign)"}
								value={newReport.birtReportFilePath}
								onChange={onChange}
							/>
						</div>

						<CronFields
								newItem={newReport}
								onChange={onChange}
						/>
					</form>
				</div>
			</ErrorBoundary>
		</Dialog>
	);
};
