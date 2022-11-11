import React from 'react';
import { Button } from 'primereact/button';

export const StatusTemplate = ({ rowData }) => {
		let styleClass = 'p-button-text p-button-plain';
		if (rowData.curationReportStatus === 'FAILED') { styleClass = "p-button-danger"; }
		if (rowData.status && (
			rowData.curationReportStatus.endsWith('STARTED') ||
			rowData.curationReportStatus.endsWith('RUNNING') ||
			rowData.curationReportStatus.endsWith('PENDING')
		)) { styleClass = "p-button-success"; }

		return (
			<Button label={rowData.curationReportStatus} tooltip={rowData.errorMessage} className={`p-button-rounded ${styleClass}`} />
		);

};
